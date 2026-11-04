# EduTrack — API de notas con OAuth2 y JWT

Microservicio Spring Boot 3.3 (Java 17, arquitectura hexagonal) que actúa como **resource server**. Valida JWT emitidos por Keycloak (`realm edutrack`) y no guarda ningún client secret.

## Endpoints

| Endpoint | Quién puede | Detalle |
| --- | --- | --- |
| `GET /api/publico/cursos` | Cualquiera, sin token | Catálogo de cursos |
| `GET /api/notas` | Rol `ESTUDIANTE` | Solo las notas del `sub` del JWT |
| `POST /api/notas` | Rol `DOCENTE` | Registra una nota de 0 a 20 |
| `GET /api/reportes/notas` | Rol `REPORTES` | Todas las notas (ticket EDU-142) |
| `GET /actuator/health` | Público | Salud de la API |

## Levantar Keycloak

Imagen fija: `quay.io/keycloak/keycloak:26.0`. El realm se importa desde `infra/keycloak/realm/edutrack-realm.json` (sin secretos).

| Paso | Docker | Podman |
| --- | --- | --- |
| Preparar (Windows y macOS) | Abrir Docker Desktop y esperar «Engine running» | `podman machine init` (una vez) y `podman machine start` |
| Construir | `docker build -t edutrack-keycloak infra/keycloak` | `podman build -t edutrack-keycloak infra/keycloak` |
| Levantar | `docker run -d --name edutrack-keycloak -p 8180:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin edutrack-keycloak start-dev --import-realm` | `podman run -d --name edutrack-keycloak -p 8180:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin edutrack-keycloak start-dev --import-realm` |
| Logs y borrar | `docker logs -f edutrack-keycloak` · `docker rm -f edutrack-keycloak` | `podman logs -f edutrack-keycloak` · `podman rm -f edutrack-keycloak` |

Keycloak tarda 30–60 segundos. Consola: http://localhost:8180 (`admin` / `admin`).

### Contraseñas de laboratorio (no van en el JSON)

Tras el arranque, asigna contraseñas desde el contenedor. Ejemplo con Docker (equivalente con `podman exec`):

```bash
docker exec edutrack-keycloak /opt/keycloak/bin/kcadm.sh config credentials --server http://localhost:8080 --realm master --user admin --password admin
docker exec edutrack-keycloak /opt/keycloak/bin/kcadm.sh set-password -r edutrack --username valeria --new-password edutrack
docker exec edutrack-keycloak /opt/keycloak/bin/kcadm.sh set-password -r edutrack --username bruno --new-password edutrack
docker exec edutrack-keycloak /opt/keycloak/bin/kcadm.sh set-password -r edutrack --username salas --new-password edutrack
```

Usuarios: `valeria` y `bruno` (`ESTUDIANTE`), `salas` (`DOCENTE`). El `sub` de Valeria es `8f14e45f-ea9d-4e1a-9c0a-000000000001`.

### Client secret de reportes (EDU-142)

Keycloak lo genera al importar el realm. Cópialo en **Clients → edutrack-reportes → Credentials**. Nunca se escribe en `application.yml` ni en el JSON. Úsalo como variable de entorno (`REPORTES_CLIENT_SECRET`) o como secret de Postman (`reportesSecret`). Cada vez que recreas el contenedor, el secret cambia.

## Levantar la API

```bash
./mvnw spring-boot:run
```

Issuer por defecto: `http://localhost:8180/realms/edutrack`. Si usas otro puerto:

```bash
OAUTH2_ISSUER_URI=http://localhost:8181/realms/edutrack ./mvnw spring-boot:run
```

## Probar con Postman

### Usuarios (Authorization Code + PKCE)

No uses el flujo password.

1. Auth type: **OAuth 2.0**
2. Grant type: **Authorization Code**
3. Callback URL: `https://oauth.pstmn.io/v1/callback`
4. Auth URL: `http://localhost:8180/realms/edutrack/protocol/openid-connect/auth`
5. Token URL: `http://localhost:8180/realms/edutrack/protocol/openid-connect/token`
6. Client ID: `edutrack-web`
7. Client Authentication: **Send client credentials in body**
8. PKCE: **S256**

`GET http://localhost:8080/api/notas` con el token de Valeria → solo sus notas.

`POST http://localhost:8080/api/notas` con el token de Salas:

```json
{
  "cursoId": "C-102",
  "estudianteId": "8f14e45f-ea9d-4e1a-9c0a-000000000001",
  "valor": 17
}
```

### Servicio de reportes (client credentials)

```http
POST http://localhost:8180/realms/edutrack/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=client_credentials&client_id=edutrack-reportes&client_secret={{reportesSecret}}
```

```http
GET http://localhost:8080/api/reportes/notas
Authorization: Bearer <token de edutrack-reportes>
```

## Fallos esperables al probar

| Error | Dónde aparece | Corrección |
| --- | --- | --- |
| 401 con un token recién emitido | API, al validar el `iss` | Pedir el token por `http://localhost:8180`, la misma URL del `issuer-uri` |
| 403 aunque el usuario tiene su rol | API, al autorizar | El conversor lee `realm_access.roles` y lo registra en `jwt(...)` |
| Podman no encuentra la imagen | `podman build` | Usar `quay.io/keycloak/keycloak:26.0` y ejecutar `podman machine start` |
| Puerto 8180 ocupado | `docker run` / `podman run` | Usar `-p 8181:8080` y `OAUTH2_ISSUER_URI` con el puerto 8181 |
| 401 `unauthorized_client` al pedir el token de reportes | Keycloak, endpoint de token | Copiar otra vez el secret desde Credentials: cambia al recrear el contenedor |
| El realm no cambia tras editar el JSON | Arranque de Keycloak | Borrar el contenedor, construir la imagen y levantarla de nuevo |
