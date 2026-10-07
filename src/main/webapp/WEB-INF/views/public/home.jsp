<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jspf" %>

<!-- Sección Hero Institucional -->
<div class="hero text-center text-lg-start p-4 p-md-5 mb-5 rounded-4 shadow-sm" 
     style="background: linear-gradient(135deg, rgba(11,61,46,0.94) 0%, rgba(22,101,52,0.92) 100%), 
            url('${pageContext.request.contextPath}/assets/images/entrance.jpg') center/cover no-repeat;">
    <div class="row align-items-center">
        <div class="col-lg-8">
            <div class="d-inline-flex align-items-center gap-2 bg-warning bg-opacity-25 border border-warning border-opacity-50 text-warning fw-bold px-3 py-1 rounded-pill mb-3">
                <img src="${pageContext.request.contextPath}/assets/images/logo_hierro_pozo.png" alt="Logo" style="width: 24px; height: 24px; object-fit: contain;">
                <span style="font-size: 0.85rem; letter-spacing: 0.5px;">INSTITUTO DE EXCELENCIA · IDEX MAHP</span>
            </div>
            <h1 class="display-5 fw-bold mb-3 text-white">Sistema de Gestión de Currículums Vitae</h1>
            <p class="lead mb-4 text-white-50" style="max-width: 680px;">
                Conectamos el talento de nuestros egresados técnicos profesionales del <strong>IESP Manuel Antonio Hierro Pozo</strong> con empresas líderes de Ayacucho y de todo el Perú.
            </p>
            <div class="d-flex flex-wrap gap-3">
                <a class="btn btn-warning btn-lg fw-bold px-4 text-dark shadow-sm" href="${pageContext.request.contextPath}/register">
                    <i class="fa-solid fa-user-plus me-1"></i> Registrarse Ahora
                </a>
                <a class="btn btn-outline-light btn-lg px-4 fw-semibold" href="${pageContext.request.contextPath}/login">
                    <i class="fa-solid fa-right-to-bracket me-1"></i> Iniciar Sesión
                </a>
            </div>
        </div>
        <div class="col-lg-4 text-center mt-4 mt-lg-0">
            <div class="p-4 bg-white bg-opacity-10 rounded-4 border border-white border-opacity-25 shadow backdrop-blur">
                <img src="${pageContext.request.contextPath}/assets/images/logo_hierro_pozo.png" 
                     alt="Sello Institucional" 
                     class="mb-3" 
                     style="width: 105px; height: 105px; object-fit: contain;">
                <h4 class="fw-bold text-white mb-1">Bolsa de Talentos</h4>
                <p class="small text-white-50 mb-0">Ayacucho – Perú · R.M. N° 068-2020-MINEDU</p>
            </div>
        </div>
    </div>
</div>

<!-- Programas de Estudios del Instituto (Fiel a la web oficial) -->
<div class="mb-5">
    <div class="mb-4">
        <h2 class="fw-bold mb-1 section-title-bar" style="color: #0f172a; font-size: 1.85rem;">
            Programas de Estudios
        </h2>
        <p class="text-muted small mt-2 mb-0">
            Formación técnica y profesional con equipamiento moderno y alta demanda laboral en la región
        </p>
    </div>
    
    <div class="row g-4">
        <!-- 1. Gestión Administrativa -->
        <div class="col-sm-6 col-lg-3">
            <div class="career-card-official career-border-gestion">
                <div class="card-img-wrapper">
                    <img src="${pageContext.request.contextPath}/assets/images/carrera_gestion.jpg" 
                         alt="Gestión Administrativa - Hierro Pozo">
                </div>
                <div class="card-body">
                    <h5>Gestión Administrativa</h5>
                </div>
            </div>
        </div>

        <!-- 2. Administración de Redes y Comunicaciones -->
        <div class="col-sm-6 col-lg-3">
            <div class="career-card-official career-border-redes">
                <div class="card-img-wrapper">
                    <img src="${pageContext.request.contextPath}/assets/images/carrera_redes.jpg" 
                         alt="Administración de Redes y Comunicaciones - Hierro Pozo">
                </div>
                <div class="card-body">
                    <h5>Administración de Redes y Comunicaciones</h5>
                </div>
            </div>
        </div>

        <!-- 3. Electricidad Industrial -->
        <div class="col-sm-6 col-lg-3">
            <div class="career-card-official career-border-electricidad">
                <div class="card-img-wrapper">
                    <img src="${pageContext.request.contextPath}/assets/images/carrera_electricidad.jpg" 
                         alt="Electricidad Industrial - Hierro Pozo">
                </div>
                <div class="card-body">
                    <h5>Electricidad Industrial</h5>
                </div>
            </div>
        </div>

        <!-- 4. Mecatrónica Automotriz -->
        <div class="col-sm-6 col-lg-3">
            <div class="career-card-official career-border-mecatronica">
                <div class="card-img-wrapper">
                    <img src="${pageContext.request.contextPath}/assets/images/carrera_mecatronica.jpg" 
                         alt="Mecatrónica Automotriz - Hierro Pozo">
                </div>
                <div class="card-body">
                    <h5>Mecatrónica Automotriz</h5>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Sección: Por qué ser parte del IDEX MAHP (Fiel a la web oficial) -->
