Actúa como un Desarrollador Senior de Software en Java y Arquitecto de Sistemas. Necesito crear una aplicación completa orientada a objetos para la materia de Programación Orientada a Objetos (POO). 

El proyecto consiste en el sistema de gestión de un taller mecánico especializado en vehículos eléctricos llamado "EVOX SPACE". 

Por favor, genera la arquitectura del proyecto en Java, el modelo de clases aplicando principios SOLID y POO (encapsulamiento estricto, herencia, polimorfismo, abstracción e interfaces), el esquema de Base de Datos SQL y el código base inicial para las funcionalidades solicitadas.

---

### 1. CONTEXTO TÉCNICO
- **Lenguaje:** Java (JDK 25 LTS)
- **Paradigma:** Programación Orientada a Objetos pura
- **Persistencia / BD:** MySQL / PostgreSQL (usando JDBC o JPA/Hibernate)
- **Estructura del Proyecto:** Proyecto estructurado por paquetes (ej. `com.evoxspace.model`, `com.evoxspace.repository`, `com.evoxspace.service`, `com.evoxspace.ui`)

---

### 2. ROLES Y FUNCIONALIDADES

#### A. Módulo de Cliente
- **Autenticación:** Login y Registro de nuevos clientes.
- **Dashboard del Cliente:**
  - Consulta del estatus de su vehículo (En Diagnóstico, Esperando Piezas, En Reparación, Listo para Entrega).
  - Consulta de Cotizaciones asociadas (monto total, desglose de mano de obra/piezas, estado de aprobación).
  - Rastreo de piezas solicitadas (fecha estimada de llegada y nombre de la refacción).
  - Mecánico/Técnico asignado al vehículo (nombre y especialidad).
  - Historial de servicios o reparaciones previas del vehículo.

#### B. Módulo de Administrador / Mecánico
- **Gestión de Clientes y Vehículos:** Registro, edición y vinculación de vehículos eléctricos a sus respectivos clientes.
- **Gestión de Expediente / Carpeta Digital del Cliente:** Vinculación de información técnica y rutas de documentos a cada servicio:
  1. Falla Diagnosticada (código DTC / falla en batería de alto voltaje, inversor, motor síncrono, etc.).
  2. Contexto del momento donde ocurrió la falla (descripción del usuario, condiciones de carga/manejo).
  3. Cotización detallada.
  4. Diagrama técnico del vehículo o sistema afectado.
- **Asignación de Personal:** Asignación de mecánico responsable al trabajo.
- **Actualización de Estatus:** Modificación del estado de avance y actualización de fechas de entrega de refacciones.

---

### 3. REQUERIMIENTOS ESPECÍFICOS DE POO EN JAVA
1. **Jerarquía de Clases e Interfaces:**
   - Clase abstracta o base `Usuario` de la que hereden `Cliente` y `Administrador`/`Mecanico`.
   - Interfaces para operaciones clave (ej. `GestorDocumental`, `Notificable` o `ServicioPersistencia`).
   - Clases de dominio bien definidas: `VehiculoElectrico`, `Diagnostico`, `Cotizacion`, `OrdenReparacion`, `PiezaRefaccion`, `DocumentoEvidencia`.
2. **Encapsulamiento y Validaciones:** 
   - Atributos `private` o `protected`.
   - Métodos `getter` y `setter` con validaciones de datos (ej. formato de correo, VIN del vehículo).
   - Uso de `enums` para estados (ej. `EstadoReparacion`, `TipoFalla`).
3. **Manejo de Excepciones:** Definición y uso de excepciones personalizadas (ej. `ClienteNoEncontradoException`, `VehiculoRegistradoException`).

---

### 4. LO QUE NECESITO QUE GENERES
1. **Esquema SQL de la Base de Datos** (Tablas, llaves primarias `PK` y foráneas `FK`).
2. **Diagrama textual / Estructura de Clases en Java** detallando atributos (`private`), constructores, métodos y relaciones entre clases (Herencia, Composición, Agregación).
3. **Código Java completo del Modelo de Clases principal** (Clase `Usuario`, `Cliente`, `VehiculoElectrico`, `OrdenReparacion`).
4. **Ejemplo del Patrón DAO / Repository en Java** para gestionar el registro de un cliente y la consulta del estatus de reparación desde la base de datos.

