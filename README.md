# ShiftTrace Secure Notes: Spring Boot Project 1

An original, small demonstration of **HTTPS + keystore + database entity/repository/controller + Spring Security**. The scenario is a company facing phishing and SQL injection threats. A local H2 database stores **fictional maintenance notes**, not employee credentials.

## 1. Requirements (Windows laptop)
- **JDK 21**, **Apache Maven 3.9+**, **Git**, **Visual Studio Code**.
- VS Code: install **Extension Pack for Java** and **Spring Boot Extension Pack**.
- Internet required the first time Maven downloads dependencies.
- Open the extracted `Project1_ShiftTrace` folder in VS Code using **File > Open Folder**.

## 2. Generate an HTTPS keystore (VS Code PowerShell terminal)
From the project root:

```powershell
mkdir certs -ErrorAction SilentlyContinue
keytool -genkeypair -alias shifttrace -keyalg RSA -keysize 3072 -storetype PKCS12 -keystore certs/localhost.p12 -validity 30 -dname "CN=localhost, OU=Course Lab, O=Demo, L=Cincinnati, ST=Ohio, C=US" -ext "SAN=dns:localhost,ip:127.0.0.1"
```

`keytool` prompts you to create a store password. Use a strong unique local password and remember it. This generates a **self-signed, demonstration-only** certificate. Do not push `localhost.p12` to GitHub.

## 3. Set local environment variables
Replace placeholders and use **different strong values** for all three. These commands set values only in the current PowerShell session:

```powershell
$env:SSL_STORE_PASSWORD = "YOUR_KEYSTORE_PASSWORD"
$env:ANALYST_PASSWORD = "YOUR_ANALYST_DEMO_PASSWORD"
$env:ADMIN_PASSWORD = "YOUR_ADMIN_DEMO_PASSWORD"
```

Never paste passwords into screenshots or the report. Avoid saving real secrets in source code or Git history.

## 4. Run and test

```powershell
mvn test
mvn spring-boot:run
```

Wait for the server to start on **HTTPS port 8443**. Open:

`https://localhost:8443/api/notes`

Your browser will warn that the *locally generated self-signed certificate* is not trusted. This is expected in a local development lab, **not** something to ignore on public sites. The browser may ask for a username/password. Enter `analyst` and the test password you created. For a screenshot, avoid showing the password.

## 5. Search endpoint clarification (fixed in this revision)

Both URL styles now work (GET, with Basic Auth):

- Preferred: `https://localhost:8443/api/notes?assetTag=LAB-RACK-01`
- Alias: `https://localhost:8443/api/notes/search?assetTag=LAB-RACK-01`

Both return the one matching maintenance note. The matching HTTP GET paths are explicitly allowed to `ANALYST` and `ADMIN` in `SecurityConfig.java`. The `/api/admin/summary` path still requires `ADMIN`. The earlier 403 arose because the `/search` path was not implemented/allowed; it was not a SQL injection failure.

For injection-shaped input, try either path with `assetTag=LAB-RACK-01%27%20OR%20%271%27%3D%271`. Expected response: `[]`. The repository binds the full value as a query parameter. Run `mvn clean test` to exercise both forms and role restrictions.

**Existing local certificates:** do not delete `certs/localhost.p12` from your laptop. It is intentionally omitted from this distributable ZIP. Keep using your existing keystore and three environment variables. The updated ZIP contains source files, not runtime secrets.

## 5. Test using Postman

Configure a new **GET** request, with **Authorization > Basic Auth** (`analyst` + your demo password). To test the local self-signed certificate only, open Postman Settings and set certificate verification off *temporarily for this lab*, then turn it back on afterward. Prefer importing/trusting your local certificate where practical.

| Scenario | URL | Expected result |
|---|---|---|
| No credentials | `https://localhost:8443/api/notes` | 401 Unauthorized |
| Valid credentials | `https://localhost:8443/api/notes?assetTag=LAB-RACK-01` | 200, one JSON record |
| Injection text as search input | `https://localhost:8443/api/notes?assetTag=%27%20OR%20%271%27%3D%271` | 200, empty JSON list `[]` |
| Analyst attempts admin report | `https://localhost:8443/api/admin/summary` | 403 Forbidden |
| Admin requests summary | `https://localhost:8443/api/admin/summary` | 200, JSON count |

Use your own laptop to capture screenshots; this ZIP contains **no fabricated runtime screenshots**.

## 6. What the code demonstrates
1. **HTTPS**: Spring Boot serves TLS traffic using a PKCS#12 key store.
2. **Entity**: `MaintenanceNote` maps typed Java fields to a local relational table.
3. **Repository**: JPA's named `:tag` parameter binds a search value; it is **not** concatenated into SQL/JPQL.
4. **Controller**: `/api/notes` returns a limited JSON projection; input over 64 characters is rejected.
5. **Security**: Spring Security authenticates users using BCrypt password hashes in memory. `ADMIN` has access to `/api/admin/summary`; `ANALYST` does not.
6. **Tests**: JUnit/MockMvc check unauthorized access, ordinary lookup, injection-shaped input, and role separation.

## 7. Network access note
By default the application binds to `127.0.0.1` **only**. This is intentional to avoid exposing demo accounts. Access from another device on your local network requires `SERVER_ADDRESS`, a certificate with that device's actual hostname/IP in its Subject Alternative Name, and carefully restricted local firewall rules. Do **not** bind this classroom demo to a public interface without additional protections. Never use a self-signed localhost certificate for a real deployment.

## 8. How the phishing scenario relates
HTTPS protects the connection but **cannot stop users from voluntarily entering passwords into a fake IT email form**. In a real organization, use centralized identity with phishing-resistant MFA (for example, FIDO2/WebAuthn), security awareness, short-lived sessions, and monitoring. This sample uses Basic Auth **only to simplify testing**; it is not an enterprise authentication design.

## 9. GitHub upload (run from the extracted project directory)
Create an **empty** GitHub repository named `shifttrace-secure-notes` (without README). Then:

```powershell
git init
git branch -M main
git add .
git status
git commit -m "Create secure Spring Boot database demo"
git remote add origin https://github.com/YOUR_USERNAME/shifttrace-secure-notes.git
git push -u origin main
```

Review `git status` **before the commit**. The keystore, data files, password-containing `.env`, and Maven build output must not be in the commit. GitHub authentication may require browser sign-in or a token. Never paste a personal access token into code or screenshots.

## 10. Screenshots for APA report
1. VS Code Explorer showing packages and file tree.
2. `application.properties` (no actual passwords) and keystore **filename only** (not contents).
3. `MaintenanceNote.java`, `MaintenanceNoteRepository.java`, `NoteController.java`, `SecurityConfig.java`.
4. Terminal showing successful `mvn test` and application start.
5. Postman 200/401/403 results and injection-text result `[]`.
6. GitHub repository page showing the project uploaded; ensure no secrets appear.

Insert your screenshots into the designated figure spaces in the Word paper. The code figures are typeset from project source on **white**, matching the Word page; they are **not** screenshots captured on your computer.
