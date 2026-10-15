<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="header.jsp" %>
<div class="page-heading d-flex justify-content-between align-items-center flex-wrap gap-2 mb-4">
    <div>
        <p class="eyebrow mb-1">BOLSA LABORAL</p>
        <h1 class="h3 mb-0">${param.tab == 'applications' ? (companyMode ? 'Postulaciones recibidas' : 'Mis postulaciones') : param.archive == 'yes' ? 'Convocatorias archivadas' : 'Convocatorias laborales'}</h1>
    </div>
    <div class="d-flex gap-2">
        <c:if test="${manageMode}">
            <button class="btn btn-success fw-bold" type="button" data-bs-toggle="collapse" data-bs-target="#jobFormCard" aria-expanded="${not empty editing or param['new'] == '1' ? 'true' : 'false'}">
                <i class="fa-solid fa-plus-circle me-1"></i> ${empty editing ? 'Publicar nueva convocatoria' : 'Editar convocatoria'}
            </button>
            <a class="btn btn-outline-secondary" href="${jobsPath}${param.archive == 'yes' ? '' : '?archive=yes'}">
                <i class="fa-solid fa-box-archive me-1"></i> ${param.archive == 'yes' ? 'Ver convocatorias' : 'Ver archivadas'}
            </a>
        </c:if>
    </div>
</div>
<c:if test="${param.tab != 'applications'}">
<c:if test="${manageMode}">
    <div class="collapse ${not empty editing or param['new'] == '1' or (empty jobs and empty param.stage and empty param.q) ? 'show' : ''} mb-4" id="jobFormCard">
        <div class="card shadow-sm border-2 border-success p-4">
            <div class="d-flex justify-content-between align-items-center border-bottom pb-2 mb-3">
                <h4 class="h5 mb-0 text-success fw-bold">
                    <i class="fa-solid fa-briefcase me-2"></i> ${empty editing ? 'Nueva convocatoria laboral' : 'Editar convocatoria'}
                </h4>
                <button type="button" class="btn-close" data-bs-toggle="collapse" data-bs-target="#jobFormCard" aria-label="Cerrar"></button>
            </div>
            <p class="small text-muted mb-3">Complete los datos de la vacante para recibir postulaciones de nuestros egresados calificados.</p>
<form method="post" action="${jobsPath}" class="row g-3 mt-2">
<hp:csrf/><input type="hidden" name="action" value="save"><input type="hidden" name="id" value="${editing.job_id}">
<c:if test="${adminMode and empty editing}"><div class="col-12"><label class="form-label">Empresa<select class="form-select" name="companyId" required><option value="">Seleccione una empresa activa</option><c:forEach var="e" items="${companies}"><c:if test="${e.status == 'active'}"><option value="${e.company_id}"><c:out value="${e.company_name}"/></option></c:if></c:forEach></select></label><p class="small text-muted">Esta convocatoria será administrada por usted en representación del instituto.</p></div></c:if>
<div class="col-12"><label class="form-label w-100">Título de la convocatoria<input class="form-control" id="jobTitleInput" name="title" maxlength="150" required value="<c:out value='${editing.title}'/>"></label></div>
<div class="col-md-6">
    <div class="d-flex justify-content-between align-items-center mb-1">
        <label class="form-label mb-0 fw-bold">Funciones / descripción</label>
        <button type="button" class="btn btn-sm btn-outline-success py-0 px-2 small" onclick="askAI('job_description', 'jobDescriptionInput', 'jobTitleInput')"><i class="fa-solid fa-wand-magic-sparkles me-1"></i> Generar con IA</button>
    </div>
    <textarea class="form-control" id="jobDescriptionInput" name="description" rows="4" maxlength="10000" required><c:out value="${editing.description}"/></textarea>
</div>
<div class="col-md-6">
    <div class="d-flex justify-content-between align-items-center mb-1">
        <label class="form-label mb-0 fw-bold">Requisitos</label>
        <button type="button" class="btn btn-sm btn-outline-success py-0 px-2 small" onclick="askAI('job_requirements', 'jobRequirementsInput', 'jobTitleInput')"><i class="fa-solid fa-wand-magic-sparkles me-1"></i> Mejorar requisitos con IA</button>
    </div>
    <textarea class="form-control" id="jobRequirementsInput" name="requirements" rows="4" maxlength="10000"><c:out value="${editing.requirements}"/></textarea>
