<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jspf" %>

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
        <div class="col-md-2 d-flex align-items-end">
            <button class="btn btn-success w-100 fw-bold" type="submit">
                <i class="fa-solid fa-filter me-1"></i> Filtrar
            </button>
        </div>
    </form>
</div>

<!-- Listado de Candidatos Encontrados -->
<div class="row g-4 mb-4">
    <c:forEach var="item" items="${results}">
        <div class="col-md-6 col-lg-6">
            <div class="card p-4 h-100 shadow-sm border-top border-4 border-success">
                <div class="d-flex justify-content-between align-items-start mb-2">
                    <h5 class="fw-bold mb-0 text-success">
                        <c:out value="${item.first_name} ${item.last_name}"/>
                    </h5>
                    <span class="badge bg-light text-dark border">
                        <c:out value="${item.career}"/>
                    </span>
                </div>
                
                <h6 class="fw-semibold text-secondary mb-2">
                    <i class="fa-solid fa-tag me-1"></i> <c:out value="${item.title}"/>
                </h6>
                
                <p class="text-muted small mb-3">
                    <c:out value="${item.professional_summary}"/>
                </p>

                <div class="mt-auto pt-3 border-top d-flex justify-content-between align-items-center">
                    <span class="small text-muted">
                        <i class="fa-solid fa-location-dot me-1"></i> <c:out value="${item.city}"/>
                    </span>
                    <div class="d-flex gap-2">
                        <a class="btn btn-outline-success btn-sm fw-bold" href="${pageContext.request.contextPath}/company/cv?id=${item.cv_id}">
                            <i class="fa-solid fa-address-card me-1"></i> Ver CV
                        </a>
                        <form method="post" action="${pageContext.request.contextPath}/company/action" class="d-inline">
                            <input type="hidden" name="action" value="favorite">
                            <input type="hidden" name="cvId" value="${item.cv_id}">
                            <button class="btn btn-outline-warning btn-sm text-dark" type="submit" title="Guardar como favorito">
                                <i class="fa-solid fa-star"></i>
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </c:forEach>

    <c:if test="${empty results}">
        <div class="col-12">
            <div class="alert alert-info text-center py-4">
                <i class="fa-solid fa-circle-info fa-2x mb-2 d-block"></i>
                No se encontraron egresados con los criterios de búsqueda especificados.
            </div>
        </div>
    </c:if>
</div>

<!-- Paginación -->
<nav class="d-flex justify-content-center gap-2">
    <a class="btn btn-outline-secondary ${page <= 1 ? 'disabled' : ''}" 
       href="?page=${page - 1}&q=<c:out value='${param.q}'/>&career=<c:out value='${param.career}'/>&skill=<c:out value='${param.skill}'/>&city=<c:out value='${param.city}'/>">
        <i class="fa-solid fa-chevron-left me-1"></i> Anterior
    </a>
    <span class="btn btn-light disabled fw-bold">Página ${page}</span>
    <a class="btn btn-outline-secondary" 
       href="?page=${page + 1}&q=<c:out value='${param.q}'/>&career=<c:out value='${param.career}'/>&skill=<c:out value='${param.skill}'/>&city=<c:out value='${param.city}'/>">
        Siguiente <i class="fa-solid fa-chevron-right ms-1"></i>
    </a>
</nav>

<%@ include file="../common/footer.jspf" %>