<div class="my-5 pt-3">
    <div class="text-center mb-4">
        <h2 class="fw-bold" style="color: #0f172a; font-size: 1.85rem;">
            Por qué ser parte del <span style="color: #0066cc;">IDEX MAHP</span>
        </h2>
        <p class="text-muted small">
            Compromiso con la formación tecnológica de excelencia, la innovación y el desarrollo regional
        </p>
    </div>

    <div class="row g-4">
        <div class="col-md-6 col-lg-3">
            <div class="card h-100 p-4 border-0 shadow-sm text-center">
                <div class="mx-auto mb-3 text-primary bg-primary bg-opacity-10 rounded-circle d-grid place-items-center" style="width: 60px; height: 60px;">
                    <i class="fa-solid fa-award fa-2x"></i>
                </div>
                <h6 class="fw-bold text-dark mb-2">Licenciamiento Institucional</h6>
                <p class="text-muted small mb-0">
                    Instituto de Excelencia con condiciones básicas de calidad garantizadas por el MINEDU.
                </p>
            </div>
        </div>

        <div class="col-md-6 col-lg-3">
            <div class="card h-100 p-4 border-0 shadow-sm text-center">
                <div class="mx-auto mb-3 text-success bg-success bg-opacity-10 rounded-circle d-grid place-items-center" style="width: 60px; height: 60px;">
                    <i class="fa-solid fa-microchip fa-2x"></i>
                </div>
                <h6 class="fw-bold text-dark mb-2">Talleres Equipados</h6>
                <p class="text-muted small mb-0">
                    Laboratorios de cómputo, redes, subestaciones eléctricas y talleres automotrices modernos.
                </p>
            </div>
        </div>

        <div class="col-md-6 col-lg-3">
            <div class="card h-100 p-4 border-0 shadow-sm text-center">
                <div class="mx-auto mb-3 text-warning bg-warning bg-opacity-10 rounded-circle d-grid place-items-center" style="width: 60px; height: 60px;">
                    <i class="fa-solid fa-handshake fa-2x"></i>
                </div>
                <h6 class="fw-bold text-dark mb-2">Convenios y Prácticas</h6>
                <p class="text-muted small mb-0">
                    Alianzas estratégicas con empresas e instituciones públicas para prácticas pre-profesionales.
                </p>
            </div>
        </div>

        <div class="col-md-6 col-lg-3">
            <div class="card h-100 p-4 border-0 shadow-sm text-center">
                <div class="mx-auto mb-3 text-danger bg-danger bg-opacity-10 rounded-circle d-grid place-items-center" style="width: 60px; height: 60px;">
                    <i class="fa-solid fa-id-card-clip fa-2x"></i>
                </div>
                <h6 class="fw-bold text-dark mb-2">CV Digital y Empleabilidad</h6>
                <p class="text-muted small mb-0">
                    Plataforma online para que empresas verifiquen y contacten a nuestros egresados calificados.
                </p>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jspf" %>
