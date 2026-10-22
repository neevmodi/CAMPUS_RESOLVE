# CAMPUS RESOLVE
### AI-Assisted College Complaint & Resolution Portal

Campus Resolve is a focused Java 17 academic application for reporting, classifying, assigning, and tracking campus complaints. Its “AI-assisted” behavior is deliberately transparent: `ComplaintClassifier` uses explainable keyword scoring, not a claimed machine-learning model.

## Features
- Student registration, login, complaint submission, and tracking
- Admin and staff role-aware dashboards
- Category, priority, and department recommendations
- Complaint lifecycle and history schema
- MySQL JDBC persistence using prepared statements
- Swing operations console using Swing components, AWT layouts, and event listeners
- Responsive presentation with JSP, JSTL, HTML, and CSS

## Architecture
Presentation (JSP) → Servlet/filter layer → service layer → DAO layer → JDBC/MySQL. Domain models demonstrate encapsulation, inheritance, polymorphism, interfaces, enums, custom exceptions, and collections.

## Setup
1. Install JDK 17, Maven, MySQL 8, and Tomcat 10+.
2. For an existing database, run `database/migration-password-hash.sql`; for a new database, run `database/schema.sql`, then `database/seed.sql`.
3. Run `database/create-runtime-user.sql` interactively as a MySQL administrator after replacing its placeholder with a strong local password. Set `CAMPUS_DB_URL`, `CAMPUS_DB_USER`, and `CAMPUS_DB_PASSWORD` before starting Tomcat. The application will not fall back to `root` or an empty password. New registrations use salted PBKDF2-HMAC-SHA256 hashes. Existing seeded SHA-256 hashes are verified once and upgraded after successful login.
4. Run `mvn clean test package`.
5. Deploy `target/campus-resolve.war` to Tomcat 10.1.

Example PowerShell setup for a local MySQL account (the password is entered interactively and is not placed in the command line):

```powershell
$env:CAMPUS_DB_URL = "jdbc:mysql://localhost:3306/campus_resolve?serverTimezone=UTC"
$env:CAMPUS_DB_USER = "campus_app"
$securePassword = Read-Host "Enter the local MySQL password" -AsSecureString
$env:CAMPUS_DB_PASSWORD = [System.Net.NetworkCredential]::new("", $securePassword).Password
mvn clean test package
Copy-Item .\target\campus-resolve.war "C:\Tomcat\apache-tomcat-10.1.61\webapps\campus-resolve.war" -Force
```

For the currently running Tomcat launched from `C:\Tomcat\apache-tomcat-10.1.61`, stop it using its normal `bin\shutdown.bat`, set the variables in the same PowerShell session, and start it again with `bin\startup.bat`. A running Java process does not inherit environment-variable changes made later. If Tomcat is installed as a Windows service, configure the same variables in the service wrapper and restart that service instead. The application URL is `http://localhost:8080/campus-resolve/`.

For the guided local workflow, run `scripts\check-environment.ps1`, then `scripts\run-local.ps1`. The latter prompts for the runtime secret without saving or printing it, builds the WAR, copies only `campus-resolve.war`, and starts Tomcat.

Demo accounts (password `Campus@123`): `admin@campusresolve.local`, `staff@campusresolve.local`, and `student@campusresolve.local`. Change seeded passwords before using this outside a classroom environment.

## Running the desktop console
After compiling, run `com.campusresolve.gui.AdminDesktopApp` from an IDE or with the Maven-built classpath. It reads the same database and demonstrates a desktop operations view.

## Testing and documentation
- Unit tests focus on deterministic classifier behavior: `mvn test`
- [Architecture notes](docs/architecture.md)
- [Database notes](docs/database.md)
- [Testing notes](docs/testing.md)
- [Initial audit](docs/INITIAL_AUDIT.md), [test report](docs/TEST_REPORT.md), [user guide](docs/USER_GUIDE.md), and [demo script](docs/DEMO_SCRIPT.md)
- [Final validation checklist](docs/FINAL_VALIDATION.md)

## Java concepts
Inheritance is used by role-specific `User` types; `Classifiable` and `Trackable` express useful contracts; DAO/service boundaries demonstrate abstraction; Swing action listeners demonstrate event handling; `try-with-resources` protects JDBC resources.

## Future enhancements
Add authenticated admin status/assignment forms, notifications, richer history timelines, and a connection pool for production deployment.

## Team members
Not provided. Add team names here before submission.
