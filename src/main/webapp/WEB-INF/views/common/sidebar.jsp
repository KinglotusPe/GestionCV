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
<a href="${pageContext.request.contextPath}/notifications" class="d-flex align-items-center justify-content-between"><span class="d-flex align-items-center"><i class="fa-solid fa-bell"></i> Notificaciones</span><span class="badge bg-danger rounded-pill notif-badge ms-auto" style="display:none;">0</span></a>
<a href="${pageContext.request.contextPath}/account"><i class="fa-solid fa-user-gear"></i> Mi cuenta</a>
</nav></aside>
<header class="portal-topbar">
    <div class="d-flex align-items-center gap-2">
        <button type="button" id="sidebar-toggle" class="btn" aria-label="Alternar menú" aria-controls="portal-sidebar" aria-expanded="false"><i class="fa-solid fa-bars"></i></button>
        <span class="portal-topbar-title">Portal de empleabilidad</span>
    </div>
    <div class="d-flex align-items-center gap-3 ms-auto">
        <a href="${pageContext.request.contextPath}/notifications" class="btn btn-light position-relative rounded-circle p-2 text-dark shadow-sm" title="Centro de Notificaciones" id="headerNotifBell" style="width: 38px; height: 38px; display: flex; align-items: center; justify-content: center;">
            <i class="fa-solid fa-bell text-secondary"></i>
            <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger notif-badge" style="display:none; font-size: 0.68rem;">0</span>
        </a>
        <form method="post" action="${pageContext.request.contextPath}/logout" class="m-0">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <button class="btn btn-sm btn-outline-secondary"><i class="fa-solid fa-right-from-bracket me-1"></i> Cerrar sesión</button>
        </form>
    </div>
</header>
<script>
(function() {
    function updateNotifBadges() {
        fetch('${pageContext.request.contextPath}/api/notifications?action=unreadCount')
            .then(r => r.json())
            .then(data => {
                const count = (data && data.unreadCount) ? data.unreadCount : 0;
                document.querySelectorAll('.notif-badge').forEach(el => {
                    if (count > 0) {
                        el.textContent = count > 99 ? '99+' : count;
                        el.style.display = 'inline-block';
                    } else {
                        el.style.display = 'none';
                    }
                });
            })
            .catch(() => {});
    }
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', updateNotifBadges);
    } else {
        updateNotifBadges();
    }
    setInterval(updateNotifBadges, 30000);
})();
</script>
</c:if>
