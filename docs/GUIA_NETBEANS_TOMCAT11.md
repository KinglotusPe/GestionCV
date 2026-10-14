# NetBeans y Tomcat 11

1. Registrar un JDK 17 o superior y Tomcat 11 en NetBeans, en Tools → Servers.
2. Abrir esta carpeta como proyecto Maven.
3. Preparar MySQL: instalación nueva con `database/cvmanager_hierropozo_db.sql`; instalación existente con los dos archivos de `database/migrations`, en orden, después de respaldar.
4. Configurar `CVMANAGER_DB_URL`, `CVMANAGER_DB_USER`, `CVMANAGER_DB_PASSWORD` y `CVMANAGER_UPLOAD_DIR` en el entorno del proceso de Tomcat. También puede usar `database.properties` para la conexión local.
5. Para la primera instalación, establecer `CVMANAGER_SETUP_TOKEN` con una clave aleatoria de al menos 24 caracteres. Si NetBeans inicia Tomcat, NetBeans debe heredar estas variables: cerrarlo y abrirlo desde el entorno configurado.
6. En Properties → Run, seleccionar Tomcat 11 y el contexto `/cvmanager`.
7. Ejecutar Clean and Build. Las pruebas unitarias no necesitan MySQL.
8. Ejecutar el proyecto y abrir `/cvmanager/setup-admin`. Introducir la clave de instalación y crear el administrador. Si ya existe uno, usar `/cvmanager/login`.
9. Desde Usuarios, activar egresados y empresas pendientes. La gestión incluye roles, estados y enlaces de recuperación.

El WAR se genera en `target/cvmanager.war`. Guardar la carpeta de archivos fuera del despliegue evita perder fotos y PDF al actualizarlo.

Para validar también la persistencia, ejecutar `mvn -Pintegration verify` apuntando a una base aislada cuyo nombre termine en `_test`. El README detalla las variables, migraciones y pruebas HTTP.
