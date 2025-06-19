# Sistema de Kardex

Sistema de gestión de inventario (Kardex) desarrollado con Spring Boot Reactive y MongoDB.

## Descripción

Este sistema permite gestionar el inventario de productos, proveedores y usuarios en una empresa. Utiliza tecnologías reactivas para proporcionar una alta escalabilidad y rendimiento.

## Tecnologías Utilizadas

- **Spring Boot 3.4.5**: Framework para el desarrollo de aplicaciones Java
- **Spring WebFlux**: Framework reactivo para aplicaciones web
- **Spring Data MongoDB Reactive**: Para la persistencia de datos reactiva con MongoDB
- **Spring Security**: Para la autenticación y autorización
- **JWT (JSON Web Token)**: Para la gestión de tokens de autenticación
- **Swagger/OpenAPI**: Para la documentación de la API
- **Lombok**: Para reducir el código boilerplate

## Estructura del Proyecto

```
src/main/java/pe/edu/vallegrande/kardex/
├── controller/         # Controladores REST reactivos
│   ├── AuthController.java
│   ├── InventarioController.java
│   ├── ProductoController.java
│   ├── ProveedorController.java
│   ├── ReporteController.java
│   └── UsuarioController.java
├── dto/                # Objetos de transferencia de datos
├── model/              # Entidades del dominio
├── repository/         # Repositorios reactivos para MongoDB
├── security/           # Configuración de seguridad y JWT
│   ├── CustomReactiveAuthenticationManager.java
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtTokenProvider.java
│   └── SecurityConfig.java
├── service/            # Servicios con lógica de negocio
└── KardexApplication.java  # Clase principal
```

## Entidades Principales

### Producto

Representa los productos en el inventario con atributos como SKU, nombre, marca, categoría, stock, precios, etc.

### Inventario

Registra los movimientos de inventario (compras, ventas, devoluciones, traslados) con información como SKU del producto, cantidad, fecha y tipo de transacción.

### Proveedor

Almacena la información de los proveedores con datos como RUC, razón social, contacto, dirección, etc.

### Usuario

Gestiona los usuarios del sistema con roles (ADMIN, SUPERVISOR, USUARIO) y autenticación.

## Endpoints API

### Productos

- `GET /api/productos`: Obtener todos los productos activos
- `GET /api/productos/{id}`: Obtener producto por ID
- `GET /api/productos/sku/{sku}`: Obtener producto por SKU
- `GET /api/productos/categoria/{categoria}`: Obtener productos por categoría
- `GET /api/productos/marca/{marca}`: Obtener productos por marca
- `GET /api/productos/stock-bajo`: Obtener productos con stock bajo
- `POST /api/productos`: Crear un nuevo producto
- `PUT /api/productos/{id}`: Actualizar un producto existente
- `DELETE /api/productos/{id}`: Eliminar lógicamente un producto
- `GET /api/productos/stream`: Stream de productos (SSE)
- `GET /api/productos/reporte/stock-bajo`: Reporte de productos con stock bajo

### Inventario

- `GET /api/inventario`: Obtener todos los movimientos de inventario
- `GET /api/inventario/{id}`: Obtener movimiento por ID
- `GET /api/inventario/producto/{skuProducto}`: Obtener movimientos por SKU de producto
- `GET /api/inventario/tipo/{tipoTransaccion}`: Obtener movimientos por tipo de transacción
- `GET /api/inventario/periodo`: Obtener movimientos por período de tiempo
- `POST /api/inventario`: Crear un nuevo movimiento de inventario
- `PUT /api/inventario/{id}`: Actualizar un movimiento existente
- `DELETE /api/inventario/{id}`: Eliminar lógicamente un movimiento
- `GET /api/inventario/stream`: Stream de movimientos (SSE)
- `GET /api/inventario/reporte/periodo`: Reporte de movimientos por período
- `GET /api/inventario/reporte/producto/{skuProducto}`: Reporte de movimientos por producto

### Proveedores

- `GET /api/proveedores`: Obtener todos los proveedores activos
- `GET /api/proveedores/{id}`: Obtener proveedor por ID
- `GET /api/proveedores/ruc/{ruc}`: Obtener proveedor por RUC
- `GET /api/proveedores/razon-social/{razonSocial}`: Obtener proveedores por razón social
- `GET /api/proveedores/nombre-comercial/{nombreComercial}`: Obtener proveedores por nombre comercial
- `POST /api/proveedores`: Crear un nuevo proveedor
- `PUT /api/proveedores/{id}`: Actualizar un proveedor existente
- `DELETE /api/proveedores/{id}`: Eliminar lógicamente un proveedor
- `GET /api/proveedores/stream`: Stream de proveedores (SSE)
- `GET /api/proveedores/reporte/pais/{pais}`: Reporte de proveedores por país

