# Bitácora de implementación — EVOX SPACE

## 2026-11-05

### Actualización del runtime
- Se actualizó el release de compilación de Maven de JDK 17 a JDK 25 LTS.
- Se utilizará Eclipse Temurin JDK 25 (`C:\Program Files\Eclipse Adoptium\jdk-25.0.5.8-hotspot`) para compilar y ejecutar las pruebas.
- Se agregó el acceso demo de administrador y su panel de operación en la aplicación Swing.
- Se agregó la tabla `mensajes` y paneles interactivos para comunicación cliente-administrador.
- Se renovó la interfaz Swing con tema visual consistente, botones estilizados, tablas legibles y pestañas mejoradas.
- Se agregó registro de cuentas desde el login y administración interactiva de roles CLIENTE, MECANICO y ADMINISTRADOR.
- Se separó el panel de mecánico con consulta y actualización operativa de órdenes de reparación.
- El registro de clientes ahora captura teléfono, modelo de auto, falla y contexto; administración puede convertir solicitudes en casos con vehículo y orden.

### Requerimiento leído
Se leyó `Readme.MD` y se identificaron cuatro entregables: esquema SQL, diseño textual de clases, modelo Java principal y ejemplos DAO/Repository con JDBC.

### Trabajo realizado
- Se creó inicialmente un proyecto Maven compatible con JDK 17.
- Se implementó la jerarquía `Usuario`, `Cliente`, `Mecanico` y `Administrador`.
- Se implementaron `VehiculoElectrico`, `Diagnostico`, `Cotizacion`, `OrdenReparacion`, `PiezaRefaccion` y `DocumentoEvidencia`.
- Se agregaron enums para estados y tipos de falla.
- Se agregaron validaciones para correo, VIN, importes, fechas y campos obligatorios.
- Se definieron excepciones personalizadas para clientes no encontrados y vehículos registrados.
- Se crearon interfaces de repositorio y sus implementaciones JDBC.
- Se creó el esquema relacional en `src/main/resources/db/schema.sql`, con PK, FK, restricciones `UNIQUE` y relaciones de dominio.
- Se añadieron pruebas unitarias para validar el flujo básico de una orden y el rechazo de un VIN inválido.
- Se instaló inicialmente Eclipse Temurin JDK 17 y Maven 3.9.9 en el equipo.
- Se habilitó H2 como dependencia de ejecución y se agregó `DatabaseConnection`, con una BD local por defecto en `./data/evoxspace`.
- Se agregó `DatabaseInitializer` para aplicar automáticamente `src/main/resources/db/schema.sql`.

### Validación
- La validación inicial se ejecutó con JDK 17; las dependencias se descargaron desde Maven Central.
- La prueba de conexión H2 local quedó incluida junto con las pruebas del modelo.
- Resultado: `Tests run: 4, Failures: 0, Errors: 0` y `BUILD SUCCESS`.

### Pendientes para producción
- Conectar las credenciales de MySQL o PostgreSQL mediante variables de entorno.
- Sustituir el almacenamiento de contraseñas de demostración por un algoritmo de hash seguro como Argon2 o BCrypt.
- Implementar autenticación, UI y servicios de aplicación completos.
- Añadir migraciones versionadas y pruebas de integración con la base de datos objetivo.
