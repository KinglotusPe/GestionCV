<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="../common/header.jsp" %>
<div class="mb-4">
    <p class="eyebrow mb-1">OPORTUNIDADES DE EMPLEO</p>
    <h2 class="h3 fw-bold mb-1"><i class="fa-solid fa-envelope-open-text text-success me-2"></i> Propuestas de Contacto de Empresas</h2>
    <p class="text-muted small">Empresas interesadas en tu perfil técnico que han solicitado tus datos para coordinar entrevistas o contratación.</p>
</div>

<c:if test="${empty requests}">
    <div class="alert alert-info py-4 text-center">
        <i class="fa-solid fa-inbox fa-2x mb-2 text-muted d-block"></i>
        Todavía no has recibido propuestas de contacto directas. Asegúrate de tener tu CV público y actualizado en la plataforma.
    </div>
</c:if>

<c:forEach var="r" items="${requests}">
    <article class="card shadow-sm p-4 mb-3 border-start border-4 ${r.status == 'accepted' ? 'border-success' : r.status == 'rejected' ? 'border-danger' : 'border-warning'}">
        <div class="d-flex justify-content-between align-items-start mb-2">
            <div>
                <h3 class="h5 fw-bold text-success mb-1"><c:out value="${r.company_name}"/></h3>
                <p class="text-muted small mb-0">
                    <i class="fa-solid fa-envelope me-1"></i> <c:out value="${r.email}"/> · 
                    <i class="fa-solid fa-phone me-1"></i> <c:out value="${r.phone}"/> · 
                    <i class="fa-solid fa-calendar-day me-1"></i> Recibida el: <c:out value="${r.created_at}"/>
                </p>
            </div>
            <div>
                <c:choose>
                    <c:when test="${r.status == 'accepted'}">
                        <span class="badge bg-success fs-6"><i class="fa-solid fa-circle-check me-1"></i> Contacto Compartido</span>
                    </c:when>
                    <c:when test="${r.status == 'rejected'}">
                        <span class="badge bg-danger fs-6"><i class="fa-solid fa-ban me-1"></i> Rechazada</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge bg-warning text-dark fs-6"><i class="fa-solid fa-clock me-1"></i> Pendiente de tu respuesta</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="bg-light p-3 rounded my-3 border">
            <h6 class="fw-bold small text-secondary mb-1">Mensaje de la empresa:</h6>
            <p class="mb-0 text-dark preserve-lines" style="white-space:pre-wrap"><c:out value="${r.message}"/></p>
        </div>

        <c:choose>
            <c:when test="${r.status == 'accepted'}">
                <div class="alert alert-success d-flex align-items-center mb-3 py-2 small">
                    <i class="fa-solid fa-circle-check fa-lg text-success me-2"></i>
                    <div>
                        <strong>Contacto autorizado:</strong> La empresa puede ver tu teléfono y correo para comunicarse contigo.
                    </div>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/graduate/requests" class="confirm-action">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="id" value="${r.request_id}">
                    <button class="btn btn-outline-danger btn-sm" name="status" value="rejected">
                        <i class="fa-solid fa-ban me-1"></i> Revocar acceso y ocultar mi contacto
                    </button>
                </form>
            </c:when>
            <c:when test="${r.status == 'rejected'}">
                <div class="alert alert-secondary mb-3 py-2 small">
                    <i class="fa-solid fa-ban me-1"></i> Has rechazado esta solicitud. Tus datos de contacto permanecen privados.
                </div>
                <form method="post" action="${pageContext.request.contextPath}/graduate/requests">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="id" value="${r.request_id}">
                    <button class="btn btn-outline-success btn-sm" name="status" value="accepted">
                        <i class="fa-solid fa-check me-1"></i> Cambiar de opinión y compartir mi contacto
                    </button>
                </form>
            </c:when>
            <c:otherwise>
                <div class="alert alert-warning mb-3 py-2 small">
                    <i class="fa-solid fa-triangle-exclamation me-1"></i> Al aceptar, compartirás tu número de teléfono y correo con esta empresa para coordinar entrevistas.
                </div>
                <form method="post" action="${pageContext.request.contextPath}/graduate/requests" class="d-flex gap-2">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="id" value="${r.request_id}">
                    <button class="btn btn-success fw-bold" name="status" value="accepted">
                        <i class="fa-solid fa-check me-1"></i> Aceptar y compartir contacto
                    </button>
                    <button class="btn btn-outline-danger" name="status" value="rejected">
                        <i class="fa-solid fa-xmark me-1"></i> Rechazar propuesta
                    </button>
                </form>
            </c:otherwise>
        </c:choose>
    </article>
</c:forEach>
<%@ include file="../common/footer.jsp" %>
