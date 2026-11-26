# Backend Neon Fox Gaming

## Ejecutar

Swagger UI: `http://localhost:8080/swagger-ui.html`  
OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Versiones

- v1 devuelve JSON directo: `/api/v1/...`
- v2 entrega representaciones HAL con enlaces: `/api/v2/...`

Recursos: `productos`, `categorias`, `usuarios`, `pedidos` y `auth`.

Usuarios registrador predeterminados:
- `admin@neonfox.com` / `admin123`
- `cliente@neonfox.com` / `cliente123`

Las contraseñas se almacenan con BCrypt. El pago del frontend es una simulación, el endpoint de pedidos registra una compra confirmada y vuelve a validar el stock y el precio en la base antes de descontarlo.
