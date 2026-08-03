# Unit Status Dashboard

A lightweight, single-page dashboard for tracking unit status across five
categories: Operational Readiness, Training & Professional Development,
Unit Administration, Maintenance & Resource Optimisation, and Soldiers'
Welfare. Pure static HTML/CSS/JS — no server, database, or paid backend
required. Works on low-end devices and slow connections.

All data shipped in this folder is **sample/dummy data** clearly labeled
as such ("Sample Field Artillery Regiment", "Sample Unit — Sub-Unit A",
etc.). Replace it with real data before operational use, following the
notes below.

## Files

```
dashboard/
├── index.html      # page shell — do not need to edit this
├── css/style.css    # styling — do not need to edit this
├── js/app.js         # rendering/logic — do not need to edit this
├── js/data.js         # ← THE DATA FILE — edit this (or use Edit Mode in-app)
└── README.md
```

## Updating the data

There are two ways to update the dashboard. Pick whichever suits the
person responsible for keeping it current.

### Option A — In-page Edit Mode (no file editing, recommended for most users)

1. Open the dashboard in a browser and navigate to the category you want
   to update.
2. Click **Edit Mode** (top right). Fields turn into text boxes and
   status dropdowns (Green / Amber / Red).
3. Make your changes, then click **Save Changes**.
4. Your edits are saved in the browser's local storage on that device
   and will still be there next time you open the page on the *same*
   browser/device.

Because Edit Mode saves to the browser only, it does **not** automatically
sync to other people's phones/computers. To share updates:

- Click **Export JSON** to download the current data as a file.
- Send that file to whoever hosts/maintains the site, and have them
  either:
  - use **Import JSON** on the live site once (loads it into that
    browser), or
  - paste its contents into `js/data.js` and redeploy (this updates the
    data for *everyone*, permanently — see Option B).

Click **Reset Sample Data** at any time to discard local edits and
return to the sample data shipped in `js/data.js`.

### Option B — Edit `js/data.js` directly (updates the data for everyone)

Open `dashboard/js/data.js` in any text editor (Notepad, Notepad++,
VS Code, etc.). It is plain, commented, English-readable data — no
programming knowledge required. For example:

```js
{ name: "120mm Mortars", serviceable: 10, total: 12, status: "amber" }
```

- Change `10` or `12` to update the numbers.
- Change `"amber"` to `"green"` or `"red"` to change the status light.
  (Must be one of exactly these three, in lowercase, with quotes.)
- Keep the punctuation (`{ } [ ] " " , :`) exactly as it is — only change
  the values.

Save the file, then re-upload/redeploy it (or `git push` if hosted from
a repo) so everyone sees the update.

## Hosting (free, static)

No backend, database, or paid hosting is required. Any of the following
work well:

- **GitHub Pages** (recommended if already using GitHub/git):
  1. Push this `dashboard/` folder to a GitHub repository.
  2. In the repo, go to **Settings → Pages**, set the source to the
     branch/folder containing `index.html`.
  3. GitHub gives you a free `https://<user>.github.io/<repo>/` URL.
- **Netlify / Vercel (free tier)**: drag-and-drop the `dashboard/` folder
  onto their web dashboard, or connect the git repo — both offer instant
  free static hosting with a shareable URL.
- **A shared drive / unit intranet**: simply copy the `dashboard/` folder
  to any internal file share or basic web server — opening `index.html`
  works directly in any modern browser, even offline (Edit Mode and
  Print work fully offline; Export/Import work offline too).

No installation, build step, or command line is required — it is just
three files (`index.html`, `css/style.css`, `js/app.js`) plus the data
file.

## Printing / paper briefs

Click **Print / PDF** on any category page to print (or "Save as PDF")
just that category in a clean, high-contrast, print-friendly layout —
useful for paper briefs and files where a screen isn't available.

## Notes for whoever maintains this

- Status values are always one of: `green`, `amber`, `red`.
- The Overview page's five summary cards automatically reflect whatever
  `overall` status is set in each category in `data.js` (or via Edit
  Mode) — you do not need to separately update the Overview.
- No personal, sensitive, or real operational data should be entered
  into a copy of this dashboard hosted anywhere that isn't properly
  access-controlled for internal unit use.
