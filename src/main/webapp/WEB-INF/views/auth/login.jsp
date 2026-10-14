<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jspf" %>

<div class="login-bg-wrapper my-3">
    <div class="login-portal-card shadow-lg">
        <div class="row g-0">
            <!-- Columna Izquierda: Formulario de Inicio de Sesión -->
            <div class="col-lg-6 login-col-form">
                <div class="text-center mb-4">
                    <img src="${pageContext.request.contextPath}/assets/images/logo_hierro_pozo.png"
                         alt="Logo Hierro Pozo"
                         class="mb-2"
                         style="width: 85px; height: 85px; object-fit: contain;">
                    <h3 class="fw-bold text-dark mt-2 mb-1" style="font-size: 1.55rem;">Iniciar Sesión</h3>
                    <p class="text-muted small mb-0">Plataforma Institucional CVManager</p>
                </div>

                <c:if test="${param.auth == '1'}">
                    <div class="alert alert-warning py-2 small d-flex align-items-center mb-3" role="alert">
                        <i class="fa-solid fa-triangle-exclamation me-2"></i>
                        <div>Debe autenticarse para acceder a este módulo.</div>
                    </div>
                </c:if>

                <c:if test="${param.adminCreated == '1'}">
                    <div class="alert alert-success py-2 small d-flex align-items-center mb-3" role="alert">
                        <i class="fa-solid fa-circle-check me-2"></i>
                        <div>Administrador creado con éxito. Inicie sesión.</div>
                    </div>
                </c:if>

                <c:if test="${param.passwordChanged == '1'}"><div class="alert alert-success">Contraseña actualizada. Inicie sesión.</div></c:if><form method="post" action="${pageContext.request.contextPath}/login" novalidate>
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <div class="mb-3">
                        <input class="form-control login-input-clean"
                               type="email"
                               name="email"
                               placeholder="Correo electrónico" aria-label="Correo electrónico"
                               required
                               autofocus>
                    </div>

                    <div class="mb-2">
                        <input class="form-control login-input-clean"
                               type="password"
                               name="password"
                               placeholder="Contraseña" aria-label="Contraseña"
                               required>
                    </div>

                    <div class="text-center my-3">
                        <a href="#" class="text-decoration-none small text-muted" data-bs-toggle="modal" data-bs-target="#modalRecuperar">
                            ¿Olvidaste tu contraseña?
                        </a>
                    </div>

                    <button class="btn btn-login-action w-100 py-2 fw-bold" type="submit">
                        INGRESAR
                    </button>
                </form>

                <div class="text-center pt-3 border-top mt-3">
                    <div class="d-flex align-items-center justify-content-center gap-2 mb-1">
                        <span class="badge bg-primary text-white fw-bold px-2 py-1" style="font-size: 0.72rem; letter-spacing: 0.5px;">MAHP</span>
                        <span class="text-secondary small fw-bold">CVMANAGER</span>
                    </div>
                    <small class="text-muted" style="font-size: 0.72rem;">Portal de empleabilidad - IDEX MAHP</small>
                </div>
            </div>

            <!-- Columna Derecha: ¿Eres nuevo aquí? y Accesos a Manuales (Fiel al portal oficial) -->
            <div class="col-lg-6 login-col-info">
                <h2 class="fw-bold mb-2 text-white" style="font-size: 1.85rem;">¿Eres nuevo aquí?</h2>
                <p class="text-white-50 small mb-4">
                    Revise nuestros manuales
                </p>

                <div class="d-flex flex-column gap-2 px-md-3">
                    <button type="button" class="btn btn-info-pill" data-bs-toggle="modal" data-bs-target="#modalManualEstudiante">
                        Manual de estudiante
                    </button>
                    <button type="button" class="btn btn-info-pill" data-bs-toggle="modal" data-bs-target="#modalManualDocente">
                        Manual de administración
                    </button>
                    <button type="button" class="btn btn-info-pill" data-bs-toggle="modal" data-bs-target="#modalManualBolsa">
                        Manual bolsa laboral
                    </button>
                    <button type="button" class="btn btn-info-pill" data-bs-toggle="modal" data-bs-target="#modalRecuperar">
                        Manual recuperar contraseña
                    </button>

                    <hr class="my-2 border-white opacity-25">

                    <a href="${pageContext.request.contextPath}/register" class="btn btn-warning text-dark fw-bold rounded-pill py-2 shadow-sm">
                        <i class="fa-solid fa-user-plus me-1"></i> Registrarse como Egresado o Empresa
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modal: Manual Estudiante / Egresado -->
<div class="modal fade" id="modalManualEstudiante" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold"><i class="fa-solid fa-book-open me-2"></i> Manual de Estudiante / Egresado</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <ol class="small text-secondary mb-3 ps-3">
                    <li class="mb-2"><strong>Registro:</strong> Cree su cuenta con correo, contraseña, nombres y apellidos. Espere la aprobación del administrador.</li>
                    <li class="mb-2"><strong>Perfil Profesional:</strong> Complete su resumen ejecutivo, teléfono y enlaces a LinkedIn o portafolio.</li>
                    <li class="mb-2"><strong>Secciones del CV:</strong> Ingrese su formación académica, experiencia de prácticas/empleo, habilidades técnicas, idiomas y certificaciones.</li>
                    <li class="mb-2"><strong>Visibilidad Laboral:</strong> Publique su CV y habilite su perfil público para aparecer en las búsquedas. Responda propuestas y postule a vacantes desde su menú.</li>
                </ol>
                <div class="text-end">
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-sm fw-bold">
                        Ir al Registro
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modal: Manual Docente / Administrador -->
<div class="modal fade" id="modalManualDocente" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-dark text-white">
                <h5 class="modal-title fw-bold"><i class="fa-solid fa-chalkboard-user me-2"></i> Manual del Administrador</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <p class="small text-muted mb-3">
                    Las cuentas con rol Administrador pueden gestionar usuarios, carreras y estadísticas desde el módulo institucional.
                </p>
                <ul class="small text-secondary mb-0 ps-3">
                    <li>Consulta de egresados por carrera, cuentas, vacantes y postulaciones.</li>
                    <li>Activación de cuentas, cambios de rol y recuperación de contraseñas.</li>
                    <li>Consulta del historial de accesos y operaciones realizadas.</li>
                </ul>
            </div>
        </div>
    </div>
