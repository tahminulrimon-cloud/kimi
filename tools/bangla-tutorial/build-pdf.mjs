/**
 * Builds docs/bangla/claude-plugin-tutorial.{html,pdf} from tools/tutorial.template.html.
 *
 *   1. Inline the Noto Bengali/Mono woff2 faces into the template as data: URIs.
 *   2. Generate the table of contents from the h1/h2 headings.
 *   3. Print a draft carrying an invisible ASCII marker inside each heading, read back
 *      which page each marker landed on, then print the final PDF with those page
 *      numbers in the TOC and the markers removed.
 *
 * Why markers: Bengali is a complex script, so the text pdf.js recovers from the page
 * is in glyph order with reordered vowels and split conjuncts — it does not match the
 * source string, and heading titles cannot be searched for directly. The markers are
 * absolutely positioned, so adding and removing them cannot move anything on the page,
 * and the build asserts the page count is identical across both passes.
 */
import { chromium } from 'playwright-core';
import { readFileSync, writeFileSync, mkdirSync } from 'node:fs';
import { resolve, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';

const HERE = dirname(fileURLToPath(import.meta.url));
const REPO = resolve(HERE, '../..');
const TEMPLATE = resolve(HERE, 'tutorial.template.html');
const OUT_HTML = resolve(REPO, 'docs/bangla/claude-plugin-tutorial.html');
const OUT_PDF = resolve(REPO, 'docs/bangla/claude-plugin-tutorial.pdf');
const CHROME = '/opt/pw-browsers/chromium-1194/chrome-linux/chrome';

const BENGALI_RANGE =
  'U+0951-0952, U+0964-0965, U+0980-09FE, U+1CD0, U+1CD2, U+1CD5-1CD6, U+1CD8, ' +
  'U+1CE1, U+1CEA, U+1CED, U+1CF2, U+1CF5-1CF7, U+200C-200D, U+20B9, U+25CC, U+A8F1';
const LATIN_RANGE =
  'U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+0304, ' +
  'U+0308, U+0329, U+2000-206F, U+20AC, U+2122, U+2191, U+2193, U+2212, U+2215, ' +
  'U+FEFF, U+FFFD';

/** Each file is a variable font, so one face covers the whole weight range. */
const FACES = [
  { family: 'Noto Sans Bengali',  file: 'NotoSansBengali-bengali.woff2',  range: BENGALI_RANGE },
  { family: 'Noto Sans Bengali',  file: 'NotoSansBengali-latin.woff2',    range: LATIN_RANGE },
  { family: 'Noto Serif Bengali', file: 'NotoSerifBengali-bengali.woff2', range: BENGALI_RANGE },
  { family: 'Noto Serif Bengali', file: 'NotoSerifBengali-latin.woff2',   range: LATIN_RANGE },
  { family: 'Noto Sans Mono',     file: 'NotoSansMono-latin.woff2',       range: LATIN_RANGE },
];

const b64 = (file) => readFileSync(resolve(HERE, 'fonts', file)).toString('base64');

const fontFace = ({ family, file, range }) => `@font-face{
  font-family:'${family}';
  font-style:normal;
  font-weight:100 900;
  font-display:block;
  src:url(data:font/woff2;base64,${b64(file)}) format('woff2');
  unicode-range:${range};
}`;

/**
 * Chromium drops fully transparent text from the PDF text layer, so the marker is
 * painted white — invisible on the white page, and gone from the final document.
 */
const MARKER_CSS = `<style id="pmark-css">
  h1,h2{position:relative}
  .pmark{position:absolute;left:0;top:0;font-size:1pt;color:#fff;font-family:monospace}
</style>`;

const PDF_OPTS = {
  format: 'A4',
  printBackground: true,
  displayHeaderFooter: true,
  margin: { top: '19mm', bottom: '20mm', left: '17mm', right: '17mm' },
  headerTemplate: '<span></span>',
  footerTemplate: `<style>
      @font-face{font-family:'NSB';font-weight:100 900;
        src:url(data:font/woff2;base64,${b64('NotoSansBengali-bengali.woff2')}) format('woff2');
        unicode-range:${BENGALI_RANGE};}
      #f{font-family:'NSB',sans-serif;font-size:7.5pt;color:#8b909b;width:100%;
         padding:0 17mm;display:flex;justify-content:space-between;align-items:center;}
    </style>
    <div id="f">
      <span>Claude Plugin &mdash; বাংলা টিউটোরিয়াল</span>
      <span>পৃষ্ঠা <span class="pageNumber"></span> / <span class="totalPages"></span></span>
    </div>`,
};

/** Builds the TOC, ids every heading, and drops a locator marker inside each one. */
function buildToc() {
  const heads = [...document.querySelectorAll('h1, h2')].filter((h) => !h.closest('.cover, .toc'));
  const list = document.getElementById('toc-list');
  const entries = [];
  heads.forEach((h, i) => {
    const id = `h-${i}`;
    h.id = id;
    const chno = h.querySelector('.chno');
    const label = chno ? chno.textContent.trim() : '';
    const title = (chno ? h.textContent.slice(chno.textContent.length) : h.textContent).trim();
    const text = h.tagName === 'H1' && label ? `${label} — ${title}` : title;

    const mark = document.createElement('span');
    mark.className = 'pmark';
    mark.textContent = `@@${id}@@`;
    h.appendChild(mark);

    const li = document.createElement('li');
    li.className = h.tagName === 'H1' ? 'lvl1' : 'lvl2';
    li.innerHTML =
      `<a href="#${id}"><span class="t"></span><span class="dots"></span>` +
      `<span class="pg" data-pg="${id}"></span></a>`;
    li.querySelector('.t').textContent = text;
    list.appendChild(li);
    entries.push({ id, text });
  });
  return entries;
}

async function readMarkers(pdfBuffer) {
  const pdfjs = await import('pdfjs-dist/legacy/build/pdf.mjs');
  const doc = await pdfjs.getDocument({ data: new Uint8Array(pdfBuffer) }).promise;
  const pages = {};
  for (let p = 1; p <= doc.numPages; p++) {
    const text = (await (await doc.getPage(p)).getTextContent()).items.map((i) => i.str).join('');
    for (const m of text.matchAll(/@@(h-\d+)@@/g)) if (!(m[1] in pages)) pages[m[1]] = p;
  }
  return { pages, total: doc.numPages };
}

const load = async (page, html) => {
  await page.setContent(html, { waitUntil: 'load' });
  await page.evaluate(() => document.fonts.ready);
};

console.log('· inlining fonts');
const base = readFileSync(TEMPLATE, 'utf8').replace('/* @@FONTS@@ */', FACES.map(fontFace).join('\n'));

const browser = await chromium.launch({ executablePath: CHROME, args: ['--no-sandbox'] });
const page = await browser.newPage();

console.log('· building table of contents');
await load(page, base.replace('</head>', `${MARKER_CSS}\n</head>`));
const entries = await page.evaluate(buildToc);

console.log('· pass 1 — locating headings');
const { pages, total: draftPages } = await readMarkers(await page.pdf(PDF_OPTS));
const missing = entries.filter((e) => !(e.id in pages));
console.log(`  ${entries.length - missing.length}/${entries.length} headings located over ${draftPages} pages`);
missing.forEach((e) => console.warn(`  ! not located: ${e.text}`));

console.log('· pass 2 — final print');
await page.evaluate((nums) => {
  document.getElementById('pmark-css')?.remove();
  document.querySelectorAll('.pmark').forEach((el) => el.remove());
  for (const [id, n] of Object.entries(nums)) {
    const el = document.querySelector(`.pg[data-pg="${id}"]`);
    if (el) el.textContent = String(n);
  }
}, pages);

const finalHtml = await page.content();
const finalPdf = await page.pdf(PDF_OPTS);
await browser.close();

mkdirSync(dirname(OUT_HTML), { recursive: true });
writeFileSync(OUT_HTML, finalHtml);
writeFileSync(OUT_PDF, finalPdf);

const { total: finalPages } = await readMarkers(finalPdf);
const ok = finalPages === draftPages && missing.length === 0;
console.log(`· verify — draft ${draftPages} pages, final ${finalPages} pages: ${ok ? 'consistent' : 'MISMATCH'}`);
console.log(`\n  HTML  ${OUT_HTML}  (${(finalHtml.length / 1024).toFixed(0)} KB)`);
console.log(`  PDF   ${OUT_PDF}  (${(finalPdf.length / 1024).toFixed(0)} KB, ${finalPages} pages)`);
process.exit(ok ? 0 : 1);
