/*
 * ADHOC 47 MORTAR REGIMENT — DASHBOARD DATA FILE
 * -----------------------------------------------------------------------
 * This file is the single source of truth for the dashboard. It contains
 * ONLY sample/dummy entries — no real unit data. Replace values below with
 * your own before real use.
 *
 * HOW A NON-CODER CAN EDIT THIS FILE:
 *   1. Open this file in any text editor (Notepad, VS Code, etc.).
 *   2. Find the line you want to change — it's plain English, e.g.
 *        status: "green"
 *      Change "green" to "amber" or "red", or edit the text in quotes.
 *   3. Keep the quotes "" and commas , exactly as they are.
 *   4. Save the file and refresh the dashboard page in the browser.
 *
 * Status values used throughout: "green" | "amber" | "red"
 *
 * NOTE: You can also update everything from inside the dashboard itself
 * using "Edit Mode" (top-right button) — no file editing needed. Edit
 * Mode saves to the browser's local storage on the device you're using.
 * See dashboard/README.md for details on syncing edits across devices.
 * -----------------------------------------------------------------------
 */

window.DEFAULT_DATA = {
  meta: {
    unitName: "Adhoc 47 Mortar Regiment",
    subtitle: "Unit Status Dashboard — Internal Use Only",
    lastUpdated: "2026-08-04",
    updatedBy: "Adjutant, 47 Mortar Regt"
  },

  // 1. OPERATIONAL READINESS & COMBAT EFFECTIVENESS
  readiness: {
    overall: "amber",
    summary: "Overall readiness Amber — ammunition resupply and two vehicle deficiencies under action.",
    equipment: [
      { name: "81mm Mortars", serviceable: 22, total: 24, status: "green" },
      { name: "120mm Mortars", serviceable: 10, total: 12, status: "amber" },
      { name: "Unit Vehicles (all types)", serviceable: 38, total: 45, status: "amber" },
      { name: "Communication Sets", serviceable: 60, total: 62, status: "green" },
      { name: "Optics / Sighting Equipment", serviceable: 34, total: 36, status: "green" }
    ],
    ammunition: [
      { type: "HE Mortar Bombs 81mm", status: "green", note: "Full authorised scale held" },
      { type: "HE Mortar Bombs 120mm", status: "amber", note: "72% of scale — resupply requested" },
      { type: "Illuminating Rounds", status: "green", note: "Full authorised scale held" },
      { type: "Smoke Rounds", status: "red", note: "38% of scale — indent raised, pending issue" }
    ],
    manning: { authorized: 650, actual: 588 },
    subunits: [
      { name: "Battery A", rating: "green" },
      { name: "Battery B", rating: "amber" },
      { name: "Battery C", rating: "green" },
      { name: "HQ Battery", rating: "green" }
    ]
  },

  // 2. TRAINING & PROFESSIONAL DEVELOPMENT
  training: {
    overall: "green",
    summary: "Training programme on track. Two JCO course nominations pending confirmation.",
    courses: [
      { name: "Young Officers' Course", category: "Officers", completed: 4, pending: 2, status: "green" },
      { name: "Advanced Gunnery Course", category: "Officers/JCOs", completed: 6, pending: 1, status: "green" },
      { name: "JCO Promotion Cadre", category: "JCOs", completed: 3, pending: 3, status: "amber" },
      { name: "Signals Refresher", category: "OR", completed: 40, pending: 8, status: "green" },
      { name: "Driving & Maintenance Course", category: "OR", completed: 18, pending: 10, status: "amber" }
    ],
    collectiveTraining: [
      { name: "Sub-unit Live Fire Practice", status: "green", note: "Completed as per training calendar" },
      { name: "Regimental Command Post Exercise", status: "amber", note: "Scheduled next quarter" },
      { name: "Combined Arms Exercise", status: "red", note: "Awaiting range allotment" }
    ],
    qualifications: [
      { name: "Basic Mortar Fire Controllers Qualified", value: 28, target: 32 },
      { name: "Signals Operators Qualified", value: 45, target: 50 },
      { name: "Drivers (Category B/C) Qualified", value: 60, target: 65 }
    ],
    upcoming: [
      { event: "Annual Firing Camp", date: "2026-09-15", status: "green" },
      { event: "JCO Study Period", date: "2026-08-20", status: "green" },
      { event: "Regimental TEWT", date: "2026-10-05", status: "amber" }
    ]
  },

  // 3. UNIT ADMINISTRATION & PERSONNEL MANAGEMENT
  admin: {
    overall: "green",
    summary: "Administration steady. Two key appointment vacancies being processed.",
    strengthReturns: [
      { category: "Posted In (this month)", count: 6 },
      { category: "Posted Out (this month)", count: 4 },
      { category: "On Leave", count: 22 },
      { category: "On Courses", count: 15 },
      { category: "Hospitalised", count: 3 }
    ],
    keyAppointments: [
      { post: "Second-in-Command", status: "green", note: "Held" },
      { post: "Adjutant", status: "green", note: "Held" },
      { post: "Quartermaster", status: "amber", note: "Vacant — officiating arrangement in place" },
      { post: "Battery Commander — Sub-Unit B", status: "red", note: "Vacant — replacement awaited" }
    ],
    pendingActions: [
      { item: "Annual Confidential Reports (ACRs)", status: "amber", note: "80% completed" },
      { item: "Part II Order backlog", status: "green", note: "Up to date" },
      { item: "Leave reserve certificate renewal", status: "green", note: "Completed" }
    ],
    compliance: [
      { item: "Unit War Diary", status: "green" },
      { item: "Documentation Audit", status: "green" },
      { item: "Statutory Returns to Higher HQ", status: "amber" }
    ]
  },

  // 4. MAINTENANCE & RESOURCE OPTIMISATION
  maintenance: {
    overall: "amber",
    summary: "Maintenance backlog within acceptable limits; two vehicles awaiting spares.",
    schedule: [
      { equipment: "81mm Mortars", lastService: "2026-06-01", nextDue: "2026-09-01", status: "green" },
      { equipment: "120mm Mortars", lastService: "2026-05-10", nextDue: "2026-08-10", status: "amber" },
      { equipment: "Unit Vehicle Fleet", lastService: "2026-07-01", nextDue: "2026-10-01", status: "green" },
      { equipment: "Radio Sets", lastService: "2026-07-15", nextDue: "2026-10-15", status: "green" }
    ],
    backlog: [
      { item: "Vehicle gearbox replacement x2", status: "red", note: "Spares on order" },
      { item: "Mortar baseplate refurbishment x3", status: "amber", note: "In workshop" },
      { item: "Radio battery replacement (unit-wide)", status: "green", note: "Completed" }
    ],
    spares: [
      { name: "Vehicle Spares (critical items)", value: 64, target: 100 },
      { name: "Weapon Spares", value: 82, target: 100 },
      { name: "Signal Equipment Spares", value: 90, target: 100 }
    ],
    budget: { allocated: 100, utilised: 68 },
    fleetHealth: [
      { name: "Battery A", rating: "green" },
      { name: "Battery B", rating: "amber" },
      { name: "Battery C", rating: "green" }
    ]
  },

  // 5. SOLDIERS' WELFARE & LIVING STANDARD
  welfare: {
    overall: "green",
    summary: "Welfare indicators satisfactory. Barrack renovation in progress for Sub-Unit B.",
    accommodation: [
      { item: "Other Ranks Barracks", status: "green", note: "Habitable, routine upkeep ongoing" },
      { item: "JCOs' Mess", status: "green", note: "Good condition" },
      { item: "Officers' Mess", status: "green", note: "Good condition" },
      { item: "Sub-Unit B Barrack Renovation", status: "amber", note: "60% complete" }
    ],
    welfareFund: { balance: 420000, currency: "BDT", target: 500000 },
    medical: [
      { item: "Unit Sick Rate", status: "green", note: "Within normal range" },
      { item: "Immunisation Coverage", status: "green", note: "98% covered" },
      { item: "Medical Category Downgrades", status: "amber", note: "4 personnel under review" }
    ],
    recreation: [
      { item: "Unit Sports Meet", status: "green", note: "Held last month" },
      { item: "Family Welfare Programme", status: "green", note: "Quarterly get-together held" },
      { item: "Library / Recreation Room Upgrade", status: "amber", note: "In progress" }
    ]
  },

  // 6. COMMANDER'S ACTION TRACKER
  // Items needing command attention that cut across the five categories
  // above, so nothing falls between them. Each item has an owner and a
  // due date; "Overdue" is worked out from the date automatically, so
  // keep the dates accurate. Remove an item (Edit Mode → x) once closed.
  //
  // Dates must be written as YYYY-MM-DD, e.g. 2026-09-15.
  actions: {
    overall: "amber",
    summary: "Two actions overdue; smoke ammunition indent is the priority for command attention.",
    items: [
      {
        item: "Smoke ammunition indent — follow up with Bde",
        owner: "Sample — Quartermaster",
        due: "2026-07-20",
        status: "red",
        note: "Holding at 38% of scale; indent raised, no issue date yet"
      },
      {
        item: "Combined Arms Exercise — obtain range allotment",
        owner: "Sample — Adjutant",
        due: "2026-07-28",
        status: "red",
        note: "Blocking collective training milestone"
      },
      {
        item: "Two vehicle deficiencies — spares demand",
        owner: "Sample — MTO",
        due: "2026-08-07",
        status: "amber",
        note: "Awaiting critical vehicle spares"
      },
      {
        item: "JCO promotion cadre nominations — confirm",
        owner: "Sample — Subedar Major",
        due: "2026-08-15",
        status: "amber",
        note: "Three nominations pending confirmation"
      },
      {
        item: "Sub-Unit B barrack renovation — completion",
        owner: "Sample — 2IC",
        due: "2026-09-30",
        status: "green",
        note: "60% complete, on programme"
      }
    ]
  },

  // 7. DAILY PARADE STATE
  // "Present" plus every accounted-for column should add up to "Posted".
  // The dashboard checks this for you and flags any row that doesn't
  // balance, so a miscount is caught before it reaches the CO.
  parade: {
    overall: "green",
    summary: "Two Other Ranks unaccounted against posted strength — both listed as absentees and under check.",
    paradeDate: "2026-08-04",
    strength: [
      { category: "Officers", posted: 22, present: 18, leave: 2, sick: 0, course: 1, duty: 1 },
      { category: "JCOs", posted: 48, present: 39, leave: 5, sick: 1, course: 2, duty: 1 },
      { category: "Other Ranks", posted: 580, present: 484, leave: 58, sick: 9, course: 14, duty: 13 }
    ],
    absentees: [
      { name: "Sample — Sldr A", reason: "Absent without leave", since: "2026-08-02", status: "red", note: "Reported to Adjutant" },
      { name: "Sample — Sldr B", reason: "Overstayed leave", since: "2026-08-03", status: "amber", note: "Contact established, returning" }
    ]
  },

  // 8. VEHICLE MOVEMENT (IN / OUT)
  // status: "amber" = currently out, "green" = returned, "red" = overdue back.
  vehicles: {
    overall: "amber",
    summary: "Four vehicles out; one overdue against expected return.",
    movements: [
      { vehicle: "Sample — BA 1234", driver: "Sample — Dvr A", purpose: "Ration collection", out: "0715", eta: "1200", status: "red", note: "Overdue — no contact" },
      { vehicle: "Sample — BA 2345", driver: "Sample — Dvr B", purpose: "Bde HQ liaison", out: "0800", eta: "1600", status: "amber", note: "Out on task" },
      { vehicle: "Sample — BA 3456", driver: "Sample — Dvr C", purpose: "Sick evacuation to CMH", out: "0930", eta: "1400", status: "amber", note: "Out on task" },
      { vehicle: "Sample — BA 4567", driver: "Sample — Dvr D", purpose: "Water bowser run", out: "0600", eta: "0900", status: "green", note: "Returned 0850" }
    ]
  },

  // 9. DUTY ROSTER
  duty: {
    overall: "green",
    summary: "Duties detailed to end of week. Two standing appointments need reliefs nominated.",
    roster: [
      { date: "2026-08-04", duty: "Orderly Officer", name: "Sample — Lt A", contact: "Ext 201", status: "green" },
      { date: "2026-08-04", duty: "Guard Commander", name: "Sample — Hav B", contact: "Ext 202", status: "green" },
      { date: "2026-08-05", duty: "Orderly Officer", name: "Sample — Lt C", contact: "Ext 201", status: "green" },
      { date: "2026-08-05", duty: "Guard Commander", name: "Sample — Hav D", contact: "Ext 202", status: "amber" },
      { date: "2026-08-06", duty: "Orderly Officer", name: "To be detailed", contact: "—", status: "red" }
    ],
    standing: [
      { duty: "Ammunition Storeman", holder: "Sample — Nk E", status: "green", note: "Qualified, in date" },
      { duty: "Unit Fire Picket NCO", holder: "Sample — Hav F", status: "amber", note: "Relief to be nominated" },
      { duty: "Range Safety Officer", holder: "Vacant", status: "red", note: "Nomination pending" }
    ]
  },

  // 10. CRITICAL RESOURCES (SUSTAINMENT)
  // "days" = estimated days of supply at current consumption. This is the
  // sustainment view; Operational Readiness holds the ammunition scale view.
  resources: {
    overall: "amber",
    summary: "Fuel holding below one week. Smoke ammunition remains the critical shortfall.",
    stocks: [
      { item: "Diesel (POL)", held: 4200, authorised: 9000, unit: "litres", days: 5, status: "amber" },
      { item: "Petrol (POL)", held: 1100, authorised: 1500, unit: "litres", days: 12, status: "green" },
      { item: "HE Bombs 81mm", held: 2400, authorised: 2400, unit: "rounds", days: 30, status: "green" },
      { item: "HE Bombs 120mm", held: 860, authorised: 1200, unit: "rounds", days: 14, status: "amber" },
      { item: "Smoke Rounds", held: 190, authorised: 500, unit: "rounds", days: 4, status: "red" },
      { item: "Dry Rations", held: 18, authorised: 21, unit: "days", days: 18, status: "green" },
      { item: "Water (stored)", held: 46000, authorised: 60000, unit: "litres", days: 6, status: "amber" }
    ]
  },

  // 11. IMPORTANT COMMITMENTS
  // Dates as YYYY-MM-DD. "Days to go" is worked out automatically.
  commitments: {
    overall: "amber",
    summary: "Bde inspection inside three weeks; range allotment still outstanding.",
    items: [
      { event: "Sample — Bde Commander's Inspection", date: "2026-08-22", lead: "Sample — Adjutant", location: "Unit Lines", status: "amber", note: "Preparation programme issued" },
      { event: "Sample — Combined Arms Exercise", date: "2026-09-10", lead: "Sample — 2IC", location: "Field Firing Range", status: "red", note: "Range allotment not yet received" },
      { event: "Sample — Unit Sports Meet", date: "2026-09-25", lead: "Sample — Adjutant", location: "Unit Ground", status: "green", note: "On programme" },
      { event: "Sample — Annual Administrative Inspection", date: "2026-10-14", lead: "Sample — Subedar Major", location: "Unit Lines", status: "green", note: "Documentation being compiled" }
    ]
  },

  // 12. BORDER / REGIONAL SITUATIONAL AWARENESS
  // -----------------------------------------------------------------------
  // OPEN-SOURCE SUMMARIES ONLY. This board is for keeping the unit generally
  // informed from publicly available reporting (news agencies, official
  // statements, public advisories).
  //
  // Do NOT enter classified material, intelligence reports, source-derived
  // information, operational plans, or anything with a security marking.
  // This dashboard has no access control worth the name and may be hosted
  // on the public internet — treat everything written here as if it could
  // be read by anyone.
  // -----------------------------------------------------------------------
  situational: {
    overall: "amber",
    summary: "Sample entries only. Open-source summaries for general awareness — no classified content.",
    updates: [
      {
        date: "2026-08-03",
        headline: "Sample — Regional news roundup reviewed",
        area: "General",
        source: "Open press",
        status: "green",
        note: "Sample placeholder. Replace with an open-source summary and cite the outlet."
      },
      {
        date: "2026-08-01",
        headline: "Sample — Public weather and river-level advisory noted",
        area: "Monsoon / terrain",
        source: "Public advisory",
        status: "amber",
        note: "Sample placeholder. Affects movement planning; cite the issuing authority."
      },
      {
        date: "2026-07-28",
        headline: "Sample — Official statement on border trade reported in press",
        area: "Cross-border",
        source: "Open press",
        status: "green",
        note: "Sample placeholder. Summarise only what has been publicly reported."
      }
    ]
  }
};
