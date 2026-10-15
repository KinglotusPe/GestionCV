<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="header.jsp" %>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-4">
        <div>
            <p class="eyebrow mb-1">COMUNICACIONES INTERNAS</p>
            <h1 class="h3 fw-bold mb-0">
                <i class="fa-solid fa-bell text-success me-2"></i>Centro de Notificaciones
            </h1>
        </div>
        <div class="d-flex gap-2">
            <c:if test="${unreadCount > 0}">
                <form method="post" action="${pageContext.request.contextPath}/notifications">
                    <hp:csrf/>
                    <input type="hidden" name="action" value="markAllRead">
                    <button type="submit" class="btn btn-outline-success">
                        <i class="fa-solid fa-check-double me-1"></i> Marcar todas como leídas
                    </button>
                </form>
            </c:if>
            <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-secondary">
                <i class="fa-solid fa-arrow-left me-1"></i> Volver al inicio
            </a>
        </div>
    </div>

    <c:if test="${empty notifications}">
        <div class="card p-5 text-center shadow-sm border-0">
            <div class="text-muted mb-3">
                <i class="fa-regular fa-bell-slash fa-3x"></i>
            </div>
            <h5 class="fw-bold text-dark">Bandeja al día</h5>
            <p class="text-muted mb-0">No tienes notificaciones registradas en este momento.</p>
        </div>
    </c:if>

    <div class="list-group shadow-sm">
        <c:forEach var="n" items="${notifications}">
            <div class="list-group-item list-group-item-action p-3 ${not n.read ? 'bg-light border-start border-4 border-success' : ''}">
                <div class="d-flex w-100 justify-content-between align-items-center mb-1">
                    <h5 class="mb-1 h6 fw-bold ${not n.read ? 'text-success' : 'text-dark'}">
                        <c:choose>
                            <c:when test="${n.type == 'SELECCION'}">
                                <i class="fa-solid fa-award text-success me-1"></i>
                            </c:when>
                            <c:when test="${n.type == 'ENTREVISTA'}">
                                <i class="fa-solid fa-calendar-check text-primary me-1"></i>
                            </c:when>
                            <c:when test="${n.type == 'PRESELECCION'}">
                                <i class="fa-solid fa-user-check text-info me-1"></i>
                            </c:when>
                            <c:when test="${n.type == 'POSTULACION_RECIBIDA'}">
                                <i class="fa-solid fa-file-arrow-up text-warning me-1"></i>
                            </c:when>
                            <c:when test="${n.type == 'RECHAZO'}">
                                <i class="fa-solid fa-circle-xmark text-secondary me-1"></i>
                            </c:when>
                            <c:when test="${n.type == 'CUENTA_APROBADA'}">
                                <i class="fa-solid fa-shield-halved text-success me-1"></i>
                            </c:when>
                            <c:otherwise>
                                <i class="fa-solid fa-bell text-secondary me-1"></i>
                            </c:otherwise>
                        </c:choose>
                        <c:out value="${n.title}"/>
                        <c:if test="${not n.read}">
                            <span class="badge bg-success ms-2" style="font-size: 0.7rem;">Nuevo</span>
                        </c:if>
                    </h5>
                    <small class="text-muted"><i class="fa-regular fa-clock me-1"></i><c:out value="${n.createdAt}"/></small>
                </div>
                <p class="mb-2 text-secondary"><c:out value="${n.message}"/></p>
                <div class="d-flex gap-2 align-items-center">
                    <c:if test="${not empty n.referenceUrl}">
                        <a href="${pageContext.request.contextPath}/notifications?read=${n.notificationId}&redirect=${n.referenceUrl}" class="btn btn-sm btn-success">
                            <i class="fa-solid fa-arrow-up-right-from-square me-1"></i> Ver detalles
                        </a>
                    </c:if>
                    <c:if test="${not n.read}">
                        <form method="post" action="${pageContext.request.contextPath}/notifications" class="d-inline">
                            <hp:csrf/>
                            <input type="hidden" name="action" value="markRead">
                            <input type="hidden" name="id" value="${n.notificationId}">
                            <button type="submit" class="btn btn-sm btn-outline-secondary">
                                <i class="fa-solid fa-check me-1"></i> Marcar como leída
                            </button>
                        </form>
                    </c:if>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<%@ include file="footer.jsp" %>
