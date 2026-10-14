<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="header.jspf" %>
<h2>${companyMode ? 'Mis vacantes y candidatos' : 'Vacantes y postulaciones'}</h2>
<c:set var="jobsPath" value="${pageContext.request.contextPath}/${companyMode ? 'company' : 'graduate'}/jobs"/>
<c:if test="${companyMode}">
<details class="card p-4 mb-4" ${not empty editing ? 'open' : ''}><summary class="h5">${empty editing ? 'Publicar una vacante' : 'Editar vacante'}</summary>
<form method="post" action="${jobsPath}" class="mt-3">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
<input type="hidden" name="action" value="save"><input type="hidden" name="id" value="${editing.job_id}">
<label class="form-label">Puesto</label><input class="form-control mb-3" name="title" maxlength="150" required value="<c:out value='${editing.title}'/>">
<label class="form-label">Funciones y requisitos</label><textarea class="form-control mb-3" name="description" rows="5" maxlength="10000" required><c:out value="${editing.description}"/></textarea>
<div class="row"><div class="col-md-6"><label class="form-label">Ciudad</label><input class="form-control mb-3" name="city" maxlength="80" required value="<c:out value='${editing.city}'/>"></div>
<div class="col-md-6"><label class="form-label">Remuneración (opcional)</label><input class="form-control mb-3" name="salary" maxlength="100" value="<c:out value='${editing.salary}'/>"></div></div>
<label class="form-label">Modalidad</label><select class="form-select mb-3" name="modality"><option value="onsite" ${editing.modality == 'onsite' ? 'selected' : ''}>Presencial</option><option value="remote" ${editing.modality == 'remote' ? 'selected' : ''}>Remoto</option><option value="hybrid" ${editing.modality == 'hybrid' ? 'selected' : ''}>Híbrido</option></select>
<label class="form-label">Programa de estudios</label><select class="form-select mb-3" name="careerId"><option value="">Cualquier carrera</option><c:forEach var="career" items="${careers}"><option value="${career.careerId}" ${editing.career_id == career.careerId ? 'selected' : ''}><c:out value="${career.name}"/></option></c:forEach></select>
<label class="form-label">Fecha de cierre (opcional)</label><input class="form-control mb-3" type="date" name="closingDate" value="${editing.closing_date}">
<label class="form-label">Estado</label><select class="form-select mb-3" name="status"><option value="open" ${editing.status == 'open' ? 'selected' : ''}>Abierta</option><option value="closed" ${editing.status == 'closed' ? 'selected' : ''}>Cerrada</option></select>
<button class="btn btn-success">Guardar vacante</button> <a class="btn btn-outline-secondary" href="${jobsPath}">Cancelar</a></form></details>
</c:if>
<form method="get" action="${jobsPath}" class="input-group mb-4"><input class="form-control" name="q" placeholder="Buscar puesto, empresa o requisito" value="<c:out value='${param.q}'/>"><button class="btn btn-success">Buscar</button></form>
<c:if test="${empty jobs}"><p class="alert alert-info">No hay vacantes para mostrar.</p></c:if>
<div class="row g-3 mb-4"><c:forEach var="j" items="${jobs}"><div class="col-lg-6"><article class="card h-100 p-4">
<h3 class="h5"><c:out value="${j.title}"/></h3><p><strong><c:out value="${j.company_name}"/></strong> · <c:out value="${j.city}"/></p>
<p><c:choose><c:when test="${j.modality == 'remote'}">Remoto</c:when><c:when test="${j.modality == 'hybrid'}">Híbrido</c:when><c:otherwise>Presencial</c:otherwise></c:choose> · <c:out value="${j.salary}"/></p>
<p style="white-space:pre-wrap"><c:out value="${j.description}"/></p>
<p class="small text-muted">Carrera: <c:out value="${j.career}" default="Cualquier carrera"/> · Cierre: <c:out value="${j.closing_date}" default="Sin fecha"/></p>
<c:choose><c:when test="${companyMode}"><p>Estado: ${j.status == 'open' ? 'Abierta' : 'Cerrada'}</p><a class="btn btn-outline-primary" href="${jobsPath}?edit=${j.job_id}">Editar / cerrar vacante</a></c:when>
<c:otherwise>
<c:set var="alreadyApplied" value="false"/><c:forEach var="a" items="${applications}"><c:if test="${a.job_id == j.job_id}"><c:set var="alreadyApplied" value="true"/></c:if></c:forEach>
<c:choose><c:when test="${alreadyApplied}"><p class="alert alert-light">Ya registró una postulación. Consulte su estado abajo.</p></c:when><c:otherwise>
<details><summary class="btn btn-success">Postular</summary><form method="post" action="${jobsPath}" class="mt-3">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
<input type="hidden" name="action" value="apply"><input type="hidden" name="id" value="${j.job_id}">
<label class="form-label">Presentación</label><textarea class="form-control mb-3" name="message" maxlength="5000" rows="3" required></textarea>
<label class="d-block mb-3"><input type="checkbox" name="consent" value="yes" required> Compartir mi CV, PDF y datos de contacto con esta empresa, incluso si mi perfil no es público.</label>
<button class="btn btn-success">Enviar postulación</button></form></details>
</c:otherwise></c:choose></c:otherwise></c:choose>
</article></div></c:forEach></div>
<h3>${companyMode ? 'Postulaciones recibidas' : 'Mis postulaciones'}</h3>
<c:if test="${empty applications}"><p class="text-muted">Todavía no hay postulaciones.</p></c:if>
<c:forEach var="a" items="${applications}"><article class="card p-4 mb-3"><h4 class="h5"><c:out value="${a.title}"/> · <c:out value="${a.company_name}"/></h4>
<c:if test="${companyMode}"><p><c:out value="${a.first_name} ${a.last_name}"/> · <c:out value="${a.email}"/> · <c:out value="${a.phone}"/></p><c:if test="${a.status != 'withdrawn' and not empty a.email}"><a class="mb-3" href="${pageContext.request.contextPath}/company/cv?id=${a.cv_id}">Ver CV del postulante</a></c:if></c:if>
<p style="white-space:pre-wrap"><c:out value="${a.message}"/></p>
<p>Estado: <strong><c:choose><c:when test="${a.status == 'pending'}">Pendiente</c:when><c:when test="${a.status == 'reviewing'}">En evaluación</c:when><c:when test="${a.status == 'accepted'}">Aceptada</c:when><c:when test="${a.status == 'rejected'}">Rechazada</c:when><c:otherwise>Retirada</c:otherwise></c:choose></strong></p>
<c:if test="${a.status != 'withdrawn'}"><form method="post" action="${jobsPath}" class="d-flex gap-2">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"><input type="hidden" name="action" value="respond"><input type="hidden" name="id" value="${a.application_id}">
<c:choose><c:when test="${companyMode}"><select class="form-select" name="status"><option value="pending" ${a.status == 'pending' ? 'selected' : ''}>Pendiente</option><option value="reviewing" ${a.status == 'reviewing' ? 'selected' : ''}>En evaluación</option><option value="accepted" ${a.status == 'accepted' ? 'selected' : ''}>Aceptada</option><option value="rejected" ${a.status == 'rejected' ? 'selected' : ''}>Rechazada</option></select><button class="btn btn-success">Actualizar</button></c:when><c:otherwise><button class="btn btn-outline-danger" name="status" value="withdrawn">Retirar postulación y acceso al CV</button></c:otherwise></c:choose>
</form></c:if></article></c:forEach>
<%@ include file="footer.jspf" %>
