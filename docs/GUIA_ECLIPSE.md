# Eclipse, MySQL y Tomcat

El proyecto incluye `.project`, `.classpath` y `.settings` para Eclipse con Maven y Web Tools. Java 17 es el nivel de compilación; el servidor es Tomcat 11 con Jakarta Servlet 6.1. No usar un servidor basado en `javax.servlet`.

1. Instalar Eclipse IDE for Enterprise Java and Web Developers y un JDK compatible con Tomcat 11 (17 o superior).
2. En **File → Import → Maven → Existing Maven Projects**, seleccionar la raíz de GestionCV y su `pom.xml`.
3. En **Preferences → Java → Installed JREs**, registrar el JDK. Asociar JavaSE-17 en Execution Environments. Ejecutar **Maven → Update Project**.
4. Crear la base MySQL e importar `database/cvmanager_hierropozo_db.sql`, siguiendo README. Si existe una instalación anterior, aplicar las migraciones en orden sin reimportar el esquema completo.
5. Configurar en el proceso del servidor `CVMANAGER_DB_URL`, `CVMANAGER_DB_USER`, `CVMANAGER_DB_PASSWORD`, `CVMANAGER_UPLOAD_DIR` y, solo para el primer administrador, `CVMANAGER_SETUP_TOKEN`. No guardar contraseñas en archivos versionados.
6. Registrar Tomcat 11 en **Servers → New → Server** si la instalación de Web Tools ofrece ese adaptador. Añadir el proyecto con contexto `cvmanager`. Si el adaptador no está disponible, ejecutar Maven desde Eclipse y desplegar el WAR en una instancia externa de Tomcat 11; no cambiar a Tomcat 9.
7. Crear una configuración **Run As → Maven build**, objetivo `verify`. El archivo desplegable es `target/cvmanager.war`. Abrir `http://localhost:8080/cvmanager` (ajustar el puerto de esa instancia).

## Verificación con el compilador de Eclipse

```text
mvn -Peclipse verify
```

Utiliza ECJ a través de `plexus-compiler-eclipse` y genera `target/eclipse/cvmanager.war`. Para incluir integración JDBC, configurar una base aislada cuyo nombre termine en `_test` y ejecutar:

```text
mvn -Peclipse,integration verify
```

La compilación ECJ y sus pruebas se ejecutaron satisfactoriamente. La importación mediante la interfaz gráfica de Eclipse no fue ejecutada en esta máquina; esta guía permite reproducirla. No ejecutar `clean` sobre el `target` principal mientras aloje la base y el servidor aislados de desarrollo.

Referencia: [importación Maven en Eclipse](https://eclipse.dev/m2e/documentation/m2e-faq.html).
