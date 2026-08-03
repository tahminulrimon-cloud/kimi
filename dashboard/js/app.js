(function () {
  "use strict";

  var STORAGE_KEY = "regimentDashboardData_v1";

  var SECTIONS = [
    { id: "overview", label: "Overview", icon: "&#8962;" },
    { id: "readiness", label: "Operational Readiness", icon: "&#127919;" },
    { id: "training", label: "Training & Development", icon: "&#127891;" },
    { id: "admin", label: "Unit Administration", icon: "&#128203;" },
    { id: "maintenance", label: "Maintenance & Resources", icon: "&#128295;" },
    { id: "welfare", label: "Welfare & Living Standard", icon: "&#9974;" }
  ];

  var state = {
    data: loadData(),
    editMode: false,
    route: currentRoute()
  };

  // ---------------- storage ----------------
  function loadData() {
    try {
      var raw = localStorage.getItem(STORAGE_KEY);
      if (raw) return JSON.parse(raw);
    } catch (e) { /* ignore corrupt storage */ }
    return JSON.parse(JSON.stringify(window.DEFAULT_DATA));
  }

  function saveData() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state.data));
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

  function progressBar(value, target, status) {
    var pct = target > 0 ? Math.min(100, Math.round((value / target) * 100)) : 0;
    return (
      '<div class="progress-row">' +
        '<div class="label"><span>' + value + ' / ' + target + '</span><span>' + pct + '%</span></div>' +
        '<div class="progress-track"><div class="progress-fill ' + status + '" style="width:' + pct + '%"></div></div>' +
      '</div>'
    );
  }

  function card(inner, extraClass) {
    return '<div class="card' + (extraClass ? " " + extraClass : "") + '">' + inner + '</div>';
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

    return (
      '<p class="section-summary">At-a-glance status across all five tracking categories. Click any card for details.</p>' +
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
              '<div class="progress-fill ' + e.status + '" style="width:' + Math.min(100, Math.round((e.serviceable / e.total) * 100)) + '%"></div>' +
            '</div>' +
          '</td>' +
          '<td>' + statusField(pfx + ".status", e.status) + '</td>' +
        '</tr>'
      );
    }).join("");

    var equipCard = card(
      '<h3>Equipment Serviceability</h3>' +
      '<div style="overflow-x:auto;"><table class="data-table"><thead><tr><th>Equipment</th><th>Serviceable</th><th>Ratio</th><th>Status</th></tr></thead><tbody>' + equipRows + '</tbody></table></div>'
    );

    var ammoRows = r.ammunition.map(function (a, i) {
      var pfx = "readiness.ammunition." + i;
      return (
        '<div class="item-row">' +
          '<div><div class="name">' + textField(pfx + ".type", a.type) + '</div><div class="note">' + textField(pfx + ".note", a.note) + '</div></div>' +
          statusField(pfx + ".status", a.status) +
        '</div>'
      );
    }).join("");

    var ammoCard = card('<h3>Ammunition State</h3>' + ammoRows);

    var manningPct = Math.round((r.manning.actual / r.manning.authorized) * 100);
    var manningStatus = manningPct >= 90 ? "green" : manningPct >= 75 ? "amber" : "red";
    var manningCard = card(
      '<h3>Manning Level vs Authorised Strength</h3>' +
      '<div class="stat-big">' + textField("readiness.manning.actual", r.manning.actual, "number") + ' / ' + textField("readiness.manning.authorized", r.manning.authorized, "number") + '</div>' +
      '<div class="stat-sub">' + manningPct + '% of authorised strength</div>' +
      progressBar(r.manning.actual, r.manning.authorized, manningStatus)
    );

    var subRows = r.subunits.map(function (s, i) {
      var pfx = "readiness.subunits." + i;
      return '<div class="item-row"><div class="name">' + textField(pfx + ".name", s.name) + '</div>' + statusField(pfx + ".rating", s.rating) + '</div>';
    }).join("");
    var subCard = card('<h3>Readiness Rating by Sub-Unit</h3>' + subRows);

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
        '</tr>'
      );
    }).join("");
    var coursesCard = card(
      '<h3>Courses — Completed vs Pending</h3>' +
      '<div style="overflow-x:auto;"><table class="data-table"><thead><tr><th>Course</th><th>Applies To</th><th>Completed</th><th>Pending</th><th>Status</th></tr></thead><tbody>' + courseRows + '</tbody></table></div>'
    );

    var collRows = t.collectiveTraining.map(function (c, i) {
      var pfx = "training.collectiveTraining." + i;
      return '<div class="item-row"><div><div class="name">' + textField(pfx + ".name", c.name) + '</div><div class="note">' + textField(pfx + ".note", c.note) + '</div></div>' + statusField(pfx + ".status", c.status) + '</div>';
    }).join("");
    var collCard = card('<h3>Individual & Collective Training</h3>' + collRows);

    var qualCards = t.qualifications.map(function (q, i) {
      var pfx = "training.qualifications." + i;
      var status = q.value / q.target >= 0.9 ? "green" : q.value / q.target >= 0.7 ? "amber" : "red";
      return (
        '<div class="progress-row">' +
          '<div class="label"><span>' + textField(pfx + ".name", q.name) + '</span></div>' +
          '<div style="display:flex;gap:6px;align-items:center;margin:4px 0;">' + textField(pfx + ".value", q.value, "number") + ' / ' + textField(pfx + ".target", q.target, "number") + '</div>' +
          progressBar(q.value, q.target, status) +
        '</div>'
      );
    }).join("");
    var qualCard = card('<h3>Certification / Qualification Tracking</h3>' + qualCards);

    var upRows = t.upcoming.map(function (u, i) {
      var pfx = "training.upcoming." + i;
      return '<div class="item-row"><div><div class="name">' + textField(pfx + ".event", u.event) + '</div><div class="note">' + textField(pfx + ".date", u.date, "date") + '</div></div>' + statusField(pfx + ".status", u.status) + '</div>';
    }).join("");
    var upCard = card('<h3>Upcoming Exercises / Study Periods</h3>' + upRows);

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
      return '<div class="item-row"><div class="name">' + textField(pfx + ".category", s.category) + '</div><div class="stat-big" style="font-size:1.2rem;">' + textField(pfx + ".count", s.count, "number") + '</div></div>';
    }).join("");
    var srCard = card('<h3>Strength Returns</h3>' + srRows);

    var keyRows = a.keyAppointments.map(function (k, i) {
      var pfx = "admin.keyAppointments." + i;
      return '<div class="item-row"><div><div class="name">' + textField(pfx + ".post", k.post) + '</div><div class="note">' + textField(pfx + ".note", k.note) + '</div></div>' + statusField(pfx + ".status", k.status) + '</div>';
    }).join("");
    var keyCard = card('<h3>Key Appointments & Vacancies</h3>' + keyRows);

    var pendRows = a.pendingActions.map(function (p, i) {
      var pfx = "admin.pendingActions." + i;
      return '<div class="item-row"><div><div class="name">' + textField(pfx + ".item", p.item) + '</div><div class="note">' + textField(pfx + ".note", p.note) + '</div></div>' + statusField(pfx + ".status", p.status) + '</div>';
    }).join("");
    var pendCard = card('<h3>Pending Administrative Actions</h3>' + pendRows);

    var compRows = a.compliance.map(function (c, i) {
      var pfx = "admin.compliance." + i;
      return '<div class="item-row"><div class="name">' + textField(pfx + ".item", c.item) + '</div>' + statusField(pfx + ".status", c.status) + '</div>';
    }).join("");
    var compCard = card('<h3>Documentation / Compliance Status</h3>' + compRows);

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
        '</tr>'
      );
    }).join("");
    var schedCard = card(
      '<h3>Maintenance Schedule</h3>' +
      '<div style="overflow-x:auto;"><table class="data-table"><thead><tr><th>Equipment</th><th>Last Service</th><th>Next Due</th><th>Status</th></tr></thead><tbody>' + schedRows + '</tbody></table></div>'
    );

    var backRows = m.backlog.map(function (b, i) {
      var pfx = "maintenance.backlog." + i;
      return '<div class="item-row"><div><div class="name">' + textField(pfx + ".item", b.item) + '</div><div class="note">' + textField(pfx + ".note", b.note) + '</div></div>' + statusField(pfx + ".status", b.status) + '</div>';
    }).join("");
    var backCard = card('<h3>Maintenance Backlog</h3>' + backRows);

    var spareCards = m.spares.map(function (s, i) {
      var pfx = "maintenance.spares." + i;
      var status = s.value / s.target >= 0.85 ? "green" : s.value / s.target >= 0.6 ? "amber" : "red";
      return (
        '<div class="progress-row">' +
          '<div class="label"><span>' + textField(pfx + ".name", s.name) + '</span></div>' +
          '<div style="display:flex;gap:6px;align-items:center;margin:4px 0;">' + textField(pfx + ".value", s.value, "number") + ' / ' + textField(pfx + ".target", s.target, "number") + '</div>' +
          progressBar(s.value, s.target, status) +
        '</div>'
      );
    }).join("");
    var sparesCard = card('<h3>Spares / Stores Availability</h3>' + spareCards);

    var budgetPct = Math.round((m.budget.utilised / m.budget.allocated) * 100);
    var budgetStatus = budgetPct <= 85 ? "green" : budgetPct <= 100 ? "amber" : "red";
    var budgetCard = card(
      '<h3>Budget / Resource Utilisation</h3>' +
      '<div class="stat-big">' + budgetPct + '%</div>' +
      '<div class="stat-sub">Utilised ' + textField("maintenance.budget.utilised", m.budget.utilised, "number") + ' of ' + textField("maintenance.budget.allocated", m.budget.allocated, "number") + ' (allocation units)</div>' +
      progressBar(m.budget.utilised, m.budget.allocated, budgetStatus)
    );

    var fleetRows = m.fleetHealth.map(function (f, i) {
      var pfx = "maintenance.fleetHealth." + i;
      return '<div class="item-row"><div class="name">' + textField(pfx + ".name", f.name) + '</div>' + statusField(pfx + ".rating", f.rating) + '</div>';
    }).join("");
    var fleetCard = card('<h3>Vehicle & Weapon Fleet Health</h3>' + fleetRows);

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
      return '<div class="item-row"><div><div class="name">' + textField(pfx + ".item", a.item) + '</div><div class="note">' + textField(pfx + ".note", a.note) + '</div></div>' + statusField(pfx + ".status", a.status) + '</div>';
    }).join("");
    var accCard = card('<h3>Accommodation & Mess Status</h3>' + accRows);

    var fundPct = Math.round((w.welfareFund.balance / w.welfareFund.target) * 100);
    var fundStatus = fundPct >= 80 ? "green" : fundPct >= 50 ? "amber" : "red";
    var fundCard = card(
      '<h3>Welfare Fund Status</h3>' +
      '<div class="stat-big">' + textField("welfare.welfareFund.balance", w.welfareFund.balance, "number") + ' ' + escapeHtml(w.welfareFund.currency) + '</div>' +
      '<div class="stat-sub">Target: ' + textField("welfare.welfareFund.target", w.welfareFund.target, "number") + ' ' + escapeHtml(w.welfareFund.currency) + '</div>' +
      progressBar(w.welfareFund.balance, w.welfareFund.target, fundStatus)
    );

    var medRows = w.medical.map(function (m, i) {
      var pfx = "welfare.medical." + i;
      return '<div class="item-row"><div><div class="name">' + textField(pfx + ".item", m.item) + '</div><div class="note">' + textField(pfx + ".note", m.note) + '</div></div>' + statusField(pfx + ".status", m.status) + '</div>';
    }).join("");
    var medCard = card('<h3>Medical / Health Indicators</h3>' + medRows);

    var recRows = w.recreation.map(function (r, i) {
      var pfx = "welfare.recreation." + i;
      return '<div class="item-row"><div><div class="name">' + textField(pfx + ".item", r.item) + '</div><div class="note">' + textField(pfx + ".note", r.note) + '</div></div>' + statusField(pfx + ".status", r.status) + '</div>';
    }).join("");
    var recCard = card("<h3>Recreation & Family Welfare</h3>" + recRows);

    return (
      '<div class="grid">' + overall + '</div>' +
      '<div class="grid" style="margin-top:16px;">' + accCard + fundCard + medCard + recCard + '</div>'
    );
  }

  var RENDERERS = {
    overview: renderOverview,
    readiness: renderReadiness,
    training: renderTraining,
    admin: renderAdmin,
    maintenance: renderMaintenance,
    welfare: renderWelfare
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
    editBtn.textContent = state.editMode ? "Save Changes" : "Edit Mode";
    editBtn.className = "btn " + (state.editMode ? "btn-primary" : "");
    document.getElementById("cancelEditBtn").style.display = state.editMode ? "inline-flex" : "none";

    document.getElementById("content").innerHTML = RENDERERS[state.route]();
    closeSidebar();
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
      state.editMode = false;
    }
    render();
  });

  document.getElementById("editToggleBtn").addEventListener("click", function () {
    if (state.editMode) {
      collectEdits();
      saveData();
      state.editMode = false;
    } else {
      state.editMode = true;
    }
    render();
  });

  document.getElementById("cancelEditBtn").addEventListener("click", function () {
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

  render();
})();
