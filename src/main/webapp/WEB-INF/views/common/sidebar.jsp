<%@ page pageEncoding="UTF-8" %>
<c:if test="${not empty sessionScope.userId}">
<aside class="portal-sidebar" id="portal-sidebar">
<a class="portal-brand" href="${pageContext.request.contextPath}/home"><img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="Escudo institucional"><span>MANUEL ANTONIO<br><strong>HIERRO POZO</strong><small>BOLSA LABORAL · CVMANAGER</small></span></a>
<div class="portal-welcome">Bienvenido/a<strong>${sessionScope.role == 'admin' ? 'Administrador' : sessionScope.role == 'company' ? 'Empresa' : 'Egresado'}</strong></div>
<p class="portal-menu-label">MENÚ DE NAVEGACIÓN</p>
<nav aria-label="Menú principal">
<a href="${pageContext.request.contextPath}/home"><i class="fa-solid fa-house"></i> Inicio</a>
<c:if test="${sessionScope.role == 'graduate'}">
<a href="${pageContext.request.contextPath}/graduate/dashboard"><i class="fa-solid fa-chart-line"></i> Mi panel</a>
<a href="${pageContext.request.contextPath}/graduate/profile"><i class="fa-solid fa-id-card"></i> Mi perfil</a>
<a href="${pageContext.request.contextPath}/graduate/cv"><i class="fa-solid fa-file-lines"></i> Mi currículum</a>
<a href="${pageContext.request.contextPath}/graduate/jobs"><i class="fa-solid fa-briefcase"></i> Convocatorias</a>
<a href="${pageContext.request.contextPath}/graduate/jobs?tab=applications"><i class="fa-solid fa-list-check"></i> Mis postulaciones</a>
<a href="${pageContext.request.contextPath}/graduate/requests"><i class="fa-solid fa-envelope"></i> Propuestas de contacto</a>
</c:if>
<c:if test="${sessionScope.role == 'company'}">
<a href="${pageContext.request.contextPath}/company/dashboard"><i class="fa-solid fa-chart-line"></i> Mi panel</a>
<a href="${pageContext.request.contextPath}/company/profile"><i class="fa-solid fa-building"></i> Mi empresa</a>
<a href="${pageContext.request.contextPath}/company/jobs"><i class="fa-solid fa-briefcase"></i> Mis convocatorias</a>
<a href="${pageContext.request.contextPath}/company/jobs?tab=applications"><i class="fa-solid fa-users"></i> Postulaciones recibidas</a>
<a href="${pageContext.request.contextPath}/company/search"><i class="fa-solid fa-search"></i> Buscar egresados</a>
<a href="${pageContext.request.contextPath}/company/favorites"><i class="fa-solid fa-star"></i> Favoritos y contactos</a>
</c:if>
<c:if test="${sessionScope.role == 'admin'}">
<a href="${pageContext.request.contextPath}/admin/dashboard"><i class="fa-solid fa-chart-pie"></i> Indicadores</a>
<a href="${pageContext.request.contextPath}/admin/companies"><i class="fa-solid fa-building"></i> Empresas y solicitudes</a>
<a href="${pageContext.request.contextPath}/admin/jobs"><i class="fa-solid fa-briefcase"></i> Convocatorias</a>
<a href="${pageContext.request.contextPath}/admin/jobs?archive=yes"><i class="fa-solid fa-box-archive"></i> Convocatorias archivadas</a>
<a href="${pageContext.request.contextPath}/admin/reports"><i class="fa-solid fa-chart-column"></i> Reportes laborales</a>
<a href="${pageContext.request.contextPath}/admin/users"><i class="fa-solid fa-users"></i> Usuarios</a>
<a href="${pageContext.request.contextPath}/admin/careers"><i class="fa-solid fa-graduation-cap"></i> Programas de estudios</a>
<a href="${pageContext.request.contextPath}/admin/audit"><i class="fa-solid fa-clock-rotate-left"></i> Auditoría</a>
</c:if>
<a href="${pageContext.request.contextPath}/account"><i class="fa-solid fa-user-gear"></i> Mi cuenta</a>
</nav></aside>
<header class="portal-topbar"><button type="button" id="sidebar-toggle" class="btn" aria-label="Alternar menú" aria-controls="portal-sidebar" aria-expanded="false"><i class="fa-solid fa-bars"></i></button><span class="portal-topbar-title">Portal de empleabilidad</span><form method="post" action="${pageContext.request.contextPath}/logout"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"><button class="btn btn-sm btn-outline-secondary">Cerrar sesión</button></form></header>
</c:if>
