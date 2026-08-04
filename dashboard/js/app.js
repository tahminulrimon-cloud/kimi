(function () {
  "use strict";

  var STORAGE_KEY = "regimentDashboardData_v1";

  var SECTIONS = [
    { id: "overview", label: "Overview", icon: "&#8962;" },
    { id: "readiness", label: "Operational Readiness", icon: "&#127919;" },
    { id: "training", label: "Training & Development", icon: "&#127891;" },
    { id: "admin", label: "Unit Administration", icon: "&#128203;" },
    { id: "maintenance", label: "Maintenance & Resources", icon: "&#128295;" },
    { id: "welfare", label: "Welfare & Living Standard", icon: "&#9974;" },
    { id: "actions", label: "Commander's Actions", icon: "&#9873;" }
  ];

  // Blank template used when the admin adds a new row to a list. The key is
  // the path to the list in the data file; the value is the shape of one entry.
  // To add a new tracked list, add its path and default entry here.
  var LIST_SCHEMAS = {
    "readiness.equipment": { name: "New equipment", serviceable: 0, total: 0, status: "green" },
    "readiness.ammunition": { type: "New ammunition type", status: "green", note: "" },
    "readiness.subunits": { name: "New sub-unit", rating: "green" },
    "training.courses": { name: "New course", category: "", completed: 0, pending: 0, status: "green" },
    "training.collectiveTraining": { name: "New training activity", status: "green", note: "" },
    "training.qualifications": { name: "New qualification", value: 0, target: 0 },
    "training.upcoming": { event: "New event", date: "", status: "green" },
    "admin.strengthReturns": { category: "New category", count: 0 },
    "admin.keyAppointments": { post: "New appointment", note: "", status: "green" },
    "admin.pendingActions": { item: "New action", note: "", status: "green" },
    "admin.compliance": { item: "New document / requirement", status: "green" },
    "maintenance.schedule": { equipment: "New equipment", lastService: "", nextDue: "", status: "green" },
    "maintenance.backlog": { item: "New backlog item", note: "", status: "green" },
    "maintenance.spares": { name: "New spares category", value: 0, target: 0 },
    "maintenance.fleetHealth": { name: "New sub-unit", rating: "green" },
    "welfare.accommodation": { item: "New facility", note: "", status: "green" },
    "welfare.medical": { item: "New indicator", note: "", status: "green" },
    "welfare.recreation": { item: "New facility / activity", note: "", status: "green" },
    "actions.items": { item: "New action", owner: "", due: "", status: "amber", note: "" }
  };

  var CFG = window.DASHBOARD_CONFIG || { REQUIRE_LOGIN: false };
  var AUTH_KEY = "regimentDashboardAuth_v1";

  var state = {
    data: loadData(),
    editMode: false,
    route: currentRoute(),
    signedIn: !CFG.REQUIRE_LOGIN || sessionStorage.getItem(AUTH_KEY) === "1"
  };

  // ---------------- storage ----------------
  function loadData() {
    var fresh = JSON.parse(JSON.stringify(window.DEFAULT_DATA));
    try {
      var raw = localStorage.getItem(STORAGE_KEY);
      if (raw) return withDefaults(JSON.parse(raw), fresh);
    } catch (e) { /* ignore corrupt storage */ }
    return fresh;
  }

  // Data saved before a new section existed would otherwise be missing it and
  // break rendering. Fill any absent top-level section from the defaults,
  // keeping everything the user has already entered.
  function withDefaults(saved, fresh) {
    if (!saved || typeof saved !== "object") return fresh;
    Object.keys(fresh).forEach(function (key) {
      if (saved[key] == null) saved[key] = fresh[key];
    });
    return saved;
  }

  function saveData() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state.data));
  }

  // ---------------- admin sign-in ----------------
  // NOTE: with no server to ask, this check necessarily runs in the viewer's
  // own browser. It keeps casual users out of Edit Mode; it is not a security
  // boundary. See the warning in js/config.js.
  function sha256Hex(text) {
    if (!window.crypto || !window.crypto.subtle) return Promise.reject(new Error("no-crypto"));
    return window.crypto.subtle.digest("SHA-256", new TextEncoder().encode(text)).then(function (buf) {
      return Array.prototype.map.call(new Uint8Array(buf), function (b) {
        return b.toString(16).padStart(2, "0");
      }).join("");
    });
  }

  function openLogin() {
    document.getElementById("loginError").className = "login-error";
    document.getElementById("loginPass").value = "";
    document.getElementById("loginBackdrop").classList.add("open");
    document.getElementById("loginUser").focus();
  }

  function closeLogin() {
    document.getElementById("loginBackdrop").classList.remove("open");
  }

  function loginFailed(message) {
    var el = document.getElementById("loginError");
    el.textContent = message;
    el.className = "login-error show";
  }

  function attemptLogin(user, pass) {
    if (user.trim().toLowerCase() !== String(CFG.ADMIN_USER).toLowerCase()) {
      loginFailed("Incorrect username or password.");
      return;
    }
    sha256Hex(user.trim().toLowerCase() + ":" + pass).then(function (hex) {
      if (hex !== CFG.ADMIN_HASH) {
        loginFailed("Incorrect username or password.");
        return;
      }
      state.signedIn = true;
      sessionStorage.setItem(AUTH_KEY, "1");
      closeLogin();
      state.editMode = true;
      render();
    })["catch"](function () {
      loginFailed("This browser cannot check the password here. Open the dashboard over https:// and try again.");
    });
  }

  function signOut() {
    sessionStorage.removeItem(AUTH_KEY);
    state.signedIn = false;
    if (state.editMode) {
      state.data = loadData();
      state.editMode = false;
    }
    render();
  }

  function resetData() {
    if (!confirm("Reset all data to the sample defaults? Any local edits on this device will be lost.")) return;
    localStorage.removeItem(STORAGE_KEY);
    state.data = JSON.parse(JSON.stringify(window.DEFAULT_DATA));
    state.editMode = false;
    render();
  }

  // ---------------- helpers ----------------
  function getByPath(obj, path) {
    return path.split(".").reduce(function (o, k) { return o == null ? o : o[k]; }, obj);
  }

  function setByPath(obj, path, value) {
    var parts = path.split(".");
    var cur = obj;
    for (var i = 0; i < parts.length - 1; i++) cur = cur[parts[i]];
    cur[parts[parts.length - 1]] = value;
  }

  function escapeHtml(str) {
    return String(str).replace(/[&<>"']/g, function (c) {
      return { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c];
    });
  }

  function cap(s) { return s.charAt(0).toUpperCase() + s.slice(1); }

  function currentRoute() {
    var h = (location.hash || "#overview").slice(1);
    return SECTIONS.some(function (s) { return s.id === h; }) ? h : "overview";
  }

  function badge(status) {
    return '<span class="badge ' + status + '"><span class="dot"></span>' + cap(status) + '</span>';
  }

  function statusField(path, value) {
    if (!state.editMode) return badge(value);
    var opts = ["green", "amber", "red"].map(function (s) {
      return '<option value="' + s + '"' + (s === value ? " selected" : "") + '>' + cap(s) + '</option>';
    }).join("");
    return '<select class="editable-input" data-path="' + path + '">' + opts + '</select>';
  }

  function textField(path, value, type) {
    type = type || "text";
    if (!state.editMode) return escapeHtml(String(value));
    return '<input class="editable-input" type="' + type + '" data-path="' + path + '" value="' + escapeHtml(String(value)) + '">';
  }

  // Percentage guarded against a zero/absent denominator — newly added rows
  // start at 0 / 0, which would otherwise produce NaN and a full-width bar.
  function pct(value, target) {
    if (!target || target <= 0) return 0;
    return Math.min(100, Math.round((value / target) * 100));
  }

  function progressBar(value, target, status) {
    var filled = pct(value, target);
    return (
      '<div class="progress-row">' +
        '<div class="label"><span>' + value + ' / ' + target + '</span><span>' + filled + '%</span></div>' +
        '<div class="progress-track"><div class="progress-fill ' + status + '" style="width:' + filled + '%"></div></div>' +
      '</div>'
    );
  }

  // ---------------- due dates ----------------
  // Whole days from today until `dateStr` (YYYY-MM-DD). Negative = overdue.
  // Returns null if the date is missing or unreadable, so a blank due date
  // is simply "not tracked" rather than silently counting as overdue.
  function daysUntil(dateStr) {
    if (!dateStr) return null;
    var parts = /^(\d{4})-(\d{2})-(\d{2})$/.exec(String(dateStr).trim());
    if (!parts) return null;
    var due = new Date(Number(parts[1]), Number(parts[2]) - 1, Number(parts[3]));
    if (isNaN(due.getTime())) return null;
    var today = new Date();
    today = new Date(today.getFullYear(), today.getMonth(), today.getDate());
    return Math.round((due - today) / 86400000);
  }

  function dueChip(dateStr) {
    var d = daysUntil(dateStr);
    if (d === null) {
      return '<span class="due-chip none">No due date</span>';
    }
    if (d < 0) {
      return '<span class="due-chip overdue">Overdue by ' + Math.abs(d) + (Math.abs(d) === 1 ? " day" : " days") + '</span>';
    }
    if (d === 0) return '<span class="due-chip soon">Due today</span>';
    if (d <= 7) return '<span class="due-chip soon">Due in ' + d + (d === 1 ? " day" : " days") + '</span>';
    return '<span class="due-chip">' + escapeHtml(dateStr) + '</span>';
  }

  // Open actions split by urgency. Used by both the Overview panel and the
  // Commander's Actions tab so the two can never disagree.
  function actionStats() {
    var items = (state.data.actions && state.data.actions.items) || [];
    var overdue = [];
    var soon = [];
    items.forEach(function (it, i) {
      var d = daysUntil(it.due);
      if (d === null) return;
      if (d < 0) overdue.push({ item: it, index: i, days: d });
      else if (d <= 7) soon.push({ item: it, index: i, days: d });
    });
    return { total: items.length, overdue: overdue, soon: soon };
  }

  function card(inner, extraClass) {
    return '<div class="card' + (extraClass ? " " + extraClass : "") + '">' + inner + '</div>';
  }

  // "+ Add" button shown under a list while in Edit Mode.
  function addBtn(listPath, label) {
    if (!state.editMode) return "";
    return '<button class="btn btn-add" data-add="' + listPath + '">&#43; Add ' + (label || "row") + '</button>';
  }

  // "✕" button that removes one entry from a list while in Edit Mode.
  function delBtn(listPath, index) {
    if (!state.editMode) return "";
    return '<button class="btn-del" data-del="' + listPath + '" data-idx="' + index +
           '" title="Remove this row" aria-label="Remove this row">&#10005;</button>';
  }

  // Table variants — the delete control needs its own column.
  function delTh() { return state.editMode ? "<th></th>" : ""; }
  function delTd(listPath, index) {
    return state.editMode ? "<td>" + delBtn(listPath, index) + "</td>" : "";
  }

  // ---------------- section renderers ----------------
  function renderOverview() {
    var d = state.data;
    var cats = [
      { id: "readiness", title: "Operational Readiness & Combat Effectiveness", data: d.readiness },
      { id: "training", title: "Training & Professional Development", data: d.training },
      { id: "admin", title: "Unit Administration & Personnel Management", data: d.admin },
      { id: "maintenance", title: "Maintenance & Resource Optimisation", data: d.maintenance },
      { id: "welfare", title: "Soldiers' Welfare & Living Standard", data: d.welfare }
    ];

    var metaCard = card(
      '<h3>Unit Information</h3>' +
      '<div class="item-row"><span class="note">Unit Name</span><span>' + textField("meta.unitName", d.meta.unitName) + '</span></div>' +
      '<div class="item-row"><span class="note">Subtitle</span><span>' + textField("meta.subtitle", d.meta.subtitle) + '</span></div>' +
      '<div class="item-row"><span class="note">Last Updated</span><span>' + textField("meta.lastUpdated", d.meta.lastUpdated, "date") + '</span></div>' +
      '<div class="item-row"><span class="note">Updated By</span><span>' + textField("meta.updatedBy", d.meta.updatedBy) + '</span></div>'
    );

    var cards = cats.map(function (c) {
      return (
        '<div class="card overview-card" data-goto="' + c.id + '">' +
          '<div class="card-header"><h3>' + c.title + '</h3>' + badge(c.data.overall) + '</div>' +
          '<p>' + escapeHtml(c.data.summary) + '</p>' +
        '</div>'
      );
    }).join("");

    // Actions needing attention are pulled to the top of the Overview so an
    // overdue item can't sit unnoticed inside its own tab.
    var stats = actionStats();
    var urgent = stats.overdue.concat(stats.soon);
    var alertCard = "";
    if (urgent.length) {
      var list = urgent.slice(0, 5).map(function (e) {
        return (
          '<div class="item-row">' +
            '<div><div class="name">' + escapeHtml(e.item.item) + '</div>' +
            '<div class="note">' + escapeHtml(e.item.owner || "Unassigned") + '</div></div>' +
            dueChip(e.item.due) +
          '</div>'
        );
      }).join("");
      var more = urgent.length > 5
        ? '<div class="note" style="margin-top:8px;">and ' + (urgent.length - 5) + ' more…</div>'
        : "";
      alertCard =
        '<div class="card action-alert" data-goto="actions">' +
          '<div class="card-header"><h3>&#9873; Needs command attention</h3>' +
            '<span class="badge ' + (stats.overdue.length ? "red" : "amber") + '"><span class="dot"></span>' +
            (stats.overdue.length ? stats.overdue.length + " overdue" : urgent.length + " due soon") +
            '</span>' +
          '</div>' + list + more +
        '</div>';
    }

    return (
      '<p class="section-summary">At-a-glance status across all five tracking categories. Click any card for details.</p>' +
      (alertCard ? '<div class="grid" style="margin-bottom:16px;">' + alertCard + '</div>' : "") +
      '<div class="grid">' + cards + '</div>' +
      '<h2 style="margin-top:28px;">Unit Information</h2>' +
      '<div class="grid">' + metaCard + '</div>'
    );
  }

  function renderReadiness() {
    var r = state.data.readiness;

    var overall = card(
      '<div class="card-header"><h2>Overall Readiness Rating</h2>' + statusField("readiness.overall", r.overall) + '</div>' +
      '<p class="note">' + textField("readiness.summary", r.summary) + '</p>'
    );

    var equipRows = r.equipment.map(function (e, i) {
      var pfx = "readiness.equipment." + i;
      return (
        '<tr>' +
          '<td>' + textField(pfx + ".name", e.name) + '</td>' +
          '<td>' + textField(pfx + ".serviceable", e.serviceable, "number") + ' / ' + textField(pfx + ".total", e.total, "number") + '</td>' +
          '<td style="min-width:110px;">' +
            '<div class="progress-track" style="width:100px;display:inline-block;vertical-align:middle;">' +
              '<div class="progress-fill ' + e.status + '" style="width:' + pct(e.serviceable, e.total) + '%"></div>' +
            '</div>' +
          '</td>' +
          '<td>' + statusField(pfx + ".status", e.status) + '</td>' +
          delTd("readiness.equipment", i) +
        '</tr>'
      );
    }).join("");

    var equipCard = card(
      '<h3>Equipment Serviceability</h3>' +
      '<div style="overflow-x:auto;"><table class="data-table"><thead><tr><th>Equipment</th><th>Serviceable</th><th>Ratio</th><th>Status</th>' + delTh() + '</tr></thead><tbody>' + equipRows + '</tbody></table></div>' +
      addBtn("readiness.equipment", "equipment"),
      "card-wide"
    );

    var ammoRows = r.ammunition.map(function (a, i) {
      var pfx = "readiness.ammunition." + i;
      return (
        '<div class="item-row">' +
          '<div><div class="name">' + textField(pfx + ".type", a.type) + '</div><div class="note">' + textField(pfx + ".note", a.note) + '</div></div>' +
          '<div class="row-controls">' + statusField(pfx + ".status", a.status) + delBtn("readiness.ammunition", i) + '</div>' +
        '</div>'
      );
    }).join("");

    var ammoCard = card('<h3>Ammunition State</h3>' + ammoRows + addBtn("readiness.ammunition", "ammunition type"));

    var manningPct = pct(r.manning.actual, r.manning.authorized);
    var manningStatus = manningPct >= 90 ? "green" : manningPct >= 75 ? "amber" : "red";
    var manningCard = card(
      '<h3>Manning Level vs Authorised Strength</h3>' +
      '<div class="stat-big">' + textField("readiness.manning.actual", r.manning.actual, "number") + ' / ' + textField("readiness.manning.authorized", r.manning.authorized, "number") + '</div>' +
      '<div class="stat-sub">' + manningPct + '% of authorised strength</div>' +
      progressBar(r.manning.actual, r.manning.authorized, manningStatus)
    );

    var subRows = r.subunits.map(function (s, i) {
      var pfx = "readiness.subunits." + i;
      return '<div class="item-row"><div class="name">' + textField(pfx + ".name", s.name) + '</div>' +
        '<div class="row-controls">' + statusField(pfx + ".rating", s.rating) + delBtn("readiness.subunits", i) + '</div></div>';
    }).join("");
    var subCard = card('<h3>Readiness Rating by Sub-Unit</h3>' + subRows + addBtn("readiness.subunits", "sub-unit"));

    return (
      '<div class="grid">' + overall + '</div>' +
      '<div class="grid" style="margin-top:16px;">' + equipCard + ammoCard + manningCard + subCard + '</div>'
    );
  }

  function renderTraining() {
    var t = state.data.training;
    var overall = card(
      '<div class="card-header"><h2>Training Status</h2>' + statusField("training.overall", t.overall) + '</div>' +
      '<p class="note">' + textField("training.summary", t.summary) + '</p>'
    );

    var courseRows = t.courses.map(function (c, i) {
      var pfx = "training.courses." + i;
      return (
        '<tr>' +
          '<td>' + textField(pfx + ".name", c.name) + '</td>' +
          '<td>' + textField(pfx + ".category", c.category) + '</td>' +
          '<td>' + textField(pfx + ".completed", c.completed, "number") + '</td>' +
          '<td>' + textField(pfx + ".pending", c.pending, "number") + '</td>' +
          '<td>' + statusField(pfx + ".status", c.status) + '</td>' +
          delTd("training.courses", i) +
        '</tr>'
      );
    }).join("");
    var coursesCard = card(
      '<h3>Courses — Completed vs Pending</h3>' +
      '<div style="overflow-x:auto;"><table class="data-table"><thead><tr><th>Course</th><th>Applies To</th><th>Completed</th><th>Pending</th><th>Status</th>' + delTh() + '</tr></thead><tbody>' + courseRows + '</tbody></table></div>' +
      addBtn("training.courses", "course"),
      "card-wide"
    );

    var collRows = t.collectiveTraining.map(function (c, i) {
      var pfx = "training.collectiveTraining." + i;
      return '<div class="item-row"><div><div class="name">' + textField(pfx + ".name", c.name) + '</div><div class="note">' + textField(pfx + ".note", c.note) + '</div></div>' +
        '<div class="row-controls">' + statusField(pfx + ".status", c.status) + delBtn("training.collectiveTraining", i) + '</div></div>';
    }).join("");
    var collCard = card('<h3>Individual &amp; Collective Training</h3>' + collRows + addBtn("training.collectiveTraining", "activity"));

    var qualCards = t.qualifications.map(function (q, i) {
      var pfx = "training.qualifications." + i;
      var status = q.value / q.target >= 0.9 ? "green" : q.value / q.target >= 0.7 ? "amber" : "red";
      return (
        '<div class="progress-row">' +
          '<div class="label"><span>' + textField(pfx + ".name", q.name) + '</span>' + delBtn("training.qualifications", i) + '</div>' +
          '<div style="display:flex;gap:6px;align-items:center;margin:4px 0;">' + textField(pfx + ".value", q.value, "number") + ' / ' + textField(pfx + ".target", q.target, "number") + '</div>' +
          progressBar(q.value, q.target, status) +
        '</div>'
      );
    }).join("");
    var qualCard = card('<h3>Certification / Qualification Tracking</h3>' + qualCards + addBtn("training.qualifications", "qualification"));

    var upRows = t.upcoming.map(function (u, i) {
      var pfx = "training.upcoming." + i;
      return '<div class="item-row"><div><div class="name">' + textField(pfx + ".event", u.event) + '</div><div class="note">' + textField(pfx + ".date", u.date, "date") + '</div></div>' +
        '<div class="row-controls">' + statusField(pfx + ".status", u.status) + delBtn("training.upcoming", i) + '</div></div>';
    }).join("");
    var upCard = card('<h3>Upcoming Exercises / Study Periods</h3>' + upRows + addBtn("training.upcoming", "event"));

    return (
      '<div class="grid">' + overall + '</div>' +
      '<div class="grid" style="margin-top:16px;">' + coursesCard + collCard + qualCard + upCard + '</div>'
    );
  }

  function renderAdmin() {
    var a = state.data.admin;
    var overall = card(
      '<div class="card-header"><h2>Administration Status</h2>' + statusField("admin.overall", a.overall) + '</div>' +
      '<p class="note">' + textField("admin.summary", a.summary) + '</p>'
    );

    var srRows = a.strengthReturns.map(function (s, i) {
      var pfx = "admin.strengthReturns." + i;
      return '<div class="item-row"><div class="name">' + textField(pfx + ".category", s.category) + '</div>' +
        '<div class="row-controls"><div class="stat-big" style="font-size:1.2rem;">' + textField(pfx + ".count", s.count, "number") + '</div>' + delBtn("admin.strengthReturns", i) + '</div></div>';
    }).join("");
    var srCard = card('<h3>Strength Returns</h3>' + srRows + addBtn("admin.strengthReturns", "category"));

    var keyRows = a.keyAppointments.map(function (k, i) {
      var pfx = "admin.keyAppointments." + i;
      return '<div class="item-row"><div><div class="name">' + textField(pfx + ".post", k.post) + '</div><div class="note">' + textField(pfx + ".note", k.note) + '</div></div>' +
        '<div class="row-controls">' + statusField(pfx + ".status", k.status) + delBtn("admin.keyAppointments", i) + '</div></div>';
    }).join("");
    var keyCard = card('<h3>Key Appointments &amp; Vacancies</h3>' + keyRows + addBtn("admin.keyAppointments", "appointment"));

    var pendRows = a.pendingActions.map(function (p, i) {
      var pfx = "admin.pendingActions." + i;
      return '<div class="item-row"><div><div class="name">' + textField(pfx + ".item", p.item) + '</div><div class="note">' + textField(pfx + ".note", p.note) + '</div></div>' +
        '<div class="row-controls">' + statusField(pfx + ".status", p.status) + delBtn("admin.pendingActions", i) + '</div></div>';
    }).join("");
    var pendCard = card('<h3>Pending Administrative Actions</h3>' + pendRows + addBtn("admin.pendingActions", "action"));

    var compRows = a.compliance.map(function (c, i) {
      var pfx = "admin.compliance." + i;
      return '<div class="item-row"><div class="name">' + textField(pfx + ".item", c.item) + '</div>' +
        '<div class="row-controls">' + statusField(pfx + ".status", c.status) + delBtn("admin.compliance", i) + '</div></div>';
    }).join("");
    var compCard = card('<h3>Documentation / Compliance Status</h3>' + compRows + addBtn("admin.compliance", "item"));

    return (
      '<div class="grid">' + overall + '</div>' +
      '<div class="grid" style="margin-top:16px;">' + srCard + keyCard + pendCard + compCard + '</div>'
    );
  }

  function renderMaintenance() {
    var m = state.data.maintenance;
    var overall = card(
      '<div class="card-header"><h2>Maintenance Status</h2>' + statusField("maintenance.overall", m.overall) + '</div>' +
      '<p class="note">' + textField("maintenance.summary", m.summary) + '</p>'
    );

    var schedRows = m.schedule.map(function (s, i) {
      var pfx = "maintenance.schedule." + i;
      return (
        '<tr>' +
          '<td>' + textField(pfx + ".equipment", s.equipment) + '</td>' +
          '<td>' + textField(pfx + ".lastService", s.lastService, "date") + '</td>' +
          '<td>' + textField(pfx + ".nextDue", s.nextDue, "date") + '</td>' +
          '<td>' + statusField(pfx + ".status", s.status) + '</td>' +
          delTd("maintenance.schedule", i) +
        '</tr>'
      );
    }).join("");
    var schedCard = card(
      '<h3>Maintenance Schedule</h3>' +
      '<div style="overflow-x:auto;"><table class="data-table"><thead><tr><th>Equipment</th><th>Last Service</th><th>Next Due</th><th>Status</th>' + delTh() + '</tr></thead><tbody>' + schedRows + '</tbody></table></div>' +
      addBtn("maintenance.schedule", "equipment"),
      "card-wide"
    );

    var backRows = m.backlog.map(function (b, i) {
      var pfx = "maintenance.backlog." + i;
      return '<div class="item-row"><div><div class="name">' + textField(pfx + ".item", b.item) + '</div><div class="note">' + textField(pfx + ".note", b.note) + '</div></div>' +
        '<div class="row-controls">' + statusField(pfx + ".status", b.status) + delBtn("maintenance.backlog", i) + '</div></div>';
    }).join("");
    var backCard = card('<h3>Maintenance Backlog</h3>' + backRows + addBtn("maintenance.backlog", "backlog item"));

    var spareCards = m.spares.map(function (s, i) {
      var pfx = "maintenance.spares." + i;
      var status = s.value / s.target >= 0.85 ? "green" : s.value / s.target >= 0.6 ? "amber" : "red";
      return (
        '<div class="progress-row">' +
          '<div class="label"><span>' + textField(pfx + ".name", s.name) + '</span>' + delBtn("maintenance.spares", i) + '</div>' +
          '<div style="display:flex;gap:6px;align-items:center;margin:4px 0;">' + textField(pfx + ".value", s.value, "number") + ' / ' + textField(pfx + ".target", s.target, "number") + '</div>' +
          progressBar(s.value, s.target, status) +
        '</div>'
      );
    }).join("");
    var sparesCard = card('<h3>Spares / Stores Availability</h3>' + spareCards + addBtn("maintenance.spares", "spares category"));

    var budgetPct = pct(m.budget.utilised, m.budget.allocated);
    var budgetStatus = budgetPct <= 85 ? "green" : budgetPct <= 100 ? "amber" : "red";
    var budgetCard = card(
      '<h3>Budget / Resource Utilisation</h3>' +
      '<div class="stat-big">' + budgetPct + '%</div>' +
      '<div class="stat-sub">Utilised ' + textField("maintenance.budget.utilised", m.budget.utilised, "number") + ' of ' + textField("maintenance.budget.allocated", m.budget.allocated, "number") + ' (allocation units)</div>' +
      progressBar(m.budget.utilised, m.budget.allocated, budgetStatus)
    );

    var fleetRows = m.fleetHealth.map(function (f, i) {
      var pfx = "maintenance.fleetHealth." + i;
      return '<div class="item-row"><div class="name">' + textField(pfx + ".name", f.name) + '</div>' +
        '<div class="row-controls">' + statusField(pfx + ".rating", f.rating) + delBtn("maintenance.fleetHealth", i) + '</div></div>';
    }).join("");
    var fleetCard = card('<h3>Vehicle &amp; Weapon Fleet Health</h3>' + fleetRows + addBtn("maintenance.fleetHealth", "sub-unit"));

    return (
      '<div class="grid">' + overall + '</div>' +
      '<div class="grid" style="margin-top:16px;">' + schedCard + backCard + sparesCard + budgetCard + fleetCard + '</div>'
    );
  }

  function renderWelfare() {
    var w = state.data.welfare;
    var overall = card(
      '<div class="card-header"><h2>Welfare Status</h2>' + statusField("welfare.overall", w.overall) + '</div>' +
      '<p class="note">' + textField("welfare.summary", w.summary) + '</p>'
    );

    var accRows = w.accommodation.map(function (a, i) {
      var pfx = "welfare.accommodation." + i;
      return '<div class="item-row"><div><div class="name">' + textField(pfx + ".item", a.item) + '</div><div class="note">' + textField(pfx + ".note", a.note) + '</div></div>' +
        '<div class="row-controls">' + statusField(pfx + ".status", a.status) + delBtn("welfare.accommodation", i) + '</div></div>';
    }).join("");
    var accCard = card('<h3>Accommodation &amp; Mess Status</h3>' + accRows + addBtn("welfare.accommodation", "facility"));

    var fundPct = pct(w.welfareFund.balance, w.welfareFund.target);
    var fundStatus = fundPct >= 80 ? "green" : fundPct >= 50 ? "amber" : "red";
    var fundCard = card(
      '<h3>Welfare Fund Status</h3>' +
      '<div class="stat-big">' + textField("welfare.welfareFund.balance", w.welfareFund.balance, "number") + ' ' + escapeHtml(w.welfareFund.currency) + '</div>' +
      '<div class="stat-sub">Target: ' + textField("welfare.welfareFund.target", w.welfareFund.target, "number") + ' ' + escapeHtml(w.welfareFund.currency) + '</div>' +
      progressBar(w.welfareFund.balance, w.welfareFund.target, fundStatus)
    );

    var medRows = w.medical.map(function (m, i) {
      var pfx = "welfare.medical." + i;
      return '<div class="item-row"><div><div class="name">' + textField(pfx + ".item", m.item) + '</div><div class="note">' + textField(pfx + ".note", m.note) + '</div></div>' +
        '<div class="row-controls">' + statusField(pfx + ".status", m.status) + delBtn("welfare.medical", i) + '</div></div>';
    }).join("");
    var medCard = card('<h3>Medical / Health Indicators</h3>' + medRows + addBtn("welfare.medical", "indicator"));

    var recRows = w.recreation.map(function (r, i) {
      var pfx = "welfare.recreation." + i;
      return '<div class="item-row"><div><div class="name">' + textField(pfx + ".item", r.item) + '</div><div class="note">' + textField(pfx + ".note", r.note) + '</div></div>' +
        '<div class="row-controls">' + statusField(pfx + ".status", r.status) + delBtn("welfare.recreation", i) + '</div></div>';
    }).join("");
    var recCard = card("<h3>Recreation &amp; Family Welfare</h3>" + recRows + addBtn("welfare.recreation", "facility"));

    return (
      '<div class="grid">' + overall + '</div>' +
      '<div class="grid" style="margin-top:16px;">' + accCard + fundCard + medCard + recCard + '</div>'
    );
  }

  function renderActions() {
    var a = state.data.actions;
    var stats = actionStats();

    var overall = card(
      '<div class="card-header"><h2>Commander\'s Action Tracker</h2>' + statusField("actions.overall", a.overall) + '</div>' +
      '<p class="note">' + textField("actions.summary", a.summary) + '</p>'
    );

    var counts = card(
      '<h3>At a glance</h3>' +
      '<div class="count-row">' +
        '<div class="count-box' + (stats.overdue.length ? " bad" : "") + '">' +
          '<div class="count-n">' + stats.overdue.length + '</div><div class="count-l">Overdue</div>' +
        '</div>' +
        '<div class="count-box' + (stats.soon.length ? " warn" : "") + '">' +
          '<div class="count-n">' + stats.soon.length + '</div><div class="count-l">Due within 7 days</div>' +
        '</div>' +
        '<div class="count-box">' +
          '<div class="count-n">' + stats.total + '</div><div class="count-l">Open actions</div>' +
        '</div>' +
      '</div>'
    );

    // Keep stored order while editing so rows don't jump under the cursor;
    // sort most-urgent-first for reading.
    var ordered = a.items.map(function (it, i) { return { item: it, index: i }; });
    if (!state.editMode) {
      ordered.sort(function (x, y) {
        var dx = daysUntil(x.item.due);
        var dy = daysUntil(y.item.due);
        if (dx === null && dy === null) return 0;
        if (dx === null) return 1;   // undated items sink to the bottom
        if (dy === null) return -1;
        return dx - dy;
      });
    }

    var rows = ordered.map(function (entry) {
      var it = entry.item;
      var pfx = "actions.items." + entry.index;
      var overdue = (daysUntil(it.due) || 0) < 0 && daysUntil(it.due) !== null;
      var note = (it.note || state.editMode)
        ? '<div class="note">' + textField(pfx + ".note", it.note) + '</div>'
        : "";
      return (
        '<tr' + (overdue && !state.editMode ? ' class="row-overdue"' : "") + '>' +
          '<td><div class="name">' + textField(pfx + ".item", it.item) + '</div>' + note + '</td>' +
          '<td>' + textField(pfx + ".owner", it.owner) + '</td>' +
          '<td>' + (state.editMode ? textField(pfx + ".due", it.due, "date") : dueChip(it.due)) + '</td>' +
          '<td>' + statusField(pfx + ".status", it.status) + '</td>' +
          delTd("actions.items", entry.index) +
        '</tr>'
      );
    }).join("");

    var empty = '<tr><td colspan="4" class="note" style="padding:16px 8px;">No open actions.</td></tr>';

    var table = card(
      '<h3>Open Actions</h3>' +
      '<div style="overflow-x:auto;"><table class="data-table"><thead><tr>' +
        '<th>Action</th><th>Owner</th><th>Due</th><th>Status</th>' + delTh() +
      '</tr></thead><tbody>' + (rows || empty) + '</tbody></table></div>' +
      addBtn("actions.items", "action"),
      "card-wide"
    );

    return (
      '<p class="section-summary">Items needing command attention that cut across the five tracking categories. ' +
      '&ldquo;Overdue&rdquo; is worked out from the due date automatically. Remove an action once it is closed.</p>' +
      '<div class="grid">' + overall + counts + '</div>' +
      '<div class="grid" style="margin-top:16px;">' + table + '</div>'
    );
  }

  var RENDERERS = {
    overview: renderOverview,
    readiness: renderReadiness,
    training: renderTraining,
    admin: renderAdmin,
    maintenance: renderMaintenance,
    welfare: renderWelfare,
    actions: renderActions
  };

  // ---------------- shell ----------------
  function buildSidebar() {
    var nav = document.getElementById("nav");
    nav.innerHTML = SECTIONS.map(function (s) {
      var dot = s.id === "overview" ? "" : '<span class="dot ' + state.data[s.id].overall + '"></span>';
      return (
        '<button class="nav-btn' + (state.route === s.id ? " active" : "") + '" data-nav="' + s.id + '">' +
          '<span class="icon">' + s.icon + '</span><span>' + s.label + '</span>' + dot +
        '</button>'
      );
    }).join("");
  }

  function sectionTitle(id) {
    var found = SECTIONS.filter(function (s) { return s.id === id; })[0];
    return found ? found.label : "";
  }

  function render() {
    state.route = currentRoute();
    buildSidebar();

    document.getElementById("page-title").textContent = sectionTitle(state.route);
    document.getElementById("page-meta").textContent =
      "Last updated " + state.data.meta.lastUpdated + " by " + state.data.meta.updatedBy;

    var editBtn = document.getElementById("editToggleBtn");
    if (state.editMode) {
      editBtn.textContent = "Save Changes";
    } else if (state.signedIn) {
      editBtn.textContent = "Edit Mode";
    } else {
      editBtn.textContent = "Sign in to edit";
    }
    editBtn.className = "btn " + (state.editMode ? "btn-primary" : "");
    document.getElementById("cancelEditBtn").style.display = state.editMode ? "inline-flex" : "none";
    document.getElementById("logoutBtn").style.display =
      (CFG.REQUIRE_LOGIN && state.signedIn && !state.editMode) ? "inline-flex" : "none";

    // Destructive/data-replacing actions belong to the admin too.
    ["resetBtn", "importInput"].forEach(function (id) {
      var el = document.getElementById(id);
      if (el) el.disabled = !state.signedIn;
    });
    var importLabel = document.querySelector('label[for="importInput"]');
    if (importLabel) importLabel.style.opacity = state.signedIn ? "" : "0.45";
    document.getElementById("resetBtn").style.opacity = state.signedIn ? "" : "0.45";

    document.getElementById("content").innerHTML = RENDERERS[state.route]();
    closeSidebar();
  }

  // After adding a row, put the cursor in its first field so the admin can
  // start typing straight away instead of hunting for the new blank row.
  function focusLastRow(listPath, index) {
    var first = document.querySelector('[data-path^="' + listPath + "." + index + '."]');
    if (!first) return;
    first.focus();
    if (typeof first.select === "function") first.select();
    first.scrollIntoView({ block: "center" });
  }

  function collectEdits() {
    var inputs = document.querySelectorAll("[data-path]");
    inputs.forEach(function (el) {
      var path = el.getAttribute("data-path");
      var val = el.tagName === "SELECT" ? el.value : el.value;
      if (el.type === "number") val = val === "" ? 0 : Number(val);
      setByPath(state.data, path, val);
    });
    state.data.meta.lastUpdated = new Date().toISOString().slice(0, 10);
  }

  // ---------------- export / import ----------------
  function exportJson() {
    var blob = new Blob([JSON.stringify(state.data, null, 2)], { type: "application/json" });
    var url = URL.createObjectURL(blob);
    var a = document.createElement("a");
    a.href = url;
    a.download = "unit-dashboard-data.json";
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }

  function importJson(file) {
    var reader = new FileReader();
    reader.onload = function () {
      try {
        var parsed = JSON.parse(reader.result);
        state.data = parsed;
        saveData();
        state.editMode = false;
        render();
        alert("Data imported successfully.");
      } catch (e) {
        alert("Could not read that file — make sure it is a valid dashboard JSON export.");
      }
    };
    reader.readAsText(file);
  }

  // ---------------- sidebar (mobile) ----------------
  function openSidebar() {
    document.getElementById("sidebar").classList.add("open");
    document.getElementById("overlay").classList.add("open");
  }
  function closeSidebar() {
    document.getElementById("sidebar").classList.remove("open");
    document.getElementById("overlay").classList.remove("open");
  }

  // ---------------- events ----------------
  document.addEventListener("click", function (e) {
    // Add a blank row to a list. Edits typed but not yet saved are collected
    // first so re-rendering does not throw them away.
    var addEl = e.target.closest("[data-add]");
    if (addEl) {
      var addPath = addEl.getAttribute("data-add");
      var schema = LIST_SCHEMAS[addPath];
      if (!schema) return;
      collectEdits();
      var list = getByPath(state.data, addPath);
      list.push(JSON.parse(JSON.stringify(schema)));
      render();
      focusLastRow(addPath, list.length - 1);
      return;
    }

    var delEl = e.target.closest("[data-del]");
    if (delEl) {
      var delPath = delEl.getAttribute("data-del");
      var idx = Number(delEl.getAttribute("data-idx"));
      collectEdits();
      if (!confirm("Remove this row? It will be gone once you click Save Changes.")) {
        render();
        return;
      }
      getByPath(state.data, delPath).splice(idx, 1);
      render();
      return;
    }

    var navBtn = e.target.closest("[data-nav]");
    if (navBtn) {
      location.hash = navBtn.getAttribute("data-nav");
      return;
    }
    var gotoCard = e.target.closest("[data-goto]");
    if (gotoCard) {
      location.hash = gotoCard.getAttribute("data-goto");
      return;
    }
  });

  window.addEventListener("hashchange", function () {
    if (state.editMode) {
      if (!confirm("Discard unsaved edits and leave this section?")) {
        location.hash = state.route;
        return;
      }
      state.data = loadData();
      state.editMode = false;
    }
    render();
  });

  document.getElementById("editToggleBtn").addEventListener("click", function () {
    if (state.editMode) {
      collectEdits();
      saveData();
      state.editMode = false;
    } else if (!state.signedIn) {
      openLogin();
      return;
    } else {
      state.editMode = true;
    }
    render();
  });

  document.getElementById("logoutBtn").addEventListener("click", signOut);

  document.getElementById("loginForm").addEventListener("submit", function (e) {
    e.preventDefault();
    attemptLogin(document.getElementById("loginUser").value, document.getElementById("loginPass").value);
  });

  document.getElementById("loginCancel").addEventListener("click", closeLogin);

  document.getElementById("loginBackdrop").addEventListener("click", function (e) {
    if (e.target === this) closeLogin();
  });

  document.addEventListener("keydown", function (e) {
    if (e.key === "Escape") closeLogin();
  });

  document.getElementById("cancelEditBtn").addEventListener("click", function () {
    // Adding/removing rows mutates state.data before Save, so Cancel has to
    // reload the last saved copy rather than just leaving edit mode.
    state.data = loadData();
    state.editMode = false;
    render();
  });

  document.getElementById("printBtn").addEventListener("click", function () {
    window.print();
  });

  document.getElementById("exportBtn").addEventListener("click", exportJson);

  document.getElementById("importInput").addEventListener("change", function (e) {
    if (e.target.files && e.target.files[0]) importJson(e.target.files[0]);
    e.target.value = "";
  });

  document.getElementById("resetBtn").addEventListener("click", resetData);

  document.getElementById("hamburgerBtn").addEventListener("click", openSidebar);
  document.getElementById("overlay").addEventListener("click", closeSidebar);

  // ---------------- live refresh across tabs/windows ----------------
  // The browser fires "storage" in every OTHER tab on the same device when
  // this key changes, so a save in one tab refreshes the rest with no reload.
  // (Different devices cannot see each other without a server — use
  // Export/Import JSON to move data between a phone and a computer.)
  var toastTimer = null;
  function showSyncToast(message) {
    var existing = document.querySelector(".sync-toast");
    if (existing) existing.remove();
    var el = document.createElement("div");
    el.className = "sync-toast";
    el.textContent = message;
    document.body.appendChild(el);
    clearTimeout(toastTimer);
    toastTimer = setTimeout(function () { el.remove(); }, 4000);
  }

  window.addEventListener("storage", function (e) {
    if (e.key !== STORAGE_KEY) return;

    if (state.editMode) {
      // Don't wipe out half-typed edits — tell them instead.
      showSyncToast("Updated in another tab. Save or cancel to see the change.");
      return;
    }
    state.data = loadData();
    render();
    showSyncToast("Dashboard updated");
  });

  render();
})();