### Usuarios

- `GET /api/usuarios`: Obtener todos los usuarios activos
- `GET /api/usuarios/{id}`: Obtener usuario por ID
- `GET /api/usuarios/email/{email}`: Obtener usuario por email
- `GET /api/usuarios/rol/{rol}`: Obtener usuarios por rol
- `POST /api/usuarios`: Crear un nuevo usuario
- `PUT /api/usuarios/{id}`: Actualizar un usuario existente
- `DELETE /api/usuarios/{id}`: Eliminar lógicamente un usuario
- `PUT /api/usuarios/{id}/toggle-activation`: Activar/desactivar un usuario
- `GET /api/usuarios/stream`: Stream de usuarios (SSE)
- `GET /api/usuarios/reporte/rol/{rol}`: Reporte de usuarios por rol

## Validaciones

El sistema implementa validaciones en la capa de servicio para garantizar la integridad de los datos:

- Validación de campos obligatorios
- Validación de formatos (email, RUC)
- Validación de existencia de registros relacionados
- Validación de stock suficiente para ventas
- Validación de duplicados (SKU, RUC, email)

## Reportes

El sistema ofrece endpoints específicos para la generación de reportes:


## Configuración

### Requisitos

- Java 17 o superior
- MongoDB 4.4 o superior
- Maven 3.6 o superior

### Propiedades de Configuración

```properties
# Configuración de la aplicación
spring.application.name=kardex
server.port=8080

# Configuración de MongoDB
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=kardexdb

# Configuración de JWT
jwt.secret=ClaveSecretaParaGenerarTokensJWTEnElSistemaKardexDeVallegrande2023
jwt.expiration=86400000

# Configuración de Swagger/OpenAPI
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

## Ejecución

1. Asegúrate de tener MongoDB en ejecución
2. Clona el repositorio
3. Ejecuta `mvn clean install` para compilar el proyecto
4. Ejecuta `mvn spring-boot:run` para iniciar la aplicación
5. Accede a la documentación de la API en `http://localhost:8080/swagger-ui.html`

## Seguridad

El sistema utiliza Spring Security con JWT para la autenticación y autorización:

- Los endpoints públicos son `/api/auth/**` y la documentación de Swagger
- El resto de endpoints requieren autenticación
- Los tokens JWT tienen una validez de 24 horas

### Componentes de Seguridad

- **JwtTokenProvider**: Genera y valida tokens JWT, extrae claims y crea objetos de autenticación.
- **JwtAuthenticationFilter**: Filtra las peticiones HTTP para validar el token JWT y establecer el contexto de seguridad.
- **CustomReactiveAuthenticationManager**: Gestiona la autenticación de usuarios verificando credenciales.
- **CustomUserDetailsService**: Carga los detalles del usuario desde la base de datos para la autenticación.
- **SecurityConfig**: Configura las reglas de seguridad, filtros y permisos de acceso a endpoints.

## Características Reactivas

El sistema aprovecha las capacidades reactivas de Spring WebFlux y MongoDB:

- Operaciones no bloqueantes para mayor rendimiento
- Soporte para Server-Sent Events (SSE) para streaming de datos
- Manejo eficiente de la concurrencia

## Guía para Probar Endpoints en Postman

### Configuración Inicial

1. **URL Base**: `http://localhost:8080`
2. **Headers Comunes**:
   - Content-Type: application/json
   - Accept: application/json

### Productos

#### Obtener todos los productos
- **Método**: GET
- **URL**: `http://localhost:8080/api/productos`
- **Descripción**: Devuelve todos los productos activos en el sistema

#### Obtener producto por ID
- **Método**: GET
- **URL**: `http://localhost:8080/api/productos/{id}`
- **Ejemplo**: `http://localhost:8080/api/productos/64a7b1d2e3f4c5d6a7b8c9d0`

#### Obtener producto por SKU
- **Método**: GET
- **URL**: `http://localhost:8080/api/productos/sku/{sku}`
- **Ejemplo**: `http://localhost:8080/api/productos/sku/PROD-001`

