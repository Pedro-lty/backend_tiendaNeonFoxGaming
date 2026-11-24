# Backend Neon Fox Gaming

Aplicación Spring Boot para administrar el catálogo, usuarios y pedidos de la tienda.

## Ejecutar

Desde esta carpeta (`backend/neonfoxgaming`), con Java 21 instalado y las variables `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` en `.env`:

```powershell
.\mvnw.cmd spring-boot:run
```

Swagger UI: `http://localhost:8080/swagger-ui.html`  
OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Al primer inicio, si no hay productos, se cargan en PostgreSQL los 12 productos que usa el frontend, con stock inicial 10 y sus categorías. Las tablas se actualizan con JPA (`ddl-auto=update`).

## Versiones

- v1 devuelve JSON directo: `/api/v1/...`
- v2 entrega representaciones HAL con enlaces: `/api/v2/...`

Recursos: `productos`, `categorias`, `usuarios`, `pedidos` y `auth`.

La lectura pública del catálogo, el registro/inicio de sesión y la creación de pedidos están disponibles sin autenticación. Las operaciones administrativas usan HTTP Basic y requieren un usuario con rol `Admin`. El inicializador crea las cuentas de demostración que ya existen en el frontend si aún no están en la base:

- `admin@neonfox.com` / `admin123`
- `cliente@neonfox.com` / `cliente123`

Las contraseñas se almacenan con BCrypt. El pago del frontend es una simulación; el endpoint de pedidos registra una compra confirmada y vuelve a validar el stock y el precio en la base antes de descontarlo.

## Pruebas

```powershell
.\mvnw.cmd test
```

Las pruebas usan una base H2 en memoria y no escriben en Neon.
