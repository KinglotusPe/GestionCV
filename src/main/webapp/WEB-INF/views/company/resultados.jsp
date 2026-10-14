<%@ page pageEncoding="UTF-8" %>
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
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
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

<!-- Paginación con parámetros codificados -->
<c:url var="previousUrl" value="/company/search"><c:param name="page" value="${page-1}"/><c:param name="q" value="${param.q}"/><c:param name="career" value="${param.career}"/><c:param name="skill" value="${param.skill}"/><c:param name="city" value="${param.city}"/><c:param name="year" value="${param.year}"/></c:url>
<c:url var="nextUrl" value="/company/search"><c:param name="page" value="${page+1}"/><c:param name="q" value="${param.q}"/><c:param name="career" value="${param.career}"/><c:param name="skill" value="${param.skill}"/><c:param name="city" value="${param.city}"/><c:param name="year" value="${param.year}"/></c:url>
<nav class="d-flex justify-content-center gap-2"><c:if test="${page > 1}"><a class="btn btn-outline-secondary" href="<c:out value='${previousUrl}'/>">Anterior</a></c:if><span class="btn btn-light">Página ${page}</span><c:if test="${hasNext}"><a class="btn btn-outline-secondary" href="<c:out value='${nextUrl}'/>">Siguiente</a></c:if></nav>
