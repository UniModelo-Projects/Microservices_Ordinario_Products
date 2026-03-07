# Product Microservice

Servicio encargado de la gestión del catálogo de productos.

## Detalles Técnicos
- **Puerto:** 8081
- **Base de Datos:** MongoDB (colección `productos`).
- **Logs:** Envía logs al log group `producto-log-group` en CloudWatch (LocalStack).

## Endpoints (vía Gateway)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/productos` | Listar todos los productos. |
| `GET` | `/productos/{id}` | Obtener un producto por su ID. |
| `POST` | `/productos` | Crear un nuevo producto. |
| `PUT` | `/productos/{id}` | Actualizar un producto existente. |
| `DELETE` | `/productos/{id}` | Eliminar un producto. |
