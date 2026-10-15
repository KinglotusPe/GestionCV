<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="../common/header.jsp" %>
<div class="card p-4 shadow-sm">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="h4 mb-0"><i class="fa-solid fa-building me-2 text-success"></i> Datos de la Empresa</h2>
        <c:choose>
            <c:when test="${company.user_status == 'active'}">
                <span class="badge bg-success px-3 py-2"><i class="fa-solid fa-circle-check me-1"></i> Empresa Aprobada</span>
            </c:when>
            <c:otherwise>
                <span class="badge bg-warning text-dark px-3 py-2"><i class="fa-solid fa-clock me-1"></i> Pendiente de Aprobación</span>
            </c:otherwise>
        </c:choose>
    </div>

    <c:if test="${company.user_status == 'active'}">
        <div class="alert alert-info py-2 px-3 small mb-3">
            <i class="fa-solid fa-shield-halved me-1"></i> <strong>Empresa Verificada:</strong> Por seguridad jurídica institucional, el RUC y la Razón Social están bloqueados tras la aprobación de la cuenta.
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/company/profile" class="row g-3">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

        <div class="col-md-6">
            <label class="form-label fw-bold">Razón Social <c:if test="${company.user_status == 'active'}"><span class="badge bg-secondary ms-1"><i class="fa-solid fa-lock"></i> Bloqueado</span></c:if></label>
            <input class="form-control ${company.user_status == 'active' ? 'bg-light' : ''}" name="companyName" maxlength="150" required value="<c:out value='${company.company_name}'/>" ${company.user_status == 'active' ? 'readonly' : ''}>
        </div>

        <div class="col-md-6">
            <label class="form-label fw-bold">RUC <c:if test="${company.user_status == 'active'}"><span class="badge bg-secondary ms-1"><i class="fa-solid fa-lock"></i> Bloqueado</span></c:if></label>
            <input class="form-control ${company.user_status == 'active' ? 'bg-light' : ''}" name="ruc" maxlength="11" pattern="[0-9]{11}" value="<c:out value='${company.ruc}'/>" ${company.user_status == 'active' ? 'readonly' : ''}>
        </div>

        <div class="col-md-6">
            <label class="form-label fw-bold">Nombre Comercial</label>
            <input class="form-control" name="tradeName" maxlength="150" value="<c:out value='${company.trade_name}'/>" placeholder="Nombre comercial de la marca">
        </div>

        <div class="col-md-6">
            <label class="form-label fw-bold">Rubro / Sector</label>
            <input class="form-control" name="industry" maxlength="100" value="<c:out value='${company.industry}'/>" placeholder="Ej. Minería, Servicios, Tecnología">
        </div>

        <div class="col-12">
            <label class="form-label fw-bold">Dirección Fiscal / Sede</label>
            <input class="form-control" name="address" maxlength="200" value="<c:out value='${company.address}'/>" placeholder="Av. / Jr. / Calle...">
        </div>

        <div class="col-md-4">
            <label class="form-label fw-bold">Teléfono de Contacto</label>
            <input class="form-control" name="phone" type="tel" value="<c:out value='${company.phone}'/>">
        </div>

        <div class="col-md-4">
            <label class="form-label fw-bold">Tamaño de la Empresa</label>
            <select class="form-select" name="companySize">
                <option value="Microempresa" ${company.company_size == 'Microempresa' ? 'selected' : ''}>Microempresa (1-10)</option>
                <option value="Pequeña empresa" ${company.company_size == 'Pequeña empresa' ? 'selected' : ''}>Pequeña empresa (11-50)</option>
                <option value="Mediana empresa" ${company.company_size == 'Mediana empresa' ? 'selected' : ''}>Mediana empresa (51-250)</option>
                <option value="Gran empresa" ${company.company_size == 'Gran empresa' ? 'selected' : ''}>Gran empresa (>250)</option>
            </select>
        </div>

        <div class="col-md-4">
            <label class="form-label fw-bold">Sitio Web</label>
            <input class="form-control" name="website" type="url" maxlength="255" value="<c:out value='${company.website}'/>" placeholder="https://...">
        </div>

        <div class="col-md-5">
            <label class="form-label fw-bold">Representante Legal</label>
            <input class="form-control" name="representativeName" maxlength="120" value="<c:out value='${company.representative_name}'/>">
        </div>

        <div class="col-md-3">
            <label class="form-label fw-bold">DNI Representante</label>
            <input class="form-control" name="representativeDni" maxlength="20" value="<c:out value='${company.representative_dni}'/>">
        </div>

        <div class="col-md-4">
            <label class="form-label fw-bold">Cargo</label>
            <input class="form-control" name="representativePosition" maxlength="80" value="<c:out value='${company.representative_position}'/>" placeholder="Ej. Gerente General">
        </div>

        <div class="col-12">
            <label class="form-label fw-bold">Descripción de la Empresa</label>
            <textarea class="form-control" name="description" rows="4" maxlength="5000" placeholder="Reseña corporativa, valores o actividad comercial..."><c:out value="${company.description}"/></textarea>
        </div>

        <div class="col-12 mt-3">
            <button class="btn btn-success fw-bold px-4" type="submit">
                <i class="fa-solid fa-floppy-disk me-1"></i> Guardar perfil
            </button>
        </div>
    </form>
</div>
<%@ include file="../common/footer.jsp" %>
