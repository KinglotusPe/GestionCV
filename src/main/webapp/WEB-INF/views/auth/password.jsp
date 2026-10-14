<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="../common/header.jsp" %>
<div class="card p-4 mx-auto" style="max-width:600px"><h2>Cambiar contraseña</h2>
<form method="post" action="${pageContext.request.contextPath}/${reset ? 'reset-password' : 'account'}">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
<c:if test="${reset}"><input type="hidden" name="token" value="<c:out value='${param.token}'/>"></c:if>
<c:if test="${not reset}"><label class="form-label">Contraseña actual</label><input class="form-control mb-3" type="password" name="currentPassword" autocomplete="current-password" required></c:if>
<label class="form-label">Nueva contraseña (mínimo 8 caracteres)</label><input class="form-control mb-3" type="password" name="password" minlength="8" autocomplete="new-password" required>
<label class="form-label">Repetir contraseña</label><input class="form-control mb-3" type="password" name="confirmPassword" minlength="8" autocomplete="new-password" required>
<button class="btn btn-success">Guardar contraseña</button></form></div>
<%@ include file="../common/footer.jsp" %>
