<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jsp" %>

<div class="mb-4">
    <h2 class="section-title mb-1">
        <i class="fa-solid fa-star text-warning me-2"></i> Mis Candidatos Favoritos y Solicitudes
    </h2>
    <p class="text-muted small">Historial de perfiles guardados y seguimiento de propuestas de contacto enviadas</p>
</div>

<!-- Pestañas de Navegación -->
<ul class="nav nav-pills mb-4" id="favTabs" role="tablist">
    <li class="nav-item" role="presentation">
        <button class="nav-link active fw-bold" id="fav-tab" data-bs-toggle="pill" data-bs-target="#tab-fav" type="button" role="tab">
            <i class="fa-solid fa-star me-1"></i> Favoritos Guardados
        </button>
    </li>
    <li class="nav-item ms-2" role="presentation">
        <button class="nav-link fw-bold" id="req-tab" data-bs-toggle="pill" data-bs-target="#tab-req" type="button" role="tab">
            <i class="fa-solid fa-paper-plane me-1"></i> Solicitudes de Contacto Enviadas
        </button>
    </li>
</ul>

<div class="tab-content" id="favTabsContent">
    <!-- Tab 1: Favoritos -->
    <div class="tab-pane fade show active" id="tab-fav" role="tabpanel">
        <div class="row g-3">
            <c:forEach var="f" items="${favorites}">
                <div class="col-md-6 col-lg-4">
                    <div class="card p-3 h-100 shadow-sm border-start border-4 border-warning">
                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <h5 class="fw-bold mb-0 text-success">
                                <c:out value="${f.first_name} ${f.last_name}"/>
                            </h5>
                            <form method="post" action="${pageContext.request.contextPath}/company/favorites" class="d-inline confirm-action">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="action" value="removeFavorite">
                                <input type="hidden" name="favoriteId" value="${f.favorite_id}">
                                <button class="btn btn-sm btn-link text-danger p-0" type="submit" title="Quitar de favoritos">
                                    <i class="fa-solid fa-trash"></i>
                                </button>
                            </form>
                        </div>
                        <p class="small text-muted mb-1"><i class="fa-solid fa-graduation-cap me-1"></i> <c:out value="${f.career}"/></p>
                        <p class="small text-secondary mb-3"><i class="fa-solid fa-location-dot me-1"></i> <c:out value="${f.city}"/></p>
                        <div class="mt-auto pt-2 border-top">
                            <a class="btn btn-outline-success btn-sm w-100 fw-bold" href="${pageContext.request.contextPath}/company/cv?id=${f.cv_id}">
                                <i class="fa-solid fa-eye me-1"></i> Ver Perfil Completo
                            </a>
                        </div>
                    </div>
                </div>
            </c:forEach>

            <c:if test="${empty favorites}">
                <div class="col-12">
                    <div class="alert alert-light border text-center py-4">
                        <i class="fa-regular fa-star fa-2x mb-2 text-muted d-block"></i>
                        Aún no ha marcado candidatos como favoritos. Puede hacerlo desde el buscador de egresados.
                    </div>
                </div>
            </c:if>
        </div>
    </div>

    <!-- Tab 2: Solicitudes de Contacto -->
    <div class="tab-pane fade" id="tab-req" role="tabpanel">
        <div class="card shadow-sm overflow-hidden">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th>Egresado</th>
                            <th>Carrera</th>
                            <th>Contacto Directo</th>
                            <th>Mensaje Enviado</th>
                            <th>Estado</th>
                            <th>Fecha</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="r" items="${requests}">
                            <tr>
                                <td><strong><c:out value="${r.first_name} ${r.last_name}"/></strong></td>
                                <td><span class="badge bg-light text-dark border"><c:out value="${r.career}"/></span></td>
                                <td class="small">
                                    <c:if test="${empty r.graduate_email}">Disponible al aceptar la solicitud</c:if><i class="fa-solid fa-phone me-1"></i> <c:out value="${r.phone}"/><br>
                                    <i class="fa-solid fa-envelope me-1"></i> <c:out value="${r.graduate_email}"/>
                                </td>
                                <td class="small text-muted" style="max-width: 250px;">
                                    <c:out value="${r.message}"/>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${r.status == 'accepted'}">
                                            <span class="badge bg-success">Aceptada</span>
                                        </c:when>
                                        <c:when test="${r.status == 'rejected'}">
                                            <span class="badge bg-danger">Rechazada</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-warning text-dark">Pendiente</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="small text-muted">${r.created_at}</td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty requests}">
                            <tr>
                                <td colspan="6" class="text-center py-4 text-muted">
                                    No ha emitido ninguna propuesta o solicitud de contacto hasta el momento.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
