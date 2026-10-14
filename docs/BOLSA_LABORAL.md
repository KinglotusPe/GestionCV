# Adaptación de la bolsa laboral institucional

## Referencia y alcance

El 13 de octubre de 2026 se observaron, con autorización y sin modificar registros, las vistas de estudiante y docente de [SIGAEST Hierro Pozo](https://iestpmahp.sistema.edu.pe/login/). Se consultó también el [manual publicado de bolsa laboral](https://drive.google.com/file/d/1sTfRsgjxtxVx7-mBXG6mVGZBX-rTuZL8/view).

Se adaptaron los patrones visuales —menú lateral oscuro, barra superior clara, paneles, tablas, filtros y acciones verdes— mediante código propio Java/JSP/CSS/JavaScript. Se mantienen la identidad CVManager y los recursos institucionales que ya estaban en el repositorio. No se copiaron credenciales, registros personales, código privado ni documentos de la otra plataforma.

La cuenta docente consultada no tenía administración laboral. Las referencias administrativas provienen del manual, no de una comprobación de esas pantallas en producción. Se conservan los roles egresado, empresa y administrador exigidos por el proyecto; no se incorpora un sistema académico de matrícula, notas o docencia.

## Funciones integradas

| Función | Implementación y límites |
| --- | --- |
| Convocatorias | Detalles, requisitos, funciones, beneficios, condiciones, ciudad, modalidad, turno, remuneración, cantidad de puestos, inicio/cierre y enlace externo validado. |
| Programas destinatarios | Relación de varias carreras por convocatoria; sin selección significa cualquiera. Orientación informativa; no restringe automáticamente quién puede postular. |
| Estados | Por comenzar, en proceso y finalizada se calculan con las fechas y cierre manual. Archivo/restauración conserva datos y postulaciones. La base MySQL determina el día de vigencia. |
| Documentos | PDF de hasta 5 MB, alta/baja por administrador de la convocatoria, descarga autenticada. Un egresado no descarga documentos de ofertas archivadas o empresas deshabilitadas. |
| Administración institucional | Alta para empresas activas, edición de las convocatorias creadas por el mismo administrador; archivo/restauración de todas. La empresa conserva evaluación de candidatos a sus puestos. |
| Empresas | Registro público existente, revisión con sustento, activación, rechazo/desactivación y suspensión. Historial de decisiones. La pantalla general de usuarios sigue permitiendo cambios de acceso con auditoría general. |
| Reportes | Cuatro reportes con fechas: convocatorias registradas, candidatos únicos por empresa, candidatos únicos por carrera y listado de postulaciones. Los conteos de candidatos excluyen retiradas; el listado mantiene historial. |
| Exportación | XLSX real de una hoja; valores de usuario como texto, sin fórmulas. PDF mediante la función de impresión del navegador. |
| Tablas | Búsqueda de texto, ordenamiento y paginación local de 10/25/50/100 filas. La consulta de convocatorias sigue cargando el conjunto filtrado: no es paginación SQL para grandes volúmenes. |
| Privacidad | Se conserva el consentimiento al postular y su revocación al retirar la postulación. Archivar una oferta no equivale a retirar una postulación ni revoca ese consentimiento por sí solo. |

## Actualización

1. Respaldar base y archivos.
2. Aplicar `database/migrations/003_labor_portal.sql` en la base habitual, después de las migraciones 001 y 002. Es aditiva y repetible; incorpora cuatro tablas y migra las carreras de vacantes existentes.
3. Desplegar `target/cvmanager.war` con Tomcat 11. Mantener las variables de conexión y `CVMANAGER_UPLOAD_DIR`.

El esquema de instalación nueva ya incluye estas tablas. En esta tarea solo se aplicó la migración a `cvmanager_test`, en MySQL local de pruebas, y se desplegó en el Tomcat aislado del puerto 18081. No se actualizó la instalación habitual ni se publicó en Internet.

## Relación con los requisitos del profesor

Esta ampliación cubre la referencia laboral, no toda la rúbrica: siguen pendientes, entre otros, la verificación institucional por correo, recuperación automática por correo, filtros avanzados de candidatos, catálogo maestro de habilidades, moderación de CV, tasa real de empleabilidad y pruebas de carga. Los reportes de postulaciones no demuestran contrataciones ni una tasa de empleo.
