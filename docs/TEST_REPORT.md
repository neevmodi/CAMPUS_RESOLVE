# Test report — 2026-10-22

## Automated evidence

Command: `mvn test package`

Result: **PASS** — 9 tests, 0 failures, 0 errors, 0 skipped. The generated WAR exists at `target/campus-resolve.war`; inspection confirmed `WEB-INF/lib/mysql-connector-j-9.0.0.jar` is packaged.

Covered: classifier behavior (4), authorization path rules (1), lifecycle/validation (2), and PBKDF2/legacy hash compatibility (2).

## Runtime evidence

| Check | Result | Evidence |
|---|---|---|
| Public landing/login/registration pages | PASS (historical) | Existing runtime report records HTTP 200 responses. |
| MySQL service, account, schema | BLOCKED | No MySQL credentials or client session were available. |
| Student/staff/admin login | NOT TESTED | Requires the configured database account. |
| Submission, assignment, history persistence | NOT TESTED | Requires database-backed login. |
| Browser E2E/screenshots | NOT TESTED | Authenticated flows are blocked by database configuration. |

## Manual verification checklist

1. Run `scripts/run-local.ps1` in a PowerShell session after setting up `campus_app`.
2. Log in as each demo role; confirm the correct dashboard.
3. As student, submit a complaint and refresh the dashboard; confirm a single stored entry.
4. As admin, assign it to staff. As that staff account, update it through valid statuses and add notes.
5. Confirm history is chronological and persists after refresh.
6. Attempt a staff access to an unassigned complaint and a student access to staff/admin URLs; expect 404/403.
7. Submit a POST after removing `csrfToken`; expect HTTP 403.