---

### 5. ESTADO DE LA CONEXIÓN A BASE DE DATOS

El proyecto ya tiene una conexión JDBC funcional para desarrollo local:

- Motor utilizado por defecto: H2.
- URL por defecto: `jdbc:h2:file:./data/evoxspace;AUTO_SERVER=TRUE`.
- Clase de conexión: `com.evoxspace.repository.jdbc.DatabaseConnection`.
- Inicialización del esquema: `com.evoxspace.repository.jdbc.DatabaseInitializer`.
- Esquema: `src/main/resources/db/schema.sql`.

La conexión puede cambiarse sin modificar el código mediante las variables de entorno `EVOX_DB_URL`, `EVOX_DB_USER` y `EVOX_DB_PASSWORD`. MySQL o PostgreSQL todavía no están vinculados; para producción se debe elegir uno de ellos y agregar su driver JDBC y credenciales.

### Ejecución local

La aplicación de consola inicia la base H2, muestra un menú para registrar y buscar clientes
y permite consultar el estado de una orden por UUID.

Desde la raíz del proyecto:

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.5.8-hotspot"
mvn test
mvn exec:java -Dexec.mainClass="com.evoxspace.EvoxSpaceApplication"
```

Para comprobar el arranque sin interacción:

```powershell
mvn exec:java -Dexec.mainClass="com.evoxspace.EvoxSpaceApplication" -Dexec.args="--demo"
```

### Demo visual de escritorio

También existe una demo gráfica Swing para visualizar el flujo del cliente:

```powershell
mvn exec:java -Dexec.mainClass="com.evoxspace.EvoxSpaceDesktopApplication"
```

La demo incluye acceso, cierre de sesión y dashboard con información de vehículo,
estado de reparación, cotización, técnico y refacción.

Credenciales del demo:

- Cliente: `demo@evoxspace.com` / `demo`
- Administrador: `admin@evoxspace.com` / `admin`

En el panel de administrador, las pestañas **Clientes** y **Órdenes de reparación**
consultan la base H2 local. Puedes ordenar las tablas, actualizar los datos, registrar
clientes y editar el cliente seleccionado; los cambios se guardan inmediatamente.

La pestaña **Mensajes** permite comunicación persistente entre cliente,
administrador y mecánico. El cliente puede elegir entre enviar el mensaje a
`admin@evoxspace.com` o al mecánico asignado a su orden. El panel del mecánico
incluye la vista **Mensajes de clientes** para consultar y responder la
conversación. En **Cuentas y roles**, el administrador también puede ver el
mecánico asignado a cada cliente.

La interfaz Swing utiliza un tema visual teal/azul oscuro, tablas con encabezados
resaltados, filas espaciadas, botones diferenciados y navegación por pestañas.

## Instalación en otra computadora

### Requisitos del sistema

- Sistema operativo Windows, macOS o Linux.
- **JDK 25 LTS** (no solamente un JRE), porque el proyecto compila con `release 25`.
- **Apache Maven 3.9.9 o superior**.
- Conexión a Internet durante la primera compilación para descargar dependencias.
- Para la aplicación gráfica, un entorno con soporte para Swing/AWT.

Verifica las instalaciones con:

```powershell
java -version
mvn -version
```

La salida debe indicar Java 25 y una versión válida de Maven. En Windows se
recomienda configurar `JAVA_HOME` apuntando a la carpeta raíz del JDK, no a su
subcarpeta `bin`:

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.5.8-hotspot"
```

En macOS o Linux:

```bash
export JAVA_HOME="/ruta/al/jdk-25"
export PATH="$JAVA_HOME/bin:$PATH"
```

### Bibliotecas y dependencias

No es necesario instalar manualmente archivos `.jar`. Maven descarga las
dependencias declaradas en [`pom.xml`](./pom.xml) y las guarda en el repositorio
local del usuario (`.m2/repository`):

| Dependencia | Versión | Uso |
|---|---:|---|
| H2 Database | 2.2.224 | Base de datos local JDBC |
| JUnit Jupiter | 5.10.2 | Pruebas automatizadas |
| Maven Surefire Plugin | 3.2.5 | Ejecución de pruebas |
| Exec Maven Plugin | 3.5.0 | Inicio de las aplicaciones |

