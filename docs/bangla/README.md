# Claude Plugin — বাংলা টিউটোরিয়াল

*(This document is independent of the unit-accounts material in the rest of this repository.)*

A comprehensive, printable Bangla (বাংলা) tutorial on using **plugins in Claude Code**:
what plugins are, the essential plugins worth installing, step-by-step integration on all
three surfaces (CLI, desktop app, web/cloud), management and scopes, and how to build and
distribute your own plugin.

Explanations are in Bangla; every command, filename and JSON key is kept in English so it
stays copy-pasteable.

| File | |
| --- | --- |
| [`claude-plugin-tutorial.pdf`](claude-plugin-tutorial.pdf) | The printable A4 document (37 pages) |
| [`claude-plugin-tutorial.html`](claude-plugin-tutorial.html) | The same document as self-contained HTML |
| `tools/bangla-tutorial/tutorial.template.html` | Source template (content + print stylesheet) |
| `tools/bangla-tutorial/build-pdf.mjs` | Build script |
| `tools/bangla-tutorial/fonts/` | Noto Sans/Serif Bengali + Noto Sans Mono, under the SIL Open Font License |

## Rebuilding

```bash
cd tools/bangla-tutorial
npm install
npm run build
```

The build inlines the fonts as `data:` URIs, generates the table of contents from the
headings, then prints twice with headless Chromium: once to discover which page each
heading lands on, and once more with those page numbers filled into the contents.

It expects a Chromium binary at the path in `CHROME` at the top of `build-pdf.mjs`.
Adjust that constant for your machine.

Fonts are committed so the build is reproducible offline. To refresh them, download the
`bengali` and `latin` subsets from the Google Fonts CSS for Noto Sans Bengali, Noto Serif
Bengali and Noto Sans Mono, and keep the filenames in `tools/bangla-tutorial/fonts/`.

## Sources

All content is drawn from the official Claude Code documentation at `code.claude.com/docs`
— the `plugins`, `discover-plugins`, `plugins-reference`, `plugin-marketplaces`, `desktop`
and `settings-reference` pages.
