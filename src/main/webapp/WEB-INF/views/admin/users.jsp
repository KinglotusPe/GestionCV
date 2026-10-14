<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jspf" %>

<div class="mb-4">
    <h2 class="section-title mb-1">
        <i class="fa-solid fa-users-gear me-2"></i> Gestión de Usuarios del Sistema
    </h2>
    <p class="text-muted small">Administración de cuentas, aprobación de solicitudes y control de accesos</p>
</div>

<c:if test="${not empty resetLink}"><div class="alert alert-warning"><strong>Recuperación para <c:out value="${resetEmail}"/></strong><p>Verifique la identidad del titular y entréguele este enlace por un canal privado. Caduca en 30 minutos y se utiliza una sola vez.</p><a href="<c:out value='${resetLink}'/>">Enlace de recuperación</a></div></c:if>
<!-- Filtro de Usuarios -->
<div class="card p-3 shadow-sm mb-4">
    <form class="row g-3" method="get" action="${pageContext.request.contextPath}/admin/users">
        <div class="col-md-4">
            <label class="form-label small fw-bold">Filtrar por Rol</label>
            <select class="form-select" name="role">
                <option value="">Todos los roles</option>
                <option value="graduate" ${param.role == 'graduate' ? 'selected' : ''}>Egresado</option>
                <option value="company" ${param.role == 'company' ? 'selected' : ''}>Empresa</option>
                <option value="admin" ${param.role == 'admin' ? 'selected' : ''}>Administrador</option>
            </select>
        </div>
        <div class="col-md-4">
            <label class="form-label small fw-bold">Filtrar por Estado</label>
            <select class="form-select" name="status">
                <option value="">Todos los estados</option>
                <option value="pending" ${param.status == 'pending' ? 'selected' : ''}>Pendiente (pending)</option>
                <option value="active" ${param.status == 'active' ? 'selected' : ''}>Activo (active)</option>
                <option value="inactive" ${param.status == 'inactive' ? 'selected' : ''}>Inactivo (inactive)</option>
                <option value="suspended" ${param.status == 'suspended' ? 'selected' : ''}>Suspendido (suspended)</option>
            </select>
        </div>
        <div class="col-md-4 d-flex align-items-end">
            <button class="btn btn-success w-100 fw-bold" type="submit">
                <i class="fa-solid fa-filter me-1"></i> Aplicar Filtros
            </button>
        </div>
    </form>
</div>

<!-- Tabla de Usuarios -->
<div class="card shadow-sm overflow-hidden">
    <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
                <tr>
                    <th style="width: 80px;">ID</th>
                    <th>Correo Electrónico</th>
                    <th>Rol</th>
                    <th>Estado de Cuenta</th>
                    <th style="width: 200px;">Acciones de Estado</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="u" items="${users}">
                    <tr>
                        <td><strong>#${u.userId}</strong></td>
                        <td><c:out value="${u.email}"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${u.role == 'admin'}">
                                    <span class="badge bg-danger">Administrador</span>
                                </c:when>
                                <c:when test="${u.role == 'company'}">
                                    <span class="badge bg-primary">Empresa</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-success">Egresado</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${u.status == 'active'}">
                                    <span class="badge bg-success">Activo</span>
                                </c:when>
                                <c:when test="${u.status == 'pending'}">
                                    <span class="badge bg-warning text-dark">Pendiente</span>
                                </c:when>
                                <c:when test="${u.status == 'suspended'}">
                                    <span class="badge bg-danger">Suspendido</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary">Inactivo</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
<details><summary class="btn btn-outline-success btn-sm">Administrar</summary>
<form method="post" action="${pageContext.request.contextPath}/admin/users" class="mt-2">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
<input type="hidden" name="id" value="${u.userId}">
<label class="form-label">Rol</label><select class="form-select mb-2" name="newRole">
<option value="graduate" ${u.role == 'graduate' ? 'selected' : ''}>Egresado</option>
<option value="company" ${u.role == 'company' ? 'selected' : ''}>Empresa</option>
<option value="admin" ${u.role == 'admin' ? 'selected' : ''}>Administrador</option></select>
<label class="form-label">Estado</label><select class="form-select mb-2" name="status">
<option value="active" ${u.status == 'active' ? 'selected' : ''}>Activo</option>
<option value="pending" ${u.status == 'pending' ? 'selected' : ''}>Pendiente</option>
<option value="inactive" ${u.status == 'inactive' ? 'selected' : ''}>Inactivo</option>
<option value="suspended" ${u.status == 'suspended' ? 'selected' : ''}>Suspendido</option></select>
<p class="small">Si cambia a un rol sin perfil previo, complete sus datos:</p>
<input class="form-control mb-2" name="firstName" placeholder="Nombres del egresado" maxlength="80">
<input class="form-control mb-2" name="lastName" placeholder="Apellidos del egresado" maxlength="100">
<input class="form-control mb-2" name="companyName" placeholder="Nombre de empresa" maxlength="150">
<button class="btn btn-success btn-sm" name="action" value="update">Guardar rol y estado</button></form>
<form method="post" action="${pageContext.request.contextPath}/admin/users" class="mt-2 confirm-action">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
<input type="hidden" name="id" value="${u.userId}"><button class="btn btn-outline-secondary btn-sm" name="action" value="resetPassword">Generar enlace de recuperación</button></form></details>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty users}">
                    <tr>
                        <td colspan="5" class="text-center py-4 text-muted">
                            No se encontraron usuarios coincidentes con el filtro.
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="../common/footer.jspf" %>
