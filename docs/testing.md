# Testing

Run `mvn clean test package` for unit tests and WAR packaging. The current verified result is 9 tests passing with no failures or errors. Coverage includes classifier behavior, password hashing/legacy compatibility, lifecycle validation, and role-path authorization. Database-backed workflows require configured MySQL credentials; see `TEST_REPORT.md` for the exact manual verification checklist and honest runtime status.
