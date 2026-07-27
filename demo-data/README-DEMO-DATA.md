# SwineCore Project Show Demo Data

This package provides realistic operational demo data for every calendar day from **16 July 2026 through 31 August 2026**.

## Import

1. Back up your existing `swinecore` database.
2. Start SwineCore once so Hibernate creates/updates the tables.
3. Import `swinecore-demo-data-2026-07-16-to-2026-08-31.sql` into the `swinecore` database using MySQL Workbench: **Server → Data Import → Import from Self-Contained File**.
4. Copy the supplied `images` directory into `src/main/resources/static/images` if you are importing the data into a different copy of the project. The v19 full-project ZIP already contains these images.
5. Restart SwineCore and log in with an account below.

The SQL is re-importable. It only replaces records in the reserved demo ID ranges and `[DEMO]` tasks; ordinary existing records are not deleted.

## Login accounts

All accounts below use password: **`123123`**

| Role | Email | Demo scope |
|---|---|---|
| Admin | `admin.demo@swinecore.mm` | Farms, users, customers, genetics and analytics |
| Manager | `manager.demo@swinecore.mm` | Green Valley farm operations, rules, reports, inventory, approvals and finance |
| HR | `hr.demo@swinecore.mm` | Staff assignments and public advertisements |
| Supervisor | `supervisor.breeding@swinecore.mm` | Breeding House tasks and reports |
| Supervisor | `supervisor.farrowing@swinecore.mm` | Farrowing House tasks and birth workflow |
| Supervisor | `supervisor.grower@swinecore.mm` | Grower tasks, feed receipt and sale listing |
| Supervisor | `supervisor.finisher@swinecore.mm` | Finisher tasks and feed discrepancy confirmation |
| Staff | `staff.breeding1@swinecore.mm` | Breeding daily task workflow |
| Staff | `staff.farrowing1@swinecore.mm` | Birth record and farrowing tasks |
| Staff | `staff.grower1@swinecore.mm` | Grower daily tasks and reports |
| Staff | `staff.finisher1@swinecore.mm` | Finisher daily tasks and reports |
| Customer | `customer.demo@swinecore.mm` | Marketplace, paid orders and vouchers |
| Customer | `buyer.demo@swinecore.mm` | Pending QR payment and shopping flow |

## Included scenarios

- 47 consecutive task dates, with 576 daily and specialized tasks.
- Pending, in-progress, submitted, approved and manager-review states.
- Realistic submitted staff notes and supervisor comments.
- Five late-gestation sows with expected farrowing dates across July and August.
- A confirmed birth record and linked piglets.
- Grower, finisher, breeding, marketplace and semen-boar records with images.
- Exactly 300 pigs in each demo building (1,800 pigs across six buildings).
- Active rules for feed, medication, vaccination, breeding and biosecurity.
- Uses only the system default genetics (`Y`, `L`, `D`, `YL`); no genetics records are added.
- Normal attendance, clocked-in shifts and approved early leave.
- Feed inventory warnings, verified factory deliveries and a quantity mismatch awaiting manager action.
- Completed and pending finance transactions.
- Paid and QR-pending pig/semen orders.
- Active farm advertisements, rooms, shifts and staff assignments.

## Validation performed

The SQL was imported into an isolated MySQL database created from the current SwineCore Hibernate schema. Validation result: **692 statements executed, 576 tasks, 47 distinct task days, range 2026-07-16 to 2026-08-31**.
