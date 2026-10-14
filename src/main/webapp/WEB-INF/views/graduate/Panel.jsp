<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="../common/header.jsp" %>

<div class="container py-4">
    <div class="p-4 mb-4 bg-light rounded-3 border">
        <h1 class="display-6 fw-bold text-success">
            <i class="fa-solid fa-graduation-cap me-2"></i>Panel del Egresado
        </h1>
        <p class="lead mb-0 text-muted">
            Bienvenido a tu portal profesional. Administra tu currículum, explora oportunidades laborales y gestiona tus contactos con empresas.
        </p>
    </div>

    <div class="row g-4">
        <div class="col-md-6 col-lg-3">
            <div class="card h-100 shadow-sm border-0 border-top border-4 border-primary">
                <div class="card-body text-center p-4">
                    <div class="text-primary mb-3">
                        <i class="fa-solid fa-id-card fa-3x"></i>
                    </div>
                    <h5 class="card-title fw-bold">Mi Perfil</h5>
                    <p class="card-text text-muted small">Actualiza tus datos personales, foto de perfil, carrera y enlaces de contacto profesional.</p>
                    <a href="${pageContext.request.contextPath}/graduate/profile" class="btn btn-outline-primary w-100 mt-2">
                        <i class="fa-solid fa-user-pen me-1"></i> Ir a Mi Perfil
                    </a>
                </div>
            </div>
        </div>

        <div class="col-md-6 col-lg-3">
            <div class="card h-100 shadow-sm border-0 border-top border-4 border-success">
                <div class="card-body text-center p-4">
                    <div class="text-success mb-3">
                        <i class="fa-solid fa-file-invoice fa-3x"></i>
                    </div>
                    <h5 class="card-title fw-bold">Mi Currículum</h5>
                    <p class="card-text text-muted small">Registra tu experiencia laboral, educación, proyectos destacados, idiomas y habilidades técnicas.</p>
                    <a href="${pageContext.request.contextPath}/graduate/cv" class="btn btn-outline-success w-100 mt-2">
                        <i class="fa-solid fa-file-lines me-1"></i> Gestionar CV
                    </a>
                </div>
            </div>
        </div>

        <div class="col-md-6 col-lg-3">
            <div class="card h-100 shadow-sm border-0 border-top border-4 border-warning">
                <div class="card-body text-center p-4">
                    <div class="text-warning mb-3">
                        <i class="fa-solid fa-briefcase fa-3x"></i>
                    </div>
                    <h5 class="card-title fw-bold">Convocatorias</h5>
                    <p class="card-text text-muted small">Encuentra ofertas laborales, postúlate con un clic y revisa el estado de aceptación de tus postulaciones.</p>
                    <a href="${pageContext.request.contextPath}/graduate/jobs" class="btn btn-outline-warning w-100 mt-2">
                        <i class="fa-solid fa-magnifying-glass me-1"></i> Ver Convocatorias
                    </a>
                </div>
            </div>
        </div>

        <div class="col-md-6 col-lg-3">
            <div class="card h-100 shadow-sm border-0 border-top border-4 border-info">
                <div class="card-body text-center p-4">
                    <div class="text-info mb-3">
                        <i class="fa-solid fa-envelope-open-text fa-3x"></i>
                    </div>
                    <h5 class="card-title fw-bold">Propuestas de Contacto</h5>
                    <p class="card-text text-muted small">Revisa las empresas interesadas en tu perfil y decide si deseas compartirles tu contacto directo.</p>
                    <a href="${pageContext.request.contextPath}/graduate/requests" class="btn btn-outline-info w-100 mt-2">
                        <i class="fa-solid fa-handshake me-1"></i> Ver Propuestas
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>