</div>

<!-- Modal: Manual Bolsa Laboral -->
<div class="modal fade" id="modalManualBolsa" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title fw-bold"><i class="fa-solid fa-briefcase me-2"></i> Manual Bolsa Laboral para Empresas</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <ol class="small text-secondary mb-3 ps-3">
                    <li class="mb-2"><strong>Registro Empresarial:</strong> Registre su correo y razón social. Tras la aprobación, complete RUC y datos corporativos en Mi empresa.</li>
                    <li class="mb-2"><strong>Buscador de Talentos:</strong> Filtre egresados por programa de estudio, habilidades y año de graduación.</li>
                    <li class="mb-2"><strong>Guardar Favoritos:</strong> Marque a los candidatos de interés con estrella para un seguimiento ágil.</li>
                    <li class="mb-2"><strong>Contacto Directo:</strong> Envíe propuestas y espere la aceptación para acceder al contacto y al PDF. En Vacantes puede publicar ofertas y evaluar postulaciones.</li>
                </ol>
            </div>
        </div>
    </div>
</div>

<!-- Modal: Recuperar Contraseña -->
<div class="modal fade" id="modalRecuperar" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-warning text-dark">
                <h5 class="modal-title fw-bold"><i class="fa-solid fa-key me-2"></i> Recuperación de Contraseña</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <p class="small text-muted mb-3">
                    Por políticas de seguridad institucional del IESP "Manuel Antonio Hierro Pozo", si ha olvidado su contraseña:
                </p>
                <p>Contacte al administrador institucional para verificar su identidad. Desde Gestión de usuarios podrá generar un enlace privado de recuperación, válido durante 30 minutos y para un solo uso.</p>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jspf" %>
