# Initial audit — 2026-10-22

## Actual baseline

- Maven source target is Java 17; the project builds a Tomcat 10 WAR and packages MySQL Connector/J 9.0.0.
- The initial `mvn clean test package` began successfully but the sandboxed run could not access Maven (`Access is denied`). The approved local-cache build was used for verification after repairs.
- Existing unit coverage was 9 tests. No integration test uses MySQL.
- Historical runtime evidence shows public pages deployed, while login reached a database authentication/configuration failure. No database credentials were available to this task, so MySQL-backed flows were not run.

## Findings repaired

1. Database code defaulted to `root` with an empty password. It now requires explicit `CAMPUS_DB_USER` and `CAMPUS_DB_PASSWORD`.
2. Complaint creation, status updates, and assignments used separate connections for complaint and history writes. They now use one transaction.
3. Staff dashboards exposed all complaints and staff could update unassigned records. Staff are now restricted to assigned complaints in the list, detail, and POST endpoint.
4. State-changing forms lacked CSRF protection. A session token is now required for every POST form.
5. Three JSPs rendered query-string messages with scriptlets and no escaping. They now use JSTL output escaping.
6. History was newest-first despite the stated chronological requirement. It is now oldest-first.
7. Detail pages offered invalid lifecycle transitions. They now offer only valid next states.

## Remaining verification plan

Configure the dedicated runtime user, deploy using `scripts/run-local.ps1`, then execute the manual workflow checklist in `TEST_REPORT.md`. Do not reset or reseed an existing database.