Swing y AWT ya forman parte del JDK, por lo que no requieren una biblioteca
adicional. La primera ejecución puede tardar más mientras Maven descarga los
artefactos desde Maven Central.

### Descargar, compilar y probar

Clona o copia el proyecto y entra en su carpeta raíz, donde se encuentra
`pom.xml`:

```powershell
git clone <URL_DEL_REPOSITORIO>
Set-Location <CARPETA_DEL_PROYECTO>

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.5.8-hotspot"
mvn clean test
```

Si no utilizas Git, descomprime el proyecto y ejecuta los comandos desde la
carpeta que contiene `pom.xml`. El comando `clean test` recompila todo desde
cero y ejecuta las pruebas del modelo y de la conexión H2.

### Ejecutar la aplicación gráfica

```powershell
mvn exec:java -Dexec.mainClass="com.evoxspace.EvoxSpaceDesktopApplication"
```

Credenciales incluidas únicamente para la demo:

```text
Cliente:
  Correo: demo@evoxspace.com
  Contraseña: demo

Administrador:
  Correo: admin@evoxspace.com
  Contraseña: admin
```

La aplicación gráfica permite consultar y editar clientes, consultar órdenes de
reparación, enviar mensajes entre cliente y administrador y administrar cuentas.

Desde la pantalla de inicio puedes pulsar **Crear nueva cuenta**. Las cuentas
nuevas se registran inicialmente con rol `CLIENTE`. El administrador puede abrir
la pestaña **Cuentas y roles**, consultar todos los registros y cambiar el rol
de una cuenta a `CLIENTE`, `MECANICO` o `ADMINISTRADOR`.

El registro de una cuenta cliente solicita también:

- Número de teléfono.
- Modelo del auto.
- Falla presentada.
- Contexto breve de cuándo y cómo ocurrió la falla.

Estos datos se guardan como una **solicitud de cliente** pendiente. El
administrador puede abrir la pestaña **Solicitudes nuevas**, revisar la
información y pulsar **Generar caso**. Esa acción crea el cliente, registra el
vehículo y genera una orden de reparación en estado `EN_DIAGNOSTICO`; después
marca la solicitud como `ATENDIDA`.

El formulario de registro se muestra en una ventana independiente con scroll
vertical. Los botones **Cancelar** y **Crear cuenta y enviar** permanecen
siempre visibles en la parte inferior, incluso en pantallas pequeñas.

Las cuentas con rol `MECANICO` ingresan a un panel operativo separado. Desde allí
pueden consultar las órdenes de reparación, revisar vehículo, cliente, estado y
fechas, y actualizar el estado de cada orden sin acceder a la administración de
cuentas o roles.

### Ejecutar la aplicación de consola

```powershell
mvn exec:java -Dexec.mainClass="com.evoxspace.EvoxSpaceApplication"
```

Para validar el inicio sin interacción:

```powershell
mvn exec:java `
  -Dexec.mainClass="com.evoxspace.EvoxSpaceApplication" `
  -Dexec.args="--demo"
```

En macOS o Linux, el mismo comando puede escribirse en una sola línea:

```bash
mvn exec:java -Dexec.mainClass="com.evoxspace.EvoxSpaceApplication" -Dexec.args="--demo"
```

### Base de datos local

Por defecto, H2 utiliza una base de datos en:

```text
./data/evoxspace
```

El esquema se crea automáticamente desde
[`src/main/resources/db/schema.sql`](./src/main/resources/db/schema.sql). La
carpeta `data` contiene información local de desarrollo y debe conservarse si
se desean mantener los registros entre ejecuciones. Para comenzar con una base
vacía, detén la aplicación y elimina únicamente esa carpeta.

La conexión puede personalizarse sin cambiar el código:

```powershell
$env:EVOX_DB_URL = "jdbc:h2:file:./data/evoxspace;AUTO_SERVER=TRUE"
$env:EVOX_DB_USER = "sa"
$env:EVOX_DB_PASSWORD = ""
```

El proyecto está preparado actualmente para H2. MySQL y PostgreSQL requieren
agregar su driver JDBC, configurar sus credenciales y adaptar el esquema antes
de usarse en producción.

### Cotizaciones detalladas

El administrador dispone de la pestaña **Cotizaciones**. Para agregar un
desglose debe copiar el ID de una orden desde **Órdenes de reparación** y
capturar:

