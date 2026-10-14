# CVManager Hierro Pozo

Sistema institucional de currículums y bolsa laboral para el IESP Manuel Antonio Hierro Pozo, Ayacucho.

## Tecnologías

Java 17+, Maven, Tomcat 11 (Servlet 6.1 / JSP 4.0), JSTL 3, MySQL 8, JDBC con pool HikariCP, BCrypt, Log4j2 y JUnit 5. Interfaz JSP con Bootstrap, FontAwesome, jQuery y Chart.js. Arquitectura MVC con DAO y servicios.

## Funciones por rol

| Rol | Funciones |
| --- | --- |
| Público | Página institucional, registro como egresado o empresa, inicio de sesión y recuperación mediante enlace privado. |
| Egresado | Editar perfil, foto, carrera y enlaces; crear, editar y eliminar educación, experiencia, habilidades, idiomas, certificaciones y referencias; subir, descargar y eliminar PDF; publicar u ocultar CV; responder propuestas; buscar vacantes, postular y retirar postulaciones; cambiar contraseña. |
| Empresa | Editar datos corporativos y RUC; buscar candidatos por texto, carrera, habilidad, ciudad y año; guardar favoritos; solicitar contacto; publicar, editar y cerrar vacantes; revisar postulaciones y actualizar estados; cambiar contraseña. |
| Administrador | Aprobar, suspender y desactivar cuentas; cambiar roles conservando perfiles previos; crear, editar, desactivar y reactivar carreras; consultar estadísticas y auditoría; generar enlaces de recuperación de un solo uso. |

El registro de egresados y empresas requiere activación administrativa. Los nuevos perfiles necesarios para un cambio de rol se crean en la misma transacción; los datos de roles anteriores se conservan. No se permite retirar el acceso al administrador que está realizando la operación ni al último administrador activo.

## 🔑 Credenciales de Acceso para Pruebas / Demostración

Para que el docente, compañeros y evaluadores puedan probar todas las funciones de inmediato, la base de datos incluye cuentas precargadas y activas para los tres roles:

| Rol | Correo Electrónico | Contraseña | Destino al ingresar |
| :--- | :--- | :--- | :--- |
| **Administrador** | `admin@hierropozo.edu.pe` | `Admin123!` | `/admin/dashboard` |
| **Egresado** | `egresado@hierropozo.edu.pe` | `Egresado123!` | `/graduate/profile` |
| **Empresa** | `empresa@hierropozo.edu.pe` | `Empresa123!` | `/company/search` |

### Contacto y privacidad

- La búsqueda y el acceso ordinario al CV requieren perfil público, CV publicado y cuenta activa de egresado.
- La solicitud de contacto muestra teléfono y correo a la empresa después de ser aceptada. El egresado puede rechazarla posteriormente para revocar ese acceso.
- El PDF y las referencias se habilitan a empresas con contacto aceptado o una postulación vigente a una vacante propia.
- Al postular, el egresado confirma que comparte CV, PDF y contacto con esa empresa aunque el perfil sea privado. Retirar la postulación revoca ese permiso; otros permisos de contacto previamente aceptados permanecen vigentes.
- Las postulaciones son únicas por candidato y vacante. Sus estados son pendiente, en evaluación, aceptada, rechazada y retirada. Una postulación retirada no puede reactivarse desde la empresa.
- Las solicitudes y postulaciones se consultan dentro de la aplicación. No se envían correos automáticamente.

### Archivos y seguridad

Las fotos JPG/PNG se decodifican y guardan como PNG, con límite de 16 megapíxeles. Los PDF se verifican por sus marcadores de formato y se descargan como adjuntos. El límite es 5 MB por archivo. Los archivos permanecen fuera del directorio público y se sirven mediante `/files`, comprobando permisos en cada descarga. Sustituir o eliminar un archivo retira el anterior.

Todas las operaciones POST requieren token CSRF, incluido el inicio de sesión. La sesión cambia de identificador al autenticarse; la aplicación vuelve a comprobar estado, rol y versión de contraseña en cada petición. Suspender una cuenta, cambiar su rol o restablecer su contraseña invalida sus sesiones anteriores. El cierre de sesión utiliza POST. Las cookies tienen HttpOnly y SameSite=Lax.

## Instalación nueva

1. Instalar JDK 17+, Maven, MySQL 8 y Tomcat 11.
2. Importar `database/cvmanager_hierropozo_db.sql`. El script crea 23 tablas y los cuatro programas institucionales; no elimina bases existentes.
3. Configurar la conexión con las variables siguientes o con `src/main/resources/database.properties`:

| Variable | Uso |
| --- | --- |
| `CVMANAGER_DB_URL` | URL JDBC de MySQL. |
| `CVMANAGER_DB_USER` | Usuario de la base de datos. |
| `CVMANAGER_DB_PASSWORD` | Contraseña de la base de datos. |
| `CVMANAGER_UPLOAD_DIR` | Carpeta persistente de archivos; por defecto, `cvmanager_uploads` dentro del directorio personal del usuario que ejecuta Tomcat. |
| `CVMANAGER_SETUP_TOKEN` | Clave de instalación de al menos 24 caracteres para crear el primer administrador. |

Las propiedades JVM `db.url`, `db.user` y `db.password` tienen prioridad sobre las variables de entorno; se usan también en pruebas aisladas. El pool utiliza hasta 10 conexiones y espera hasta 5 segundos para obtener una conexión disponible.

4. Ejecutar `mvn verify` y desplegar `target/cvmanager.war` en Tomcat.
5. Abrir `/cvmanager/setup-admin` y proporcionar la clave de instalación, correo y contraseña. Esta ruta permanece cerrada si no se configura la clave y se bloquea después de crear el primer administrador. El bloqueo de MySQL evita altas simultáneas de administradores iniciales.
6. Iniciar sesión y aprobar las cuentas registradas desde Usuarios. Retirar la variable de instalación después de crear al administrador.

