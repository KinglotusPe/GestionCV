# Guía NetBeans + Tomcat 11
1. Instala JDK 17 o superior.
2. Descarga y descomprime Apache Tomcat 11.
3. NetBeans: **Tools > Servers > Add Server > Apache Tomcat or TomEE**.
4. Selecciona la carpeta de Tomcat 11 y configura credenciales si NetBeans las solicita.
5. Abre este proyecto Maven con **File > Open Project**.
6. Revisa `database.properties`.
7. Ejecuta el SQL de `/database`.
8. Haz **Clean and Build**.
9. Ejecuta el proyecto y confirma el contexto `/cvmanager`.
10. Crea el administrador en `/setup-admin`.

Si NetBeans intenta usar otro servidor: clic derecho al proyecto > **Properties > Run > Server** y selecciona Tomcat 11.
