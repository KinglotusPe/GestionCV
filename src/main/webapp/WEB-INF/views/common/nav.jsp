<%@ page pageEncoding="UTF-8" %>
<!-- Navegación pública -->
<nav class="navbar navbar-expand-lg navbar-dark navbar-hp sticky-top ${not empty sessionScope.userId ? 'd-none' : ''}">
    <div class="container">
        <a class="navbar-brand d-flex align-items-center gap-2" href="${pageContext.request.contextPath}/home">
            <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="Logo IESP MAHP" style="width: 44px; height: 44px; object-fit: contain;">
            <span>
                <strong class="d-block" style="font-size: 1.15rem; line-height: 1.1;">CVManager</strong>
                <small class="d-block text-warning fw-semibold" style="font-size: 0.73rem; letter-spacing: 0.3px;">IESP Manuel Antonio Hierro Pozo</small>
            </span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#nav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div id="nav" class="collapse navbar-collapse">
            <ul class="navbar-nav ms-auto align-items-lg-center"><li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/about">Acerca de</a></li><li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/contact">Contacto</a></li>
                <c:choose>
                    <c:when test="${empty sessionScope.userId}">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/home">
                                <i class="fa-solid fa-house me-1"></i> Inicio
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/login">
                                <i class="fa-solid fa-right-to-bracket me-1"></i> Ingresar
                            </a>
                        </li>
                        <li class="nav-item ms-lg-2">
                            <a class="btn btn-warning text-dark fw-bold px-3" href="${pageContext.request.contextPath}/register">
                                <i class="fa-solid fa-user-plus me-1"></i> Registrarse
                            </a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <c:if test="${sessionScope.role == 'graduate'}"><li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/graduate/jobs">Vacantes</a></li><li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/graduate/requests">Propuestas</a></li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/graduate/profile">
                                    <i class="fa-solid fa-id-card me-1"></i> Mi Perfil
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/graduate/cv">
                                    <i class="fa-solid fa-file-lines me-1"></i> Mi CV
                                </a>
                            </li>
                        </c:if>
                        <c:if test="${sessionScope.role == 'company'}"><li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/company/jobs">Vacantes</a></li><li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/company/profile">Mi empresa</a></li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/company/search">
                                    <i class="fa-solid fa-magnifying-glass me-1"></i> Buscar Egresados
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/company/favorites">
                                    <i class="fa-solid fa-star me-1"></i> Favoritos y Contactos
                                </a>
                            </li>
                        </c:if>
                        <c:if test="${sessionScope.role == 'admin'}">
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">
                                    <i class="fa-solid fa-chart-pie me-1"></i> Dashboard
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/admin/users">
                                    <i class="fa-solid fa-users me-1"></i> Usuarios
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/admin/careers">
                                    <i class="fa-solid fa-graduation-cap me-1"></i> Carreras
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/admin/audit">
                                    <i class="fa-solid fa-clock-rotate-left me-1"></i> Auditoría
                                </a>
                            </li>
                        </c:if>
                        <li class="nav-item ms-lg-3">
                            <a class="nav-link" href="${pageContext.request.contextPath}/account">Mi cuenta</a>
                            <form method="post" action="${pageContext.request.contextPath}/logout">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"><button class="btn btn-outline-light btn-sm" type="submit">Cerrar sesión</button></form>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>
