<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jsp" %>

<div class="row justify-content-center py-4">
    <div class="col-md-8 col-lg-6">
        <div class="card p-4 p-md-5 shadow-sm">
            <div class="text-center mb-4">
                <img src="${pageContext.request.contextPath}/assets/images/logo.png"
                     alt="Logo Hierro Pozo"
                     class="mb-2"
                     style="width: 70px; height: 70px; object-fit: contain;">
                <h3 class="section-title mb-1">Registro de Nuevo Usuario</h3>
                <p class="text-muted small">Cree su cuenta institucional de Egresado o Empresa colaboradora</p>
            </div>

            <form method="post" action="${pageContext.request.contextPath}/register">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <!-- Tipo de Rol -->
                <div class="mb-3">
                    <label class="form-label fw-bold">Tipo de Cuenta / Rol</label>
                    <select class="form-select" id="role" name="role" required>
                        <option value="graduate" selected>Egresado del Instituto</option>
                        <option value="company">Empresa Empleadora</option>
                    </select>
                </div>

                <!-- Credenciales Básicas -->
                <div class="mb-3">
                    <label class="form-label fw-bold">Correo Electrónico</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fa-solid fa-envelope"></i></span>
                        <input class="form-control" type="email" id="registerEmail" name="email" placeholder="correo@ejemplo.com" required>
                    </div>
                    <div id="emailFeedback" class="small mt-1 fw-bold"></div>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold">Contraseña (Mínimo 8 caracteres)</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fa-solid fa-key"></i></span>
                        <input class="form-control" type="password" name="password" placeholder="••••••••" minlength="8" required>
                    </div>
                </div>

                <!-- Campos específicos para Estudiantes y Egresados -->
                <div class="graduate-fields" id="graduateFields">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">DNI <span class="text-danger">*</span></label>
                            <input class="form-control" type="text" name="documentNumber" id="gradDni" placeholder="8 dígitos" maxlength="8" pattern="[0-9]{8}">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Celular</label>
                            <input class="form-control" type="tel" name="phone" placeholder="987654321" maxlength="15">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Nombres <span class="text-danger">*</span></label>
                            <input class="form-control" type="text" name="firstName" id="gradFirstName" placeholder="Nombres completos">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Apellidos <span class="text-danger">*</span></label>
                            <input class="form-control" type="text" name="lastName" id="gradLastName" placeholder="Apellidos completos">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Código Institucional</label>
                            <input class="form-control" type="text" name="institutionalCode" placeholder="Ej. 2024-HP-0123" maxlength="30">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Estado Académico</label>
                            <select class="form-select" name="academicStatus">
                                <option value="ESTUDIANTE">ESTUDIANTE (Cursando)</option>
                                <option value="EGRESADO" selected>EGRESADO (Concluido)</option>
                            </select>
                        </div>
                        <div class="col-12 mb-3">
                            <label class="form-label fw-bold">Programa de Estudios / Carrera</label>
                            <select class="form-select" name="careerId">
                                <option value="">Seleccione su carrera...</option>
                                <c:forEach var="car" items="${careers}">
                                    <option value="${car.careerId}"><c:out value="${car.name}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Año de Ingreso</label>
                            <input class="form-control" type="number" name="entryYear" placeholder="Ej. 2022" min="1990" max="2035">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Año de Egreso (si aplica)</label>
                            <input class="form-control" type="number" name="graduationYear" placeholder="Ej. 2025" min="1990" max="2035">
                        </div>
                    </div>
                </div>

                <!-- Campos específicos para Empresas -->
                <div class="company-fields" id="companyFields" style="display: none;">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">RUC <span class="text-danger">*</span></label>
                            <input class="form-control" type="text" name="ruc" id="compRuc" placeholder="11 dígitos" maxlength="11" pattern="[0-9]{11}">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Teléfono / Celular</label>
                            <input class="form-control" type="tel" name="phone" placeholder="Teléfono de contacto">
                        </div>
                        <div class="col-12 mb-3">
                            <label class="form-label fw-bold">Razón Social <span class="text-danger">*</span></label>
                            <input class="form-control" type="text" name="companyName" id="compName" placeholder="Razón social registrada en SUNAT">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Nombre Comercial</label>
                            <input class="form-control" type="text" name="tradeName" placeholder="Nombre comercial (marca)">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Rubro / Sector</label>
                            <input class="form-control" type="text" name="industry" placeholder="Ej. Tecnología, Minería, Educación">
                        </div>
                        <div class="col-12 mb-3">
                            <label class="form-label fw-bold">Dirección Fiscal / Sede</label>
                            <input class="form-control" type="text" name="address" placeholder="Av. / Jr. / Calle...">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Representante Legal</label>
                            <input class="form-control" type="text" name="representativeName" placeholder="Nombres y apellidos del representante">
                        </div>
                        <div class="col-md-3 mb-3">
                            <label class="form-label fw-bold">DNI Representante</label>
                            <input class="form-control" type="text" name="representativeDni" placeholder="8 dígitos" maxlength="8">
                        </div>
                        <div class="col-md-3 mb-3">
                            <label class="form-label fw-bold">Cargo</label>
                            <input class="form-control" type="text" name="representativePosition" placeholder="Ej. Gerente General">
                        </div>
                    </div>
                </div>

                <div class="mt-4">
                    <button class="btn btn-warning w-100 py-2 fw-bold text-dark" type="submit">
                        <i class="fa-solid fa-check me-1"></i> Completar Registro
                    </button>
                </div>
            </form>
            <script>
            (function() {
                const roleSelect = document.getElementById('role');
                const gradFields = document.getElementById('graduateFields');
                const compFields = document.getElementById('companyFields');
                const gradDni = document.getElementById('gradDni');
                const gradFirstName = document.getElementById('gradFirstName');
                const gradLastName = document.getElementById('gradLastName');
                const compRuc = document.getElementById('compRuc');
                const compName = document.getElementById('compName');

                function updateRole() {
                    const isGrad = roleSelect.value === 'graduate';
                    gradFields.style.display = isGrad ? 'block' : 'none';
                    compFields.style.display = isGrad ? 'none' : 'block';

                    if (isGrad) {
                        gradDni.setAttribute('required', 'required');
                        gradFirstName.setAttribute('required', 'required');
                        gradLastName.setAttribute('required', 'required');
                        compRuc.removeAttribute('required');
                        compName.removeAttribute('required');
                    } else {
                        gradDni.removeAttribute('required');
                        gradFirstName.removeAttribute('required');
                        gradLastName.removeAttribute('required');
                        compRuc.setAttribute('required', 'required');
                        compName.setAttribute('required', 'required');
                    }
                }
                roleSelect.addEventListener('change', updateRole);
                updateRole();
            })();
            </script>

            <hr class="my-4">

            <div class="text-center">
                <p class="mb-0 text-muted small">¿Ya tiene una cuenta creada?</p>
                <a class="fw-bold text-decoration-none text-success" href="${pageContext.request.contextPath}/login">
                    <i class="fa-solid fa-arrow-right-to-bracket me-1"></i> Inicie Sesión aquí
                </a>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
