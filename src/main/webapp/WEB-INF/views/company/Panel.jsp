<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="../common/header.jsp" %>

<div class="container py-4">
    <div class="p-4 mb-4 bg-light rounded-3 border">
        <h1 class="display-6 fw-bold text-success">
            <i class="fa-solid fa-building me-2"></i>Panel de la Empresa
        </h1>
        <p class="lead mb-0 text-muted">
            Gestiona la presencia institucional de tu empresa, publica nuevas convocatorias de empleo, busca talentos egresados y consulta los contactos autorizados.
        </p>
    </div>

    <div class="row g-4">
        <div class="col-md-6 col-lg-3">
            <div class="card h-100 shadow-sm border-0 border-top border-4 border-primary">
                <div class="card-body text-center p-4">
                    <div class="text-primary mb-3">
                        <i class="fa-solid fa-building-user fa-3x"></i>
                    </div>
                    <h5 class="card-title fw-bold">Mi Empresa</h5>
                    <p class="card-text text-muted small">Configura la información institucional, logo corporativo, sector, descripción y datos de contacto.</p>
                    <a href="${pageContext.request.contextPath}/company/profile" class="btn btn-outline-primary w-100 mt-2">
                        <i class="fa-solid fa-pen-to-square me-1"></i> Perfil Corporativo
                    </a>
                </div>
            </div>
        </div>

        <div class="col-md-6 col-lg-3">
            <div class="card h-100 shadow-sm border-0 border-top border-4 border-success">
                <div class="card-body text-center p-4">
                    <div class="text-success mb-3">
                        <i class="fa-solid fa-bullhorn fa-3x"></i>
                    </div>
                    <h5 class="card-title fw-bold">Convocatorias</h5>
                    <p class="card-text text-muted small">Publica nuevas ofertas laborales y gestiona a los postulantes aceptándolos o evaluándolos.</p>
                    <a href="${pageContext.request.contextPath}/company/jobs" class="btn btn-outline-success w-100 mt-2">
                        <i class="fa-solid fa-plus-circle me-1"></i> Publicar / Gestionar
                    </a>
                </div>
            </div>
        </div>

        <div class="col-md-6 col-lg-3">
            <div class="card h-100 shadow-sm border-0 border-top border-4 border-warning">
                <div class="card-body text-center p-4">
                    <div class="text-warning mb-3">
                        <i class="fa-solid fa-users-viewfinder fa-3x"></i>
                    </div>
                    <h5 class="card-title fw-bold">Buscar Egresados</h5>
                    <p class="card-text text-muted small">Filtra candidatos por carrera o palabra clave, revisa sus currículums y solicita su contacto directo.</p>
                    <a href="${pageContext.request.contextPath}/company/search" class="btn btn-outline-warning w-100 mt-2">
                        <i class="fa-solid fa-magnifying-glass me-1"></i> Buscar Talentos
                    </a>
                </div>
            </div>
        </div>

        <div class="col-md-6 col-lg-3">
            <div class="card h-100 shadow-sm border-0 border-top border-4 border-info">
                <div class="card-body text-center p-4">
                    <div class="text-info mb-3">
                        <i class="fa-solid fa-address-book fa-3x"></i>
                    </div>
                    <h5 class="card-title fw-bold">Favoritos y Contactos</h5>
                    <p class="card-text text-muted small">Revisa los perfiles que guardaste como favoritos y consulta las solicitudes de contacto que te aprobaron.</p>
                    <a href="${pageContext.request.contextPath}/company/favorites" class="btn btn-outline-info w-100 mt-2">
                        <i class="fa-solid fa-star me-1"></i> Ver Contactos
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>