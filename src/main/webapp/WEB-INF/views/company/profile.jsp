<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="../common/header.jspf" %>
<div class="card p-4"><h2>Mi empresa</h2><form method="post" action="${pageContext.request.contextPath}/company/profile">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
<label class="form-label">Razón social</label><input class="form-control mb-3" name="companyName" maxlength="150" required value="<c:out value='${company.company_name}'/>">
<label class="form-label">RUC (opcional)</label><input class="form-control mb-3" name="ruc" maxlength="11" value="<c:out value='${company.ruc}'/>">
<label class="form-label">Sector</label><input class="form-control mb-3" name="industry" maxlength="100" value="<c:out value='${company.industry}'/>">
<label class="form-label">Tamaño de empresa</label><input class="form-control mb-3" name="companySize" maxlength="50" value="<c:out value='${company.company_size}'/>">
<label class="form-label">Teléfono</label><input class="form-control mb-3" name="phone" type="tel" value="<c:out value='${company.phone}'/>">
<label class="form-label">Sitio web</label><input class="form-control mb-3" name="website" type="url" maxlength="255" value="<c:out value='${company.website}'/>">
<label class="form-label">Descripción</label><textarea class="form-control mb-3" name="description" rows="4" maxlength="5000"><c:out value="${company.description}"/></textarea>
<button class="btn btn-success">Guardar perfil</button></form></div>
<%@ include file="../common/footer.jspf" %>
