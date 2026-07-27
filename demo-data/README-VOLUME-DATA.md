# SwineCore high-volume demo data

Import in this order after starting SwineCore once:

1. `swinecore-demo-data.sql`
2. `swinecore-volume-data.sql`

The volume file adds 28,000 rows (well above the requested 1,000 records):

- 12,000 staff operational reports/tasks
- 5,000 attendance records
- 4,000 finance transactions
- 3,000 semen orders
- 2,000 pig orders
- 2,000 pig order items

Reserved ID ranges and cleanup statements make the file safe to re-import. This dataset is intended for pagination, reporting, search, analytics, and load demonstrations. The smaller base dataset remains the best source for a realistic day-to-day project presentation.

Category/master data is intentionally excluded. The demo files do not create genetics categories and use only the system defaults (`Y`, `L`, `D`, `YL`).
