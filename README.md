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
2. Importar `database/cvmanager_hierropozo_db.sql`. El script crea 19 tablas y los cuatro programas institucionales; no elimina bases existentes.
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
3. Desplegar el nuevo WAR y conservar la carpeta de archivos original.

Las migraciones son repetibles y no eliminan las tablas anteriores. Las sesiones de la versión anterior deberán iniciar sesión nuevamente. No es necesario volver a crear al administrador.

## Recuperación de contraseña

El administrador verifica la identidad del titular y genera un enlace desde Usuarios → Administrar → Generar enlace de recuperación. Lo entrega por un canal privado. Caduca en 30 minutos, se almacena únicamente su hash y se consume una sola vez. Generar otro enlace o cambiar la contraseña invalida los anteriores. Cada usuario autenticado también puede cambiar su contraseña desde Mi cuenta proporcionando la contraseña actual.

## Pruebas

```text
mvn test
```

Ejecuta 21 pruebas unitarias sin MySQL: validaciones, BCrypt, campos de CV y rechazo de archivos inválidos.

```text
mvn -Pintegration verify
```

Incluye 11 pruebas de integración, para un total de 32, y genera el WAR. Antes de ejecutarlo, importar el esquema en una base aislada cuyo nombre termine en `_test` y apuntar las variables `CVMANAGER_DB_*` a ella. Las pruebas de escritura crean usuarios con identificadores únicos y los eliminan al terminar. No usar una base productiva.

También se incluye `scripts/smoke_test.py`, que verifica los flujos HTTP reales en un Tomcat local aislado:

```text
python scripts/smoke_test.py --isolated --base http://127.0.0.1:18081/cvmanager
```

La instancia de pruebas debe tener esquema importado y `CVMANAGER_SETUP_TOKEN` configurado. Crear `target/test-config.json` con el campo `setupToken` de esa instancia. En una base vacía el script crea su administrador y guarda las credenciales de prueba en ese archivo ignorado por Git. Las ejecuciones posteriores reutilizan ese administrador y crean cuentas de prueba nuevas. Los archivos deben apuntar a una carpeta de pruebas independiente. El resultado se guarda en `target/http-test-results.json`.

## Estructura

- `src/main/java/.../controller`: rutas y formularios.
- `src/main/java/.../service`: registro, CV, cuentas, vacantes, búsquedas y estadísticas.
- `src/main/java/.../dao`: persistencia JDBC.
- `src/main/java/.../filter`: seguridad, sesión y autorización.
- `src/main/webapp/WEB-INF/views`: vistas protegidas.
- `database/migrations`: actualización incremental.
- `src/test`: pruebas unitarias y de integración.
- `scripts/smoke_test.py`: pruebas de flujos sobre HTTP.

La guía para el IDE está en `docs/GUIA_NETBEANS_TOMCAT11.md`.
