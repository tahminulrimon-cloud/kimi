# Unit Status Dashboard

A lightweight, single-page dashboard for tracking unit status across five
categories: Operational Readiness, Training & Professional Development,
Unit Administration, Maintenance & Resource Optimisation, and Soldiers'
Welfare. Pure static HTML/CSS/JS — no server, database, or paid backend
required. Works on low-end devices and slow connections.

All data shipped in this folder is **sample/dummy data** clearly labeled
as such. Replace values with real data before operational use, following
the notes below.

## Files

```
dashboard/
├── index.html           # page shell — no need to edit this
├── set-password.html    # ← tool for changing the admin password
├── css/style.css        # styling — no need to edit this
├── js/app.js            # rendering/logic — no need to edit this
├── js/config.js         # ← ADMIN LOGIN SETTINGS — username & password
├── js/data.js           # ← THE DATA FILE — edit this (or use Edit Mode in-app)
└── README.md
```

## Admin login

Viewing the dashboard needs no sign-in. **Editing** does.

Default credentials — **change these before real use**:

| Username | Password |
| -------- | -------- |
| `admin`  | `mortar47` |

### Changing the password

1. Open **`set-password.html`** (in this folder) in any browser.
2. Type the username and password you want, then click **Generate**.
3. Copy the two lines it produces into `js/config.js`, replacing the
   existing `ADMIN_USER` and `ADMIN_HASH` lines.
4. Save and re-upload / `git push`.

The password itself is never stored in the files — only a one-way SHA-256
hash of it.

To remove the login entirely and let anyone with the link edit, set
`REQUIRE_LOGIN: false` in `js/config.js`.

> ### ⚠️ How much protection is this really?
>
> This dashboard is a set of plain files with no server behind it. That
> means the password check necessarily happens **inside the viewer's own
> browser**, and someone who knows how to open the page source can bypass
> the prompt and edit anyway.
>
> Treat it as a **lock that keeps honest people out** — like a sign on a
> door — not as real security. Do not put genuinely sensitive or classified
> information on a page that untrusted people can open. The real control is
> **who can reach the URL**: keep it on an access-controlled intranet, a
> private host, or a restricted share.
>
> If you later need proper accounts and enforced permissions, that requires
> a backend service (Supabase and Firebase both have free tiers) — ask and
> it can be added.

## Updating the data

There are two ways to update the dashboard. Pick whichever suits the
person responsible for keeping it current.

### Option A — In-page Edit Mode (no file editing, recommended for most users)

1. Open the dashboard in a browser and navigate to the category you want
   to update.
2. Click **Sign in to edit** (top right) and enter the admin username and
   password. Once signed in the button becomes **Edit Mode** — click it.
   Fields turn into text boxes and status dropdowns (Green / Amber / Red).
3. Make your changes:
   - **Edit** — type over any value, or pick a new status from the dropdown.
   - **Insert** — click the dashed **+ Add …** button at the bottom of any
     list or table (e.g. "+ Add equipment", "+ Add course", "+ Add sub-unit").
     A blank row appears with the cursor already in its first field.
   - **Remove** — click the **✕** at the end of a row. You will be asked to
     confirm.
4. Click **Save Changes**. Nothing is written until you do — **Cancel**
   discards everything, including rows you added or removed.
5. Your edits are saved in the browser's local storage on that device
   and will still be there next time you open the page on the *same*
   browser/device.
6. Click **Sign out** when you are finished. (Signing in only lasts for
   that browser tab's session anyway — closing the browser signs you out.)

Click **Reset Sample Data** at any time to discard local edits and
return to the sample data shipped in `js/data.js`. Import and Reset are
available to signed-in admins only; Export and Print are open to everyone.

## Who sees a correction, and when

This matters — please read it before relying on the dashboard.

**Automatic (no reload needed):** if the same person has the dashboard open
in **several tabs or windows on the same device**, saving in one updates all
the others instantly. A small "Dashboard updated" note confirms it. If you
happen to be mid-edit in another tab, your typing is not overwritten — you
are told a change arrived so you can save or cancel first.

**NOT automatic:** a correction made on the Adjutant's laptop does **not**
appear on someone else's phone. There is no server holding a shared copy —
each device keeps its own. This is the direct trade-off of running with no
backend and no hosting cost.

### Pushing a correction out to everyone

1. On the device with the correct figures, click **Export JSON**.
2. Send that file to whoever maintains the site.
3. They paste its contents into `js/data.js` and redeploy (see Option B).
   Everyone now sees the corrected figures the next time they load the page.

For day-to-day use the practical pattern is: **one nominated person keeps
the master copy** (usually the Adjutant), makes corrections there, and
publishes via step 3. Others view, print, and report changes to them.

> If you want a correction on any device to appear on every device
> automatically, that needs a shared database and a login server. Both
> Supabase and Firebase do this on a free tier — it is a change of
> architecture, not a tweak, so ask if you want it.

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

- **GitHub Pages** (already set up for this repo): a workflow at
  `.github/workflows/pages.yml` automatically deploys the `dashboard/`
  folder to GitHub Pages on every push to the default branch that
  touches `dashboard/`. Live site:
  **https://tahminulrimon-cloud.github.io/kimi/**
  - The first deploy needs GitHub Pages turned on once: **Settings →
    Pages → Build and deployment → Source: GitHub Actions** (the
    workflow will do this automatically on its first successful run on
    most accounts; if the Pages tab still shows "not enabled" after the
    workflow run, flip that dropdown manually — one-time only).
  - To deploy manually/immediately: **Actions → Deploy dashboard to
    GitHub Pages → Run workflow**.
  - For a different repo/host: push the `dashboard/` folder anywhere,
    go to **Settings → Pages**, and set the source to the branch/folder
    containing `index.html`.
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
- The blank row that **+ Add** inserts is defined by `LIST_SCHEMAS` near the
  top of `js/app.js`. If you add a brand-new list to `js/data.js`, add a
  matching entry there so the Add button knows what shape a new row is.
- The Overview page's five summary cards automatically reflect whatever
  `overall` status is set in each category in `data.js` (or via Edit
  Mode) — you do not need to separately update the Overview.
- No personal, sensitive, or real operational data should be entered
  into a copy of this dashboard hosted anywhere that isn't properly
  access-controlled for internal unit use. The admin login is a deterrent,
  not a safeguard — see the warning under **Admin login**.
- The sign-in lasts for the browser session only. Closing the browser
  signs the admin out; there is no "stay signed in".
- The password check needs a modern browser. It works when the page is
  opened over `https://` (e.g. GitHub Pages) and when opened directly from
  a local file. A page served over plain `http://` from a remote server may
  block it — use `https://` for anything hosted.
