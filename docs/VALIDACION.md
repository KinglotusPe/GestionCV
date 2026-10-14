# Validación de los flujos completos

Fecha: 8 de octubre de 2026.

## Entorno verificado

- Compilación con nivel Java 17 y Maven incluido en NetBeans.
- Ejecución en Java 26, Tomcat 11.0.24 y MySQL 8.0.46.
- MySQL de pruebas independiente en `127.0.0.1:33316`, base `cvmanager_test`.
- Tomcat de pruebas independiente en `127.0.0.1:18081`.
- Archivos de pruebas en `target/test-uploads`.
- No se modificó la base habitual ni el despliegue habitual de Tomcat.

## Resultados

| Validación | Resultado |
| --- | --- |
| `mvn test` | 21 pruebas unitarias aprobadas. |
| `mvn -Pintegration verify` | 32 pruebas aprobadas, 0 fallos y 0 errores. Incluye las 21 unitarias. |
| `python scripts/smoke_test.py --isolated` | 104 comprobaciones HTTP aprobadas contra Tomcat y MySQL reales. |
| Empaquetado Maven | WAR generado en `target/cvmanager.war`. |
| Pantalla de acceso | Revisada visualmente en navegador. |

## Cobertura de los flujos HTTP

- Registro de dos egresados y una empresa, activación y acceso por rol.
- Rechazo de POST sin CSRF y de acceso a administración desde una cuenta de egresado.
- Guardado de perfiles con documento vacío, rechazo de DNI inválido y escape de texto en atributos HTML.
- Alta, edición, carga del formulario y eliminación de las seis secciones del CV.
- Rechazo de cambios sobre secciones de otro egresado.
- Publicación y despublicación del CV; búsqueda por texto y año; favoritos.
- Solicitudes de contacto, bandeja del egresado, aceptación y revocación de los datos compartidos.
- Subida, descarga y eliminación de PDF y fotos; rechazo de un PDF falso; revocación de descargas.
- Guardado de perfil corporativo.
- Alta y cierre de vacantes, acceso al formulario de edición, postulación y evaluación.
- Acceso a un CV privado mediante postulación consentida; revocación al retirarla.
- Rechazo de postulaciones a vacantes cerradas.
- Alta, edición, desactivación y reactivación de carreras.
- Protección del administrador que realiza la operación.
- Suspensión de una empresa e invalidación de su sesión abierta.
- Generación y uso de enlace de recuperación, rechazo de reutilización y revocación de sesiones previas.
- Cambio de rol con creación de perfil corporativo y nuevo inicio de sesión.
- Cambio de contraseña desde Mi cuenta y cierre de sesión.
- Carga de páginas de auditoría y estadísticas.

Las pruebas son una validación funcional y de regresión; no constituyen una auditoría externa de seguridad ni una prueba de carga. El acceso público requiere configurar el despliegue y aplicar las migraciones descritas en el README. Las notificaciones se muestran dentro del sistema; no existe envío automático de correos.


## Ampliación de bolsa laboral — 13 de octubre de 2026

- 42 pruebas Java aprobadas (25 unitarias y 17 de integración), sin fallos ni errores.
- 136 comprobaciones HTTP aprobadas en la instancia aislada; incluyen las 104 anteriores y 32 adicionales de bolsa laboral.
- Migración 003 aplicada a la base de pruebas; esquema principal actualizado para instalaciones nuevas.
- Verificados: fechas invertidas, convocatorias futuras/finalizadas/archivadas, pertenencia y administrador de convocatoria, varias carreras, rollback ante carrera inválida, documentos PDF y descarga autorizada, revisión de empresas con motivo, cuatro reportes y archivo XLSX válido.
- Interfaz inspeccionada en navegador a tamaño escritorio y 390 × 844; menú lateral, listado y navegación móvil.
- La exportación PDF se realiza mediante imprimir/guardar PDF del navegador. No se incorporó un generador PDF en el servidor.
- No se modificó SIGAEST, la base habitual ni el Tomcat habitual. WAR actualizado disponible en `target/cvmanager.war`.

## Adecuación al sílabo (13/10/2026)

46 pruebas Java aprobadas (28 unitarias y 18 de integración), tanto con javac como con ECJ mediante el perfil eclipse. 144 verificaciones HTTP aprobadas sobre Tomcat 11 y MySQL aislados. Incluyen Ajax con JSON, validación, CSRF, roles, persistencia del historial y DisplayTag. La prueba de rollback comprueba que un fallo al insertar el historial no altera el estado de la empresa. Véase CUMPLIMIENTO_SILABO.md para la correspondencia completa.

## Reorganización del punto 6 (13/10/2026)

Los 78 nombres de archivo indicados en el árbol del documento están presentes. Se migró a com.cvmanager, se separaron interfaces e implementaciones DAO y se conectaron los modelos y componentes JSP. Pasaron 47 pruebas Java (29 unitarias y 18 de integración), incluidas referencias de archivos JSP, y 157 comprobaciones HTTP. La compilación ECJ también pasó 47 pruebas. Se comprobó en el navegador el texto UTF-8, DataTables y una revisión Ajax de la empresa de demostración. El WAR no contiene clases del paquete anterior.
