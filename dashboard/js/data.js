/*
 * UNIT DASHBOARD — SAMPLE DATA FILE
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
    unitName: "Sample Field Artillery Regiment (Mortar)",
    subtitle: "Unit Status Dashboard — Internal Use Only",
    lastUpdated: "2026-08-03",
    updatedBy: "Adjutant, Sample Regt"
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
      { name: "Sample Unit — Sub-Unit A (Battery A)", rating: "green" },
      { name: "Sample Unit — Sub-Unit B (Battery B)", rating: "amber" },
      { name: "Sample Unit — Sub-Unit C (Battery C)", rating: "green" },
      { name: "Sample Unit — HQ Battery", rating: "green" }
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
      { name: "Sample Unit — Sub-Unit A", rating: "green" },
      { name: "Sample Unit — Sub-Unit B", rating: "amber" },
      { name: "Sample Unit — Sub-Unit C", rating: "green" }
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
  }
};
