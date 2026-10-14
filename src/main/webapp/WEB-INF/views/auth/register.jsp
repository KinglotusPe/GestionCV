<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jspf" %>

<div class="row justify-content-center py-4">
    <div class="col-md-8 col-lg-6">
        <div class="card p-4 p-md-5 shadow-sm">
            <div class="text-center mb-4">
                <img src="${pageContext.request.contextPath}/assets/images/logo_hierro_pozo.png" 
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

                <!-- Campos específicos para Egresados -->
                <div class="graduate-fields">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Nombres</label>
                            <input class="form-control" type="text" name="firstName" placeholder="Nombres completos">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Apellidos</label>
                            <input class="form-control" type="text" name="lastName" placeholder="Apellidos completos">
                        </div>
                    </div>
                </div>

                <!-- Campos específicos para Empresas -->
                <div class="company-fields" style="display: none;">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Razón Social / Nombre Comercial</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fa-solid fa-building"></i></span>
                            <input class="form-control" type="text" name="companyName" placeholder="Nombre de la empresa">
                        </div>
                    </div>
                </div>

                <div class="mt-4">
                    <button class="btn btn-warning w-100 py-2 fw-bold text-dark" type="submit">
                        <i class="fa-solid fa-check me-1"></i> Completar Registro
                    </button>
                </div>
            </form>

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

<%@ include file="../common/footer.jspf" %>
