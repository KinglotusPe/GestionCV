<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="../common/header.jsp" %>
<h2>Mis propuestas laborales</h2>
<p>Al aceptar, comparte su teléfono y correo con la empresa. Puede revocar el acceso rechazando la solicitud posteriormente.</p>
<c:if test="${empty requests}"><div class="alert alert-info">Todavía no tiene propuestas.</div></c:if>
<c:forEach var="r" items="${requests}">
<article class="card p-4 mb-3"><h3 class="h5"><c:out value="${r.company_name}"/></h3>
<p class="text-muted"><c:out value="${r.email}"/> · <c:out value="${r.phone}"/> · <c:out value="${r.created_at}"/></p>
<p style="white-space:pre-wrap"><c:out value="${r.message}"/></p>
<p>Estado: <strong><c:choose><c:when test="${r.status == 'accepted'}">Aceptada</c:when><c:when test="${r.status == 'rejected'}">Rechazada</c:when><c:otherwise>Pendiente</c:otherwise></c:choose></strong></p>
<form method="post" action="${pageContext.request.contextPath}/graduate/requests">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
<input type="hidden" name="id" value="${r.request_id}">
<button class="btn btn-success" name="status" value="accepted">Aceptar y compartir contacto</button>
<button class="btn btn-outline-danger" name="status" value="rejected">Rechazar / revocar acceso</button>
</form></article>
</c:forEach>
<%@ include file="../common/footer.jsp" %>