</div>
<div class="col-md-6">
    <div class="d-flex justify-content-between align-items-center mb-1">
        <label class="form-label mb-0 fw-bold">Beneficios</label>
        <button type="button" class="btn btn-sm btn-outline-secondary py-0 px-2 small" onclick="askAI('improve_text', 'jobBenefitsInput')"><i class="fa-solid fa-wand-magic-sparkles me-1"></i> Mejorar redacción con IA</button>
    </div>
    <textarea class="form-control" id="jobBenefitsInput" name="benefits" rows="3" maxlength="10000"><c:out value="${editing.benefits}"/></textarea>
</div>
<div class="col-md-6"><label class="form-label w-100 fw-bold">Condiciones<textarea class="form-control" name="conditions" rows="3" maxlength="10000"><c:out value="${editing.conditions_text}"/></textarea></label></div>
<div class="col-md-4"><label class="form-label w-100">Lugar de trabajo<input class="form-control" name="city" maxlength="80" required value="<c:out value='${editing.city}'/>"></label></div>
<div class="col-md-4"><label class="form-label w-100">Remuneración<input class="form-control" name="salary" maxlength="100" value="<c:out value='${editing.salary}'/>"></label></div>
<div class="col-md-4"><label class="form-label w-100">Número de vacantes<input class="form-control" type="number" min="1" max="10000" name="positions" required value="${empty editing.positions ? 1 : editing.positions}"></label></div>
<div class="col-md-4"><label class="form-label w-100">Modalidad<select class="form-select" name="modality"><option value="onsite" ${editing.modality == 'onsite' ? 'selected' : ''}>Presencial</option><option value="remote" ${editing.modality == 'remote' ? 'selected' : ''}>Remoto</option><option value="hybrid" ${editing.modality == 'hybrid' ? 'selected' : ''}>Híbrido</option></select></label></div>
<div class="col-md-4"><label class="form-label w-100">Turno<input class="form-control" name="shift" maxlength="40" required value="<c:out value='${empty editing.shift_name ? "Completo" : editing.shift_name}'/>"></label></div>
<div class="col-md-4"><label class="form-label w-100 fw-bold">Estado de la convocatoria<select class="form-select" name="status"><option value="PUBLICADA" ${editing.status == 'PUBLICADA' or editing.status == 'open' or empty editing.status ? 'selected' : ''}>PUBLICADA (Visible para postulantes)</option><option value="BORRADOR" ${editing.status == 'BORRADOR' ? 'selected' : ''}>BORRADOR (Guardar sin publicar)</option><option value="PAUSADA" ${editing.status == 'PAUSADA' ? 'selected' : ''}>PAUSADA (Temporalmente detenida)</option><option value="CERRADA" ${editing.status == 'CERRADA' or editing.status == 'closed' ? 'selected' : ''}>CERRADA (Proceso finalizado)</option></select></label></div>
<div class="col-md-6"><label class="form-label w-100">Fecha de inicio (opcional)<input class="form-control" type="date" name="openingDate" value="${editing.opening_date}"></label></div>
<div class="col-md-6"><label class="form-label w-100">Fecha de cierre (opcional)<input class="form-control" type="date" name="closingDate" value="${editing.closing_date}"></label></div>
<div class="col-12"><fieldset><legend class="h6">Programas de estudios destinatarios</legend><p class="small text-muted">Sin seleccionar: cualquier carrera. Puede elegir varios programas.</p><div class="row"><c:forEach var="career" items="${careers}"><c:if test="${career.active or (not empty editing and editing.careerIds.contains(career.careerId))}"><div class="col-md-6"><label class="mb-2"><input type="checkbox" name="careerId" value="${career.careerId}" ${not empty editing and editing.careerIds.contains(career.careerId) ? 'checked' : ''}> <c:out value="${career.name}"/></label></div></c:if></c:forEach></div></fieldset></div>
<div class="col-12"><label class="form-label w-100">Enlace informativo externo (opcional)<input type="url" class="form-control" name="externalUrl" maxlength="255" value="<c:out value='${editing.external_url}'/>"></label></div>
<div class="col-12 mt-4 pt-2 border-top d-flex gap-2"><button class="btn btn-success fw-bold px-4"><i class="fa-solid fa-floppy-disk me-1"></i> Guardar convocatoria</button> <a class="btn btn-outline-secondary" href="${jobsPath}">Cancelar</a></div>
</form></div></div></c:if>
<c:if test="${not empty selected and empty editing}">
<section class="portal-panel mb-4"><a href="${jobsPath}" class="small">← Volver al listado</a><h2 class="h4 mt-3"><c:out value="${selected.title}"/></h2><p class="text-muted"><c:out value="${selected.company_name}"/> · <c:out value="${selected.city}"/></p>
<div class="row"><div class="col-lg-4 job-facts"><p><strong>Vacantes:</strong> <c:out value="${selected.positions}" default="1"/></p><p><strong>Turno:</strong> <c:out value="${selected.shift_name}" default="Completo"/></p><p><strong>Remuneración:</strong> <c:out value="${selected.salary}" default="A convenir"/></p><p><strong>Inicio:</strong> <c:out value="${selected.opening_date}" default="Inmediato"/></p><p><strong>Cierre:</strong> <c:out value="${selected.closing_date}" default="Sin fecha"/></p><p><strong>Programas:</strong> <c:out value="${selected.career}" default="Cualquier carrera"/></p><c:if test="${not empty selected.external_url}"><a href="<c:out value='${selected.external_url}'/>" target="_blank" rel="noopener noreferrer">Enlace informativo de la convocatoria ↗</a></c:if></div><div class="col-lg-8"><h3 class="h6">Funciones</h3><p class="preserve-lines"><c:out value="${selected.description}"/></p><h3 class="h6">Requisitos</h3><p class="preserve-lines"><c:out value="${selected.requirements}" default="No especificados"/></p><h3 class="h6">Beneficios</h3><p class="preserve-lines"><c:out value="${selected.benefits}" default="No especificados"/></p><h3 class="h6">Condiciones</h3><p class="preserve-lines"><c:out value="${selected.conditions_text}" default="No especificadas"/></p></div></div>
<hr><h3 class="h6">Documentos de la convocatoria</h3><c:if test="${empty documents}"><p class="text-muted">No hay documentos adjuntos.</p></c:if><c:forEach var="doc" items="${documents}"><div class="d-flex gap-3 align-items-center mb-2"><a href="${jobsPath}?document=${doc.document_id}"><i class="fa-regular fa-file-pdf"></i> <c:out value="${doc.title}"/></a><c:if test="${editable}"><form method="post" action="${jobsPath}" class="confirm-action"><hp:csrf/><input type="hidden" name="action" value="removeDocument"><input type="hidden" name="id" value="${selected.job_id}"><input type="hidden" name="documentId" value="${doc.document_id}"><button class="btn btn-sm btn-outline-danger">Eliminar</button></form></c:if></div></c:forEach>
<c:if test="${editable}"><form method="post" enctype="multipart/form-data" action="${jobsPath}?csrfToken=${sessionScope.csrfToken}" class="row g-2 mt-3"><hp:csrf/><input type="hidden" name="action" value="upload"><input type="hidden" name="id" value="${selected.job_id}"><div class="col-md-5"><label class="form-label w-100">Título del documento<input name="title" class="form-control" required maxlength="150"></label></div><div class="col-md-5"><label class="form-label w-100">PDF (máximo 5 MB)<input type="file" class="form-control" name="file" accept="application/pdf" required></label></div><div class="col-md-2 align-self-end pb-2"><button class="btn btn-success">Adjuntar</button></div></form></c:if>
</section></c:if>
<section class="portal-panel"><form method="get" action="${jobsPath}" class="row g-3 mb-4"><input type="hidden" name="archive" value="<c:out value='${param.archive}'/>"><div class="col-md-4"><label class="form-label" for="stage">Filtrar por estado</label><select class="form-select" id="stage" name="stage"><option value="">Todos</option><option value="PUBLICADA" ${param.stage == 'PUBLICADA' ? 'selected' : ''}>Publicada</option><option value="BORRADOR" ${param.stage == 'BORRADOR' ? 'selected' : ''}>Borrador</option><option value="PAUSADA" ${param.stage == 'PAUSADA' ? 'selected' : ''}>Pausada</option><option value="CERRADA" ${param.stage == 'CERRADA' ? 'selected' : ''}>Cerrada</option><option value="VENCIDA" ${param.stage == 'VENCIDA' ? 'selected' : ''}>Vencida</option></select></div><div class="col-md-6"><label class="form-label" for="job-query">Buscar</label><input id="job-query" class="form-control" name="q" placeholder="Puesto, empresa o descripción" value="<c:out value='${param.q}'/>"></div><div class="col-md-2 align-self-end"><button class="btn btn-success">Filtrar</button></div></form>
<div class="table-responsive"><table class="table table-bordered table-striped portal-table" data-local-table><thead><tr><th>N.º</th><th>Empresa</th><th>Título de la convocatoria</th><th>Lugar / modalidad</th><th>Turno / salario</th><th>Fecha fin</th><th>Estado</th><th>Acciones</th></tr></thead><tbody><c:forEach var="j" items="${jobs}"><tr><td>${j.job_id}</td><td><c:out value="${j.company_name}"/><c:if test="${manageMode}"><small class="d-block text-muted">${empty j.managed_by ? 'Administra: empresa' : 'Administra: instituto'}</small></c:if></td><td><strong><c:out value="${j.title}"/></strong><small class="d-block text-muted"><c:out value="${j.career}" default="Cualquier carrera"/></small></td><td><c:out value="${j.city}"/><small class="d-block">${j.modality == 'remote' ? 'Remoto' : j.modality == 'hybrid' ? 'Híbrido' : 'Presencial'}</small></td><td><c:out value="${j.shift_name}" default="Completo"/><small class="d-block"><c:out value="${j.salary}"/></small></td><td><c:out value="${j.closing_date}" default="Sin fecha"/></td><td><hp:status value="${j.stage}"/></td><td><div class="job-actions d-flex flex-wrap gap-1 align-items-center"><a class="btn btn-success btn-sm" href="${jobsPath}?view=${j.job_id}" aria-label="Ver convocatoria ${j.job_id}"><i class="fa-solid fa-eye"></i> Ver</a>
<c:if test="${manageMode}">
    <a class="btn btn-primary btn-sm position-relative" href="${jobsPath}?tab=applications" title="Ver postulaciones recibidas">
        <i class="fa-solid fa-users me-1"></i> Postulantes <c:if test="${j.applicant_count > 0}"><span class="badge bg-white text-dark ms-1">${j.applicant_count}</span></c:if>
    </a>
    <c:if test="${adminMode ? j.managed_by == sessionScope.userId : empty j.managed_by}">
        <a class="btn btn-outline-secondary btn-sm" href="${jobsPath}?edit=${j.job_id}" title="Editar convocatoria"><i class="fa-solid fa-pen-to-square"></i></a>
        <c:choose>
            <c:when test="${j.stage == 'BORRADOR'}">
                <form method="post" action="${jobsPath}" class="d-inline">
                    <hp:csrf/><input type="hidden" name="action" value="changeJobStatus"><input type="hidden" name="id" value="${j.job_id}"><input type="hidden" name="newStatus" value="PUBLICADA">
                    <button class="btn btn-success btn-sm" title="Publicar convocatoria"><i class="fa-solid fa-upload me-1"></i> Publicar</button>
                </form>
            </c:when>
            <c:when test="${j.stage == 'PUBLICADA' or j.stage == 'ongoing'}">
                <form method="post" action="${jobsPath}" class="d-inline">
                    <hp:csrf/><input type="hidden" name="action" value="changeJobStatus"><input type="hidden" name="id" value="${j.job_id}"><input type="hidden" name="newStatus" value="PAUSADA">
                    <button class="btn btn-warning btn-sm" title="Pausar temporalmente"><i class="fa-solid fa-pause"></i></button>
                </form>
                <form method="post" action="${jobsPath}" class="d-inline" onsubmit="return confirm('¿Confirma que desea cerrar esta convocatoria?');">
                    <hp:csrf/><input type="hidden" name="action" value="changeJobStatus"><input type="hidden" name="id" value="${j.job_id}"><input type="hidden" name="newStatus" value="CERRADA">
                    <button class="btn btn-outline-danger btn-sm" title="Cerrar convocatoria"><i class="fa-solid fa-lock"></i></button>
                </form>
            </c:when>
            <c:when test="${j.stage == 'PAUSADA'}">
                <form method="post" action="${jobsPath}" class="d-inline">
                    <hp:csrf/><input type="hidden" name="action" value="changeJobStatus"><input type="hidden" name="id" value="${j.job_id}"><input type="hidden" name="newStatus" value="PUBLICADA">
                    <button class="btn btn-success btn-sm" title="Reanudar publicación"><i class="fa-solid fa-play"></i></button>
                </form>
                <form method="post" action="${jobsPath}" class="d-inline" onsubmit="return confirm('¿Confirma que desea cerrar esta convocatoria?');">
                    <hp:csrf/><input type="hidden" name="action" value="changeJobStatus"><input type="hidden" name="id" value="${j.job_id}"><input type="hidden" name="newStatus" value="CERRADA">
                    <button class="btn btn-outline-danger btn-sm" title="Cerrar convocatoria"><i class="fa-solid fa-lock"></i></button>
                </form>
            </c:when>
            <c:when test="${j.stage == 'CERRADA' or j.stage == 'finished'}">
                <form method="post" action="${jobsPath}" class="d-inline">
                    <hp:csrf/><input type="hidden" name="action" value="changeJobStatus"><input type="hidden" name="id" value="${j.job_id}"><input type="hidden" name="newStatus" value="PUBLICADA">
                    <button class="btn btn-outline-success btn-sm" title="Reabrir convocatoria"><i class="fa-solid fa-rotate-right me-1"></i> Reabrir</button>
                </form>
            </c:when>
        </c:choose>
    </c:if>
    <c:if test="${adminMode or empty j.managed_by}">
        <form method="post" action="${jobsPath}" class="d-inline">
            <hp:csrf/><input type="hidden" name="action" value="archive"><input type="hidden" name="id" value="${j.job_id}"><input type="hidden" name="archived" value="${j.stage == 'archived' ? 'no' : 'yes'}">
            <button class="btn btn-outline-secondary btn-sm" title="${j.stage == 'archived' ? 'Restaurar' : 'Archivar'}"><i class="fa-solid ${j.stage == 'archived' ? 'fa-box-open' : 'fa-box-archive'}"></i></button>
        </form>
    </c:if>