#### Crear nuevo producto
- **Método**: POST
- **URL**: `http://localhost:8080/api/productos`
- **Body** (JSON):
```json
{
  "sku": "PROD-001",
  "nombre": "Laptop HP ProBook",
  "marca": "HP",
  "categoria": "Computadoras",
  "paisOrigen": "Estados Unidos",
  "codigoQR": "QR-PROD-001",
  "activo": true,
  "especificacionesTecnicas": "Procesador i7, 16GB RAM, 512GB SSD",
  "stockActual": 10,
  "stockMinimo": 3,
  "stockMaximo": 20,
  "ubicacionAlmacen": "Estante A-12",
  "precioCompra": 1200.00,
  "precioVentaSugerido": 1500.00,
  "precioVentaFinal": 1550.00,
  "imagenUrl": "https://ejemplo.com/imagen.jpg",
  "isDeleted": false
}
```

#### Actualizar producto
- **Método**: PUT
- **URL**: `http://localhost:8080/api/productos/{id}`
- **Ejemplo**: `http://localhost:8080/api/productos/64a7b1d2e3f4c5d6a7b8c9d0`
- **Body** (JSON):
```json
{
  "sku": "PROD-001",
  "nombre": "Laptop HP ProBook 450 G8",
  "marca": "HP",
  "categoria": "Computadoras",
  "paisOrigen": "Estados Unidos",
  "codigoQR": "QR-PROD-001",
  "activo": true,
  "especificacionesTecnicas": "Procesador i7-1165G7, 16GB RAM, 512GB SSD",
  "stockActual": 15,
  "stockMinimo": 5,
  "stockMaximo": 25,
  "ubicacionAlmacen": "Estante A-15",
  "precioCompra": 1250.00,
  "precioVentaSugerido": 1550.00,
  "precioVentaFinal": 1600.00,
  "imagenUrl": "https://ejemplo.com/imagen_actualizada.jpg",
  "isDeleted": false
}
```

#### Eliminar producto
- **Método**: DELETE
- **URL**: `http://localhost:8080/api/productos/{id}`
- **Ejemplo**: `http://localhost:8080/api/productos/64a7b1d2e3f4c5d6a7b8c9d0`

### Inventario

#### Obtener todos los movimientos
- **Método**: GET
- **URL**: `http://localhost:8080/api/inventario`

#### Obtener movimiento por ID
- **Método**: GET
- **URL**: `http://localhost:8080/api/inventario/{id}`
- **Ejemplo**: `http://localhost:8080/api/inventario/64a7b1d2e3f4c5d6a7b8c9d1`

#### Obtener movimientos por producto
- **Método**: GET
- **URL**: `http://localhost:8080/api/inventario/producto/{skuProducto}`
- **Ejemplo**: `http://localhost:8080/api/inventario/producto/PROD-001`

#### Obtener movimientos por tipo
- **Método**: GET
- **URL**: `http://localhost:8080/api/inventario/tipo/{tipoTransaccion}`
- **Ejemplo**: `http://localhost:8080/api/inventario/tipo/COMPRA`
- **Valores posibles para tipoTransaccion**: COMPRA, VENTA, DEVOLUCION, TRASLADO

#### Obtener movimientos por período
- **Método**: GET
- **URL**: `http://localhost:8080/api/inventario/periodo?inicio={fechaInicio}&fin={fechaFin}`
- **Ejemplo**: `http://localhost:8080/api/inventario/periodo?inicio=2023-01-01T00:00:00&fin=2023-12-31T23:59:59`
- **Formato de fecha**: ISO 8601 (YYYY-MM-DDThh:mm:ss)

#### Crear nuevo movimiento
- **Método**: POST
- **URL**: `http://localhost:8080/api/inventario`
- **Body** (JSON):
```json
{
  "skuProducto": "PROD-001",
  "cantidad": 5,
  "fechaTransaccion": "2023-06-15T10:30:00",
  "tipoTransaccion": "COMPRA",
  "documentoAsociado": "F001-123",
  "isDeleted": false
}
```

#### Actualizar movimiento
- **Método**: PUT
- **URL**: `http://localhost:8080/api/inventario/{id}`
- **Ejemplo**: `http://localhost:8080/api/inventario/64a7b1d2e3f4c5d6a7b8c9d1`
- **Body** (JSON):
```json
{
  "skuProducto": "PROD-001",
  "cantidad": 3,
  "fechaTransaccion": "2023-06-15T10:30:00",
  "tipoTransaccion": "COMPRA",
  "documentoAsociado": "F001-123-A",
  "isDeleted": false
}
```

#### Eliminar movimiento
- **Método**: DELETE
- **URL**: `http://localhost:8080/api/inventario/{id}`
- **Ejemplo**: `http://localhost:8080/api/inventario/64a7b1d2e3f4c5d6a7b8c9d1`