- Tipo: `MANO_OBRA` o `PIEZA`.
- Descripción del trabajo o refacción.
- Costo individual.
- Fecha estimada de llegada en formato `AAAA-MM-DD`.

Cada concepto se guarda en `cotizacion_detalles`. El cliente puede revisar los
conceptos, costos, fechas de llegada y estado desde la pestaña **Cotizaciones**
de su dashboard. Al seleccionar un concepto puede aprobar la cotización
completa de la orden; la aprobación queda registrada en `cotizaciones`.

### Documentos externos de cotización

El administrador también puede abrir la pestaña **Documentos**, indicar el ID
de la orden y pulsar **Subir documento** para adjuntar un PDF, imagen u otro
archivo recibido de un proveedor externo. El archivo se copia a
`data/cotizaciones` y queda asociado a la cotización en la tabla
`cotizacion_documentos`.

El cliente puede abrir la pestaña **Documentos**, actualizar la lista y pulsar
**Abrir documento seleccionado** para visualizar el archivo con la aplicación
predeterminada del sistema.

### Control ascendente de casos

Cada orden de reparación tiene un número de caso entero autoincremental,
visible como **ID** en los paneles y comenzando en 1. Este número es el
identificador que deben utilizar los administradores y mecánicos en las
acciones de cotizaciones, documentos y estados. La base conserva además un
identificador interno UUID para mantener sus relaciones y referencias.

El VIN mostrado para cada vehículo utiliza como máximo 7 caracteres. Los VIN
existentes se recortan automáticamente a esa longitud al iniciar la aplicación.

### Generar casos demo

Para poblar la base H2 con cinco casos completos de prueba, ejecuta:

```powershell
mvn exec:java -Dexec.mainClass="com.evoxspace.DatosDemoGenerator"
```

Cada caso incluye cliente, vehículo, mecánico, orden, diagnóstico, cotización
desglosada y refacción. El comando genera nuevos datos cada vez que se ejecuta.

La tabla `usuarios` centraliza el acceso y contiene el rol de cada cuenta. Las
credenciales de esta demo se almacenan de forma simple para facilitar la
práctica; antes de producción deben reemplazarse por hash seguro (Argon2 o
BCrypt), sesiones y autorización por permisos.

### Solución de problemas

- **`mvn` no se reconoce:** instala Maven o agrega su carpeta `bin` al `PATH`.
- **`release version 25 not supported`:** Maven está usando un JDK anterior;
  revisa `JAVA_HOME` y vuelve a ejecutar `mvn -version`.
- **La ventana gráfica no aparece:** verifica que estés en un entorno con
  escritorio y soporte Swing/AWT; no ejecutes la demo desde un servidor sin
  interfaz gráfica.
- **Error de tabla existente:** la versión actual usa `CREATE TABLE IF NOT EXISTS`;
  confirma que no haya otra instancia bloqueando la base H2.
- **Dependencias no descargadas:** comprueba la conexión a Internet y vuelve a
  ejecutar `mvn clean test`.

### Acceso directo en el escritorio de Windows

El proyecto incluye [`run-evoxspace.cmd`](./run-evoxspace.cmd), que configura
Java 25 y Maven y abre la interfaz gráfica. Puedes crear un acceso directo
haciendo clic derecho sobre ese archivo y seleccionando **Enviar a > Escritorio
(crear acceso directo)**.

También puedes crear un acceso directo con PowerShell:

```powershell
$project = "C:\Users\Mauri\Desktop\POO"
$desktop = [Environment]::GetFolderPath("Desktop")
$shell = New-Object -ComObject WScript.Shell
$shortcut = $shell.CreateShortcut((Join-Path $desktop "EVOX SPACE.lnk"))
$shortcut.TargetPath = Join-Path $project "run-evoxspace.cmd"
$shortcut.WorkingDirectory = $project
$shortcut.Description = "Abrir EVOX SPACE"
$shortcut.IconLocation = "$env:SystemRoot\System32\SHELL32.dll,13"
$shortcut.Save()
```

Después, abre **EVOX SPACE.lnk** desde el escritorio. El acceso directo necesita
que Java 25 y Maven estén instalados en las rutas indicadas en
`run-evoxspace.cmd`.