</c:if>
<c:if test="${not manageMode and (j.stage == 'PUBLICADA' or j.stage == 'ongoing')}"><c:set var="alreadyApplied" value="false"/><c:forEach var="a" items="${applications}"><c:if test="${a.job_id == j.job_id}"><c:set var="alreadyApplied" value="true"/></c:if></c:forEach><c:choose><c:when test="${alreadyApplied}"><span class="badge bg-light text-secondary border">Postulado</span></c:when><c:otherwise><details><summary class="btn btn-outline-success btn-sm">Quiero postular</summary><form method="post" action="${jobsPath}" class="application-form mt-2"><hp:csrf/><input type="hidden" name="action" value="apply"><input type="hidden" name="id" value="${j.job_id}"><label class="form-label">Presentación<textarea class="form-control" name="message" maxlength="5000" rows="3" required placeholder="Explica brevemente tu motivación y disponibilidad para esta vacante..."></textarea></label><label class="small d-block mb-2"><input type="checkbox" name="consent" value="yes" required> Compartir mi CV con esta empresa para el proceso de selección.</label><button class="btn btn-success btn-sm">Confirmar postulación</button></form></details></c:otherwise></c:choose></c:if>
</div></td></tr></c:forEach></tbody></table></div><c:if test="${empty jobs}"><p class="alert alert-info">No hay convocatorias para estos filtros.</p></c:if></section>
<script>
function askAI(action, targetFieldId, contextFieldId) {
    const target = document.getElementById(targetFieldId);
    if (!target) return;
    const currentVal = target.value.trim();
    const contextVal = contextFieldId ? document.getElementById(contextFieldId)?.value.trim() : '';
    if (action === 'job_description' && !contextVal && !currentVal) {
        alert('Por favor ingrese el título de la convocatoria para que la IA pueda generar las funciones.');
        document.getElementById(contextFieldId)?.focus();
        return;
    }
    const originalPlaceholder = target.placeholder;
    target.disabled = true;
    target.placeholder = 'Generando sugerencia con Inteligencia Artificial...';
    const params = new URLSearchParams();
    params.append('action', action);
    params.append('prompt', currentVal);
    params.append('jobTitle', contextVal);
    params.append('career', '');
    fetch('${pageContext.request.contextPath}/api/ai-assistant', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString()
    })
    .then(r => r.json())
    .then(data => {
        target.disabled = false;
        target.placeholder = originalPlaceholder;
        if (data.success && data.suggestion) {
            target.value = data.suggestion;
        } else {
            alert(data.error || 'No se pudo generar la sugerencia. Por favor intente nuevamente.');
        }
    })
    .catch(err => {
        target.disabled = false;
        target.placeholder = originalPlaceholder;
        alert('Error al comunicarse con el asistente de IA.');
    });
}
</script>
</c:if>
<%@ include file="applications.jspf" %>
<%@ include file="footer.jsp" %>
