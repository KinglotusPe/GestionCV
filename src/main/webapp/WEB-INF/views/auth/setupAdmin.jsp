<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jsp" %>

<div class="row justify-content-center py-4">
    <div class="col-md-6 col-lg-5">
        <div class="card p-4 p-md-5 shadow-sm">
            <div class="text-center mb-4">
                <div class="brand-mark mx-auto mb-2" style="width: 50px; height: 50px; background: #b91c1c; color: #fff;">
                    <i class="fa-solid fa-shield-halved"></i>
                </div>
                <h3 class="section-title mb-1">Configuración Administrativa Inicial</h3>
                <p class="text-muted small">Creación del primer superadministrador del sistema</p>
            </div>

            <c:choose>
                <c:when test="${locked}">
                    <div class="alert alert-danger text-center">
                        <i class="fa-solid fa-lock fa-2x mb-2 d-block"></i>
                        <strong>La creación inicial de administrador está deshabilitada.</strong>
                        <p class="small mb-0 mt-2">Por motivos de seguridad, esta acción está inhabilitada.</p>
                    </div>
                    <div class="text-center mt-3">
                        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/login">
                            Ir al Inicio de Sesión
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-info small">
                        <i class="fa-solid fa-circle-info me-1"></i>
                        Esta operación solo se puede realizar una única vez al iniciar el despliegue del sistema.
                    </div>

                    <form method="post" action="${pageContext.request.contextPath}/setup-admin">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <div class="mb-3">
                            <label class="form-label fw-bold">Clave de instalación</label><input class="form-control mb-3" type="password" name="setupToken" required autocomplete="off"><label class="form-label fw-bold">Correo de Administrador</label>
                            <input class="form-control" type="email" name="email" placeholder="admin@hierropozo.edu.pe" required>
                        </div>
                        <div class="mb-4">
                            <label class="form-label fw-bold">Contraseña (Mínimo 8 caracteres)</label>
                            <input class="form-control" type="password" name="password" placeholder="••••••••" minlength="8" required>
                        </div>
                        <button class="btn btn-danger w-100 py-2 fw-bold" type="submit">
                            <i class="fa-solid fa-user-shield me-1"></i> Crear Administrador Inicial
                        </button>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