### Proveedores

#### Obtener todos los proveedores
- **Método**: GET
- **URL**: `http://localhost:8080/api/proveedores`

#### Obtener proveedor por ID
- **Método**: GET
- **URL**: `http://localhost:8080/api/proveedores/{id}`
- **Ejemplo**: `http://localhost:8080/api/proveedores/64a7b1d2e3f4c5d6a7b8c9d2`

#### Obtener proveedor por RUC
- **Método**: GET
- **URL**: `http://localhost:8080/api/proveedores/ruc/{ruc}`
- **Ejemplo**: `http://localhost:8080/api/proveedores/ruc/20123456789`

#### Crear nuevo proveedor
- **Método**: POST
- **URL**: `http://localhost:8080/api/proveedores`
- **Body** (JSON):
```json
{
  "ruc": "20123456789",
  "razonSocial": "Distribuidora Comercial S.A.",
  "nombreComercial": "DICOMSA",
  "contactoPrincipal": "Juan Pérez",
  "emailContacto": "contacto@dicomsa.com",
  "telefonoContacto": "987654321",
  "direccionFiscal": "Av. Industrial 123",
  "ciudad": "Lima",
  "pais": "Perú",
  "terminosPago": "30 días",
  "observaciones": "Proveedor principal de materiales de oficina",
  "isDeleted": false
}
```

#### Actualizar proveedor
- **Método**: PUT
- **URL**: `http://localhost:8080/api/proveedores/{id}`
- **Ejemplo**: `http://localhost:8080/api/proveedores/64a7b1d2e3f4c5d6a7b8c9d2`
- **Body** (JSON):
```json
{
  "ruc": "20123456789",
  "razonSocial": "Distribuidora Comercial S.A.C.",
  "nombreComercial": "DICOMSA",
  "contactoPrincipal": "Carlos Rodríguez",
  "emailContacto": "carlos@dicomsa.com",
  "telefonoContacto": "987654322",
  "direccionFiscal": "Av. Industrial 123, Piso 4",
  "ciudad": "Lima",
  "pais": "Perú",
  "terminosPago": "45 días",
  "observaciones": "Proveedor principal actualizado",
  "isDeleted": false
}
```

#### Eliminar proveedor
- **Método**: DELETE
- **URL**: `http://localhost:8080/api/proveedores/{id}`
- **Ejemplo**: `http://localhost:8080/api/proveedores/64a7b1d2e3f4c5d6a7b8c9d2`

### Reportes

#### Reporte de movimientos por fecha
- **Método**: GET
- **URL**: `http://localhost:8080/api/reportes/movimientos?fechaInicio={fechaInicio}&fechaFin={fechaFin}`
- **Ejemplo**: `http://localhost:8080/api/reportes/movimientos?fechaInicio=2023-01-01&fechaFin=2023-12-31`
- **Formato de fecha**: YYYY-MM-DD

#### Reporte de stock actual
- **Método**: GET
- **URL**: `http://localhost:8080/api/reportes/stock`

#### Reporte de productos con stock bajo
- **Método**: GET
- **URL**: `http://localhost:8080/api/reportes/stock-bajo`

#### Reporte de valorización de inventario
- **Método**: GET
- **URL**: `http://localhost:8080/api/reportes/valorizacion`

### Consejos para Probar en Postman

1. **Crear una colección**: Organiza tus peticiones en una colección llamada "Sistema Kardex".

2. **Configurar variables de entorno**:
   - Crea un entorno "Kardex Local" con la variable `baseUrl` = `http://localhost:8080`
   - Usa la variable en tus peticiones: `{{baseUrl}}/api/productos`

3. **Guardar IDs dinámicamente**:
   - Después de crear un producto, guarda su ID en una variable:
   ```javascript
   pm.environment.set("productoId", pm.response.json().id);
   ```
   - Usa la variable en peticiones posteriores: `{{baseUrl}}/api/productos/{{productoId}}`

4. **Probar flujos completos**:
   - Crea un producto
   - Consulta el producto creado
   - Actualiza el producto
   - Registra movimientos de inventario para ese producto
   - Elimina el producto

5. **Verificar respuestas**:
   - Añade tests para verificar códigos de estado:
   ```javascript
   pm.test("Estado 200 OK", function () {
       pm.response.to.have.status(200);
   });
   ```

## Autor

Desarrollado por el equipo de Vallegrande para el sistema de Kardex.