# CVManager Hierro Pozo

Sistema Web Institucional de Gestión de Currículums Vitae y Bolsa Laboral para el **Instituto de Educación Superior Público "Manuel Antonio Hierro Pozo"** (Ayacucho – Perú).

Desarrollado para la asignatura **Lenguaje de Programación II** bajo la arquitectura estándar **MVC + DAO (Jakarta EE / Java Web)**, respetando las tecnologías fundamentales del curso.

---

## 🏛️ Institución de Referencia
* **Institución:** IESP Público "Manuel Antonio Hierro Pozo" (IDEX MAHP)
* **Ubicación:** Ayacucho, Perú
* **Sitio Web Oficial:** [tecnologicohierropozo.edu.pe](https://tecnologicohierropozo.edu.pe/)
* **Plataforma Académica:** SIGAEST

---

## 🚀 Tecnologías y Entorno
* **Lenguaje:** Java 17+
* **IDE Recomendado:** Apache NetBeans 23
* **Servidor de Aplicaciones:** Apache Tomcat 11 (Jakarta EE 10 / Servlet 6.0 / JSP 3.1 / JSTL 3.0)
* **Gestor de Dependencias:** Apache Maven
* **Base de Datos:** MySQL 8.0 (16 tablas relacionales normalizadas)
* **Frontend:** HTML5, CSS3, Bootstrap 5.3, FontAwesome 6, jQuery 3.7 (Ajax) y Chart.js
* **Seguridad:** BCrypt (`jBCrypt 0.4`), Filtros de Autenticación, Autorización RBAC y Codificación UTF-8

---

## 🎓 Programas de Estudios Oficiales (IDEX MAHP)
1. **Administración de Redes y Comunicaciones (ARC)**
2. **Electricidad Industrial (EI)**
3. **Gestión Administrativa (GA)**
4. **Mecatrónica Automotriz (MA)**

---

## 📋 Módulos y Funcionalidades
### 1. Módulo Público y Autenticación
* Página de inicio institucional con los 4 programas de estudio y el bloque "Por qué ser parte del IDEX MAHP".
* Pantalla de inicio de sesión inspirada en el portal oficial **SIGAEST** con fondo de la fachada del instituto, accesos a manuales y recuperación de contraseña.
* Registro diferenciado para **Egresados** y **Empresas** con validación de correo en tiempo real mediante Ajax (`/api/check-email`).
* Seguridad criptográfica con BCrypt y sesiones HTTP protegidas.

### 2. Módulo de Egresados
* Gestión de perfil profesional (foto, teléfono, ciudad, resumen ejecutivo, enlaces a LinkedIn y portafolio).
* Gestión integral del CV:
  * Formación Académica (Educación)
  * Experiencia Laboral / Prácticas
  * Habilidades Técnicas y Blandas
  * Idiomas y Niveles
  * Certificaciones y Cursos
  * Referencias Laborales y Personales
* Carga de CV en archivo PDF (`Part` / Servlet Multipart nativo).
* Opción de Publicar / Ocultar CV en la bolsa de talentos.

### 3. Módulo de Empresas Aliadas
* Buscador avanzado de egresados con filtros combinados (carrera, habilidades, año de egreso) y paginación SQL.
* Visualización detallada del perfil y currículum vitae de cada egresado.
* Marcador de candidatos favoritos con estrellas.
* Envío de solicitudes de contacto y propuestas de entrevista laboral con mensajería personalizada.

### 4. Módulo de Administración Institucional
* Dashboard estadístico interactivo con gráficos de Chart.js (egresados por programa de estudios, empresas, CVs publicados y cuentas pendientes).
* Gestión de usuarios: aprobación de cuentas de empresas, activación, suspensión y cambio de roles.
* Mantenimiento de Programas de Estudio (carreras técnicas).
* Módulo de auditoría y trazabilidad de eventos del sistema (`audit_logs`).

---

## 🛠️ Instalación y Configuración

### 1. Base de Datos
Importe el script SQL en su servidor local de MySQL:
```bash
mysql -u root -p < database/cvmanager_hierropozo_db.sql
```

Ajuste las credenciales en `src/main/resources/database.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/cvmanager_hierropozo_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
db.user=root
db.password=su_password
```

### 2. Abrir y Ejecutar en Apache NetBeans
1. Abra NetBeans: **File > Open Project** y seleccione esta carpeta.
2. Asegúrese de tener registrado Tomcat 11 en **Tools > Servers**.
3. Haga clic derecho sobre el proyecto > **Clean and Build**.
4. Ejecute el proyecto (**Run** o `F6`). El contexto por defecto es `/cvmanager`.
5. Si inicia por primera vez, ingrese a:
   `http://localhost:8080/cvmanager/setup-admin`
   para crear las credenciales del Administrador Institucional inicial.

---

## 🧪 Pruebas Unitarias
El proyecto cuenta con suite de pruebas JUnit 5:
* `DBConnectionTest`: Verificación de pool de conexiones y carga de properties.
* `UserDAOTest`: Pruebas de persistencia e integridad de usuarios.
* `PasswordUtilTest`: Cifrado y verificación de hashes BCrypt.
* `ValidationUtilTest`: Reglas de validación de correo, DNI, RUC y teléfono peruano.

Ejecutar pruebas:
```bash
mvn test
```