Para un despliegue público, terminar HTTPS en Tomcat o en un proxy configurado correctamente y usar credenciales de base de datos propias de la aplicación. Conservar y respaldar tanto MySQL como la carpeta de archivos.

## Actualizar una instalación existente

Hacer una copia de seguridad, seleccionar la base existente en MySQL y aplicar, en orden:

1. `database/migrations/001_complete_flows.sql`: tabla de recuperación de contraseña y normalización de documentos vacíos.
2. `database/migrations/002_jobs.sql`: vacantes y postulaciones.
3. `database/migrations/003_labor_portal.sql`: convocatorias ampliadas, carreras destinatarias, documentos y revisiones de empresas.
4. Desplegar el nuevo WAR y conservar la carpeta de archivos original.

Las migraciones son repetibles y no eliminan las tablas anteriores. Las sesiones de la versión anterior deberán iniciar sesión nuevamente. No es necesario volver a crear al administrador.

## Recuperación de contraseña

El administrador verifica la identidad del titular y genera un enlace desde Usuarios → Administrar → Generar enlace de recuperación. Lo entrega por un canal privado. Caduca en 30 minutos, se almacena únicamente su hash y se consume una sola vez. Generar otro enlace o cambiar la contraseña invalida los anteriores. Cada usuario autenticado también puede cambiar su contraseña desde Mi cuenta proporcionando la contraseña actual.

## Pruebas

```text
mvn test
```

Ejecuta 29 pruebas unitarias sin MySQL: validaciones, BCrypt, campos de CV y rechazo de archivos inválidos.

```text
mvn -Pintegration verify
```

Incluye 18 pruebas de integración, para un total de 47, y genera el WAR. Antes de ejecutarlo, importar el esquema en una base aislada cuyo nombre termine en `_test` y apuntar las variables `CVMANAGER_DB_*` a ella. Las pruebas de escritura crean usuarios con identificadores únicos y los eliminan al terminar. No usar una base productiva.

## Estructura

- `src/main/java/com/cvmanager/controllers`: rutas y formularios.
- `src/main/java/com/cvmanager/services`: registro, CV, cuentas, vacantes, búsquedas y estadísticas.
- `src/main/java/com/cvmanager/dao`: persistencia JDBC.
- `src/main/java/com/cvmanager/filters`: seguridad, sesión y autorización.
- `src/main/webapp/WEB-INF/views`: vistas protegidas.
- `database/migrations`: actualización incremental.
- `src/test`: pruebas unitarias y de integración.

Las guías de IDE están en `docs/GUIA_ECLIPSE.md` y `docs/GUIA_NETBEANS_TOMCAT11.md`. La matriz de temas, evidencias y guion de sustentación está en `docs/CUMPLIMIENTO_SILABO.md`.

## Bolsa laboral institucional

La interfaz autenticada usa menú lateral, barra superior, tablas con búsqueda, ordenamiento y paginación, y una adaptación responsive para móviles. Las funciones de CV y los tres roles originales se conservan.

- **Convocatorias:** fechas de inicio y cierre, turno, cantidad de puestos, requisitos, funciones, beneficios, condiciones, enlace HTTP/HTTPS y varios programas destinatarios. Sin programas seleccionados significa cualquier carrera; son destinatarios informativos, no una exclusión automática de candidatos.
- **Vigencia:** por comenzar, en proceso, finalizada y archivada. Solo se puede postular en proceso, con empresa activa y consentimiento. Las fechas límite son inclusivas según la fecha de MySQL. Archivar oculta la convocatoria al egresado; conserva su historial de postulaciones y permite restaurarla.
- **Administración:** `/admin/jobs` permite crear convocatorias para empresas activas y archivar/restaurar las existentes. Cada convocatoria conserva su administrador: la empresa edita las propias; el administrador institucional edita las que creó. La evaluación de candidatos permanece en la empresa destinataria.
- **Documentos:** PDF de hasta 5 MB por archivo; alta y eliminación solo por quien administra la convocatoria. Descarga autenticada, validación de formato y almacenamiento fuera del directorio web. No se copia documentación de SIGAEST.
- **Empresas:** `/admin/companies` permite aprobar, rechazar/desactivar y suspender con sustento obligatorio e historial. El registro se realiza desde el formulario existente. La gestión general de usuarios conserva sus permisos administrativos anteriores.
- **Reportes:** `/admin/reports` ofrece convocatorias por fecha de registro, candidatos únicos por empresa o carrera y listado de postulaciones por fecha de envío. Exportación XLSX real, sin fórmulas ejecutables provenientes de texto de usuario. PDF mediante imprimir/guardar como PDF del navegador; no generación directa en servidor.

Referencia funcional y límites: `docs/BOLSA_LABORAL.md`. Esta ampliación no sustituye los requisitos aún pendientes del documento del profesor, como verificación por correo y recuperación automática por correo.

## Estructura del punto 6

El proyecto se identifica como `CV_Administrador` y usa el paquete `com.cvmanager`. Los contratos están en `dao/interfaces` y las implementaciones JDBC en `dao/impl`. La correspondencia completa de archivos y las decisiones de despliegue se documentan en [ESTRUCTURA_PUNTO_6.md](docs/ESTRUCTURA_PUNTO_6.md).

Para preparar un borrador de correo de recuperación en Usuarios, configurar `CVMANAGER_PUBLIC_URL` con la URL pública del contexto (por ejemplo, `https://instituto.example/cvmanager`). El administrador revisa y entrega el mensaje; no hay envío SMTP automático.
