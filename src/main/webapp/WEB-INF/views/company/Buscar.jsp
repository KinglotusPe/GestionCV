<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jsp" %>

<div class="mb-4">
    <h2 class="section-title mb-1">
        <i class="fa-solid fa-magnifying-glass me-2"></i> Búsqueda de Talentos y Egresados
    </h2>
    <p class="text-muted small">Localice perfiles técnicos calificados del IESP "Manuel Antonio Hierro Pozo"</p>
</div>

<!-- Filtros de Búsqueda SQL -->
<div class="card p-4 shadow-sm mb-4">
    <form class="row g-3" method="get" action="${pageContext.request.contextPath}/company/search">
        <div class="col-md-3">
            <label class="form-label small fw-bold">Palabra clave</label>
            <input class="form-control" type="text" name="q" value="<c:out value='${param.q}'/>" placeholder="Nombre, cargo, perfil...">
        </div>
        <div class="col-md-3">
            <label class="form-label small fw-bold">Programa de Estudios</label>
            <select class="form-select" name="career">
                <option value="">Todas las carreras</option>
                <c:forEach var="c" items="${careers}">
                    <option value="${c.careerId}" ${param.career == c.careerId ? 'selected' : ''}>
                        <c:out value="${c.name}"/>
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-2">
            <label class="form-label small fw-bold">Habilidad</label>
            <input class="form-control" type="text" name="skill" value="<c:out value='${param.skill}'/>" placeholder="Ej: Redes, PLC...">
        </div>
        <div class="col-md-2">
            <label class="form-label small fw-bold">Ciudad</label>
            <input class="form-control" type="text" name="city" value="<c:out value='${param.city}'/>" placeholder="Ej: Ayacucho">
        </div>
        <div class="col-md-2"><label class="form-label">Año de egreso</label><input class="form-control" type="number" name="year" min="1950" max="2100" value="<c:out value='${param.year}'/>"></div><div class="col-md-2 d-flex align-items-end">
            <button class="btn btn-success w-100 fw-bold" type="submit">
                <i class="fa-solid fa-filter me-1"></i> Filtrar
            </button>
        </div>
    </form>
</div>

<%@ include file="resultados.jsp" %>
<%@ include file="../common/footer.jsp" %>
