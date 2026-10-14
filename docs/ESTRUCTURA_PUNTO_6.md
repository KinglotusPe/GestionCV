# Estructura del proyecto Maven: punto 6

El proyecto se organiza según el árbol de las páginas 12–16 de `Proyecto_final.pdf`. El nombre visible en Maven y Eclipse es `CV_Administrador`; la carpeta de trabajo y el repositorio conservan el nombre `GestionCV`. El WAR y contexto siguen siendo `cvmanager` para conservar los enlaces existentes.

## Organización principal

```text
CV_Administrador/
├── src/main/java/com/cvmanager/
│   ├── config/         DatabaseConfig, AppConfig
│   ├── controllers/    LoginServlet, RegisterServlet, CVServlet,
│   │                   ProfileServlet, SearchServlet, AdminServlet y otros
│   ├── dao/
│   │   ├── interfaces/ UsuarioDAO, EgresadosDAO, CVDAO,
│   │   │               EducaciónDAO, ExperienciaDAO y otros contratos
│   │   └── impl/       UsuarioDAOImpl, EgresadoDAOImpl, CVDAOImpl,
│   │                   EducaciónDAOImpl, ExperienciaDAOImpl y otros JDBC
│   ├── models/         User, Egresados, CV, Educación, Experiencia,
│   │                   Habilidades, Idioma, Certificacion y modelos auxiliares
│   ├── services/       Servicio_Autenticacion, CVService, BusquedaServicio,
│   │                   EmailServicio, Archivo_Servicio y otros servicios
│   ├── filters/        Autenticacion_Filtro, Autorizacion_Filtro, Codificacion_Filtro
│   ├── listeners/      AppContextListener
│   └── utils/          DBConnection, PasswordUtil, ValidacionUtil,
│                       ArchivoUtil, Constantes y utilidades adicionales
├── src/main/resources/
│   ├── database.properties
│   ├── log4j2.xml
│   └── messages.properties
├── src/main/webapp/
│   ├── WEB-INF/
│   │   ├── web.xml
│   │   ├── views/      common, auth, graduate, company, admin, public, error
│   │   ├── tags/       Etiquetas propias del sílabo
│   │   └── lib/        Dependencias que Maven incorpora al WAR
│   ├── assets/
│   │   ├── css/        style.css, admin.css, cv-Plantillas.css
│   │   ├── js/         main.js, validación.js, cv-Constructor.js
│   │   ├── images/     logo.png, default-avatar.png, backgrounds/
│   │   └── vendor/     DataTables y licencias
│   ├── uploads/        photos/, cvs/ (acceso HTTP directo bloqueado)
│   └── index.jsp
├── src/test/java/com/cvmanager/ (dao, services, utils y otras pruebas)
├── pom.xml
├── README.md
└── .gitignore
```

## Vistas del documento y uso real

| Carpeta | Archivos del punto 6 | Uso |
|---|---|---|
| common | `header.jsp`, `footer.jsp`, `nav.jsp`, `sidebar.jsp` | Componentes compartidos incluidos en las páginas |
| auth | `login.jsp`, `registro.jsp`, `Olvidado_Password.jsp` | Inicio de sesión, alta y orientación para recuperar acceso |
| graduate | `Panel.jsp`, `Perfil.jsp`, `editarPerfil.jsp`, `cv.jsp`, `editarCV.jsp` | Panel; perfil con formulario incluido; CV y edición de sus secciones |
| company | `Panel.jsp`, `Buscar.jsp`, `resultados.jsp`, `Preferencia.jsp` | Panel; filtros con resultados incluidos; favoritos y contactos |
| admin | `Panel.jsp`, `usuarios.jsp`, `Carreras.jsp`, `Reportes.jsp` | Indicadores, cuentas, carreras y reportes |
| public | `home.jsp`, `Acerca_de.jsp`, `contactos.jsp` | Inicio, explicación del sistema y contacto institucional |
| error | `404.jsp`, `500.jsp`, `403.jsp` | Errores HTTP; se conserva además `400.jsp` para validaciones |

Las rutas HTTP anteriores se conservan; cambiar un nombre de JSP no cambia la dirección pública del Servlet. Las páginas nuevas se acceden por `/about`, `/contact`, `/forgot-password`, `/graduate/dashboard` y `/company/dashboard`.

## Responsabilidades verificables

- Los contratos DAO están separados de las implementaciones. Los DAO de educación, experiencia, habilidades, idiomas y certificaciones reciben modelos tipados y reutilizan el motor de escritura transaccional; `CVService` los usa en los formularios existentes.
- `DatabaseConfig` crea la configuración del pool y `DBConnection` ofrece y cierra conexiones. `AppConfig` centraliza mensajes y la ubicación de archivos. `Constantes` aporta el límite de archivos.
- `Servicio_Autenticacion` verifica credenciales; `LoginServlet` administra la sesión. `Archivo_Servicio` comprueba autorización y coordina persistencia/archivos. `ArchivoUtil` valida los bytes y opera sobre el almacenamiento.
- `EmailServicio` prepara un borrador de recuperación para entrega manual. Se muestra en Usuarios cuando existe `CVMANAGER_PUBLIC_URL`. Tener esta clase no significa que haya envío SMTP automático: esa función permanece pendiente dentro de los requisitos funcionales adicionales.
- `validación.js` contiene validaciones y Ajax; `main.js` gestiona navegación y tablas; `cv-Constructor.js` muestra el contador de caracteres del resumen. Las hojas CSS separan estilos generales, paneles y presentación de CV.

## Almacenamiento y dependencias

Las carpetas `uploads/photos` y `uploads/cvs` están representadas en el código fuente mediante `.gitkeep`. `web.xml` bloquea `/uploads/*` y Git ignora sus contenidos. Los archivos reales siguen almacenándose en `CVMANAGER_UPLOAD_DIR/photos` y `CVMANAGER_UPLOAD_DIR/cvs`, fuera de la aplicación desplegada, y se sirven por `/files` después de verificar permisos. Esto evita publicar documentos personales o perderlos al sustituir el WAR.

`WEB-INF/lib` se completa automáticamente al empaquetar con Maven; no es necesario versionar los JAR. Las carpetas y clases adicionales conservan funciones ya implementadas y no sustituyen las solicitadas por el profesor.

## Validación

Compilar con `mvn -Pintegration verify` sobre la base aislada. El perfil `eclipse,integration` comprueba la compilación con ECJ. Al migrar desde el paquete anterior, utilizar una compilación limpia de clases y un WAR nuevo para no mezclar ambos paquetes. No eliminar el `target` completo si contiene la base de pruebas; retirar únicamente los directorios generados de compilación.

Resultado verificado: 78 nombres de archivo del árbol presentes, 47 pruebas Java y 157 verificaciones HTTP aprobadas. La comprobación del WAR confirma que no incluye clases residuales del paquete anterior.
