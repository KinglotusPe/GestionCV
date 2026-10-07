<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jspf" %>

<div class="row">
    <!-- Columna Izquierda: Información de Perfil -->
    <div class="col-lg-8 mb-4">
        <div class="card p-4 shadow-sm">
            <h3 class="section-title mb-3">
                <i class="fa-solid fa-user-pen me-2"></i> Datos Personales y Profesionales
            </h3>

            <form method="post" action="${pageContext.request.contextPath}/graduate/profile">
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Nombres</label>
                        <input class="form-control" type="text" name="firstName" value="<c:out value='${graduate.firstName}'/>" required>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Apellidos</label>
                        <input class="form-control" type="text" name="lastName" value="<c:out value='${graduate.lastName}'/>" required>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-4 mb-3">
                        <label class="form-label fw-bold">Tipo de Documento</label>
                        <select class="form-select" name="documentType">
                            <option value="DNI" ${graduate.documentType == 'DNI' ? 'selected' : ''}>DNI</option>
                            <option value="CE" ${graduate.documentType == 'CE' ? 'selected' : ''}>Carné Extranjería</option>
                        </select>
                    </div>
                    <div class="col-md-4 mb-3">
                        <label class="form-label fw-bold">Número de Documento</label>
                        <input class="form-control" type="text" name="documentNumber" value="<c:out value='${graduate.documentNumber}'/>">
                    </div>
                    <div class="col-md-4 mb-3">
                        <label class="form-label fw-bold">Teléfono / Celular</label>
                        <input class="form-control" type="text" name="phone" value="<c:out value='${graduate.phone}'/>">
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Programa de Estudios (Carrera)</label>
                        <select class="form-select" name="careerId">
                            <option value="">-- Seleccione una Carrera --</option>
                            <c:forEach var="c" items="${careers}">
                                <option value="${c.careerId}" ${graduate.careerId == c.careerId ? 'selected' : ''}>
                                    <c:out value="${c.name}"/> (<c:out value="${c.code}"/>)
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Año de Graduación</label>
                        <input class="form-control" type="number" name="graduationYear" value="${graduate.graduationYear}" min="1990" max="2030">
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Dirección</label>
                        <input class="form-control" type="text" name="address" value="<c:out value='${graduate.address}'/>">
                    </div>
                    <div class="col-md-3 mb-3">
                        <label class="form-label fw-bold">Ciudad</label>
                        <input class="form-control" type="text" name="city" value="<c:out value='${graduate.city}'/>">
                    </div>
                    <div class="col-md-3 mb-3">
                        <label class="form-label fw-bold">País</label>
                        <input class="form-control" type="text" name="country" value="<c:out value='${graduate.country}'/>">
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-4 mb-3">
                        <label class="form-label fw-bold">Fecha de Nacimiento</label>
                        <input class="form-control" type="date" name="birthDate" value="${graduate.birthDate}">
                    </div>
                    <div class="col-md-4 mb-3">
                        <label class="form-label fw-bold">LinkedIn URL</label>
                        <input class="form-control" type="url" name="linkedinUrl" value="<c:out value='${graduate.linkedinUrl}'/>" placeholder="https://linkedin.com/in/...">
                    </div>
                    <div class="col-md-4 mb-3">
                        <label class="form-label fw-bold">Portafolio / Web</label>
                        <input class="form-control" type="url" name="portfolioUrl" value="<c:out value='${graduate.portfolioUrl}'/>" placeholder="https://...">
                    </div>
                </div>

                <div class="form-check form-switch mb-4">
                    <input class="form-check-input" type="checkbox" id="publicProfile" name="publicProfile" ${graduate.publicProfile ? 'checked' : ''}>
                    <label class="form-check-label fw-bold" for="publicProfile">Permitir que las empresas encuentren mi perfil en las búsquedas</label>
                </div>

                <button class="btn btn-success px-4 fw-bold" type="submit">
                    <i class="fa-solid fa-floppy-disk me-1"></i> Guardar Cambios
                </button>
            </form>
        </div>
    </div>

    <!-- Columna Derecha: Subida de Foto de Perfil -->
    <div class="col-lg-4 mb-4">
        <div class="card p-4 shadow-sm text-center">
            <h4 class="section-title mb-3">
                <i class="fa-solid fa-camera me-1"></i> Foto de Perfil
            </h4>

            <div class="mb-3">
                <c:choose>
                    <c:when test="${not empty graduate.photoUrl}">
                        <div class="alert alert-info py-2 small">
                            <i class="fa-solid fa-check me-1"></i> Foto registrada: <br>
                            <code><c:out value="${graduate.photoUrl}"/></code>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="p-4 bg-light rounded-circle d-inline-block mb-2 text-secondary">
                            <i class="fa-solid fa-user fa-4x"></i>
                        </div>
                        <p class="small text-muted">Aún no ha cargado una fotografía de perfil.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <form method="post" action="${pageContext.request.contextPath}/graduate/upload" enctype="multipart/form-data">
                <input type="hidden" name="type" value="photo">
                <div class="mb-3 text-start">
                    <label class="form-label small fw-bold">Seleccionar imagen (JPG o PNG):</label>
                    <input class="form-control form-control-sm" type="file" name="file" accept="image/png, image/jpeg" required>
                </div>
                <button class="btn btn-outline-success btn-sm w-100 fw-bold" type="submit">
                    <i class="fa-solid fa-upload me-1"></i> Subir Fotografía
                </button>
            </form>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jspf" %>
