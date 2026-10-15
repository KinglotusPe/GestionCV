<%@ page pageEncoding="UTF-8" %>
<form method="post" action="${pageContext.request.contextPath}/graduate/profile">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <c:if test="${isApproved}">
                    <div class="alert alert-info py-2 px-3 small mb-3">
                        <i class="fa-solid fa-shield-halved me-1"></i> <strong>Cuenta Verificada:</strong> Por seguridad institucional, los datos de identidad y académicos (DNI, nombres, apellidos, código y carrera) están bloqueados para modificaciones directas.
                    </div>
                </c:if>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Nombres <c:if test="${isApproved}"><span class="badge bg-secondary ms-1"><i class="fa-solid fa-lock"></i> Bloqueado</span></c:if></label>
                        <input class="form-control ${isApproved ? 'bg-light' : ''}" type="text" name="firstName" value="<c:out value='${graduate.firstName}'/>" ${isApproved ? 'readonly' : 'required'}>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Apellidos <c:if test="${isApproved}"><span class="badge bg-secondary ms-1"><i class="fa-solid fa-lock"></i> Bloqueado</span></c:if></label>
                        <input class="form-control ${isApproved ? 'bg-light' : ''}" type="text" name="lastName" value="<c:out value='${graduate.lastName}'/>" ${isApproved ? 'readonly' : 'required'}>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-4 mb-3">
                        <label class="form-label fw-bold">DNI <c:if test="${isApproved}"><span class="badge bg-secondary ms-1"><i class="fa-solid fa-lock"></i> Bloqueado</span></c:if></label>
                        <input class="form-control ${isApproved ? 'bg-light' : ''}" type="text" name="documentNumber" value="<c:out value='${graduate.documentNumber}'/>" ${isApproved ? 'readonly' : ''} maxlength="8">
                    </div>
                    <div class="col-md-4 mb-3">
                        <label class="form-label fw-bold">Código Institucional <c:if test="${isApproved}"><span class="badge bg-secondary ms-1"><i class="fa-solid fa-lock"></i> Bloqueado</span></c:if></label>
                        <input class="form-control ${isApproved ? 'bg-light' : ''}" type="text" name="institutionalCode" value="<c:out value='${graduate.institutionalCode}'/>" ${isApproved ? 'readonly' : ''} maxlength="30">
                    </div>
                    <div class="col-md-4 mb-3">
                        <label class="form-label fw-bold">Estado Académico <c:if test="${isApproved}"><span class="badge bg-secondary ms-1"><i class="fa-solid fa-lock"></i> Bloqueado</span></c:if></label>
                        <c:choose>
                            <c:when test="${isApproved}">
                                <input class="form-control bg-light" type="text" value="${graduate.academicStatus}" readonly>
                            </c:when>
                            <c:otherwise>
                                <select class="form-select" name="academicStatus">
                                    <option value="ESTUDIANTE" ${graduate.academicStatus == 'ESTUDIANTE' ? 'selected' : ''}>ESTUDIANTE</option>
                                    <option value="EGRESADO" ${graduate.academicStatus == 'EGRESADO' or empty graduate.academicStatus ? 'selected' : ''}>EGRESADO</option>
                                </select>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Programa de Estudios (Carrera) <c:if test="${isApproved}"><span class="badge bg-secondary ms-1"><i class="fa-solid fa-lock"></i> Bloqueado</span></c:if></label>
                        <c:choose>
                            <c:when test="${isApproved}">
                                <input class="form-control bg-light" type="text" value="<c:out value='${graduate.careerName}'/>" readonly>
                            </c:when>
                            <c:otherwise>
                                <select class="form-select" name="careerId">
                                    <option value="">-- Seleccione una Carrera --</option>
                                    <c:forEach var="c" items="${careers}">
                                        <option value="${c.careerId}" ${graduate.careerId == c.careerId ? 'selected' : ''}>
                                            <c:out value="${c.name}"/> (<c:out value="${c.code}"/>)
                                        </option>
                                    </c:forEach>
                                </select>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="col-md-3 mb-3">
                        <label class="form-label fw-bold">Año de Ingreso</label>
                        <input class="form-control" type="number" name="entryYear" value="${graduate.entryYear}" min="1950" max="2035">
                    </div>
                    <div class="col-md-3 mb-3">
                        <label class="form-label fw-bold">Año de Egreso</label>
                        <input class="form-control" type="number" name="graduationYear" value="${graduate.graduationYear}" min="1950" max="2035">
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-12 mb-3">
                        <label class="form-label fw-bold">Teléfono / Celular</label>
                        <input class="form-control" type="text" name="phone" value="<c:out value='${graduate.phone}'/>">
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