# Revision notes

- Added the `/api/notes/search?assetTag=...` GET alias while retaining `/api/notes?assetTag=...`.
- Allowed both endpoints to authenticated ANALYST/ADMIN users in Spring Security.
- Preserved ADMIN-only access to `/api/admin/summary` and default deny for other endpoints.
- Added 4 automated regression tests for the alias, SQL-injection-shaped input, unauthenticated searches, and denied POST.
- Kept the parameterized JPA repository query and Spring Boot 3.5.7 / Java 21 settings.
- Included only repository-safe files: no keystore, database, logs, or real credentials.

## Updating an already running laptop project

Back up your current project. Copy the revised project source into your existing VS Code project. **Do not overwrite** your laptop's `certs/localhost.p12` or `data/` folder. Keep your own environment variables. Stop the currently running server, then run `mvn clean test` and `mvn spring-boot:run`. Existing `.gitignore` prevents local secrets from being added to GitHub; double-check `git status`.
