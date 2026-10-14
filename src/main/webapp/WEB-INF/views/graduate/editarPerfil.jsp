<%@ page pageEncoding="UTF-8" %>
<form method="post" action="${pageContext.request.contextPath}/graduate/profile">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
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
                        <input class="form-control" type="number" name="graduationYear" value="${graduate.graduationYear}" min="1950" max="2100">
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