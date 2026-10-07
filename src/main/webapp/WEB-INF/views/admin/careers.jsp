<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jspf" %>

<div class="mb-4">
    <h2 class="section-title mb-1">
        <i class="fa-solid fa-graduation-cap me-2"></i> Gestión de Programas de Estudio (Carreras)
    </h2>
    <p class="text-muted small">Mantenimiento del catálogo académico del IESP "Manuel Antonio Hierro Pozo"</p>
</div>

<!-- Formulario para Registrar Nueva Carrera -->
<div class="card p-4 shadow-sm mb-4">
    <h5 class="fw-bold text-success mb-3">
        <i class="fa-solid fa-plus-circle me-1"></i> Registrar Nueva Carrera Técnica
    </h5>

    <form method="post" action="${pageContext.request.contextPath}/admin/careers" class="row g-3">
        <input type="hidden" name="action" value="create">

        <div class="col-md-4">
            <label class="form-label small fw-bold">Nombre del Programa</label>
            <input class="form-control" type="text" name="name" placeholder="Ej: Electrónica Digital" required>
        </div>

        <div class="col-md-2">
            <label class="form-label small fw-bold">Código Oficial</label>
            <input class="form-control" type="text" name="code" placeholder="Ej: ED" required>
        </div>

        <div class="col-md-3">
            <label class="form-label small fw-bold">Descripción / Especialidad</label>
            <input class="form-control" type="text" name="description" placeholder="Breve resumen del perfil técnico">
        </div>

        <div class="col-md-1">
            <label class="form-label small fw-bold">Años</label>
            <input class="form-control" type="number" name="durationYears" value="3" min="1" max="5" required>
        </div>

        <div class="col-md-1 d-flex align-items-center pt-4">
            <div class="form-check">
                <input class="form-check-input" type="checkbox" id="activeCheck" name="active" checked>
                <label class="form-check-label small fw-bold" for="activeCheck">Activa</label>
            </div>
        </div>

        <div class="col-md-1 d-flex align-items-end">
            <button class="btn btn-success w-100 fw-bold" type="submit">
                <i class="fa-solid fa-floppy-disk"></i>
            </button>
        </div>
    </form>
</div>

<!-- Tabla de Carreras Existentes -->
<div class="card shadow-sm overflow-hidden">
    <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
                <tr>
                    <th>Nombre de la Carrera</th>
                    <th>Código</th>
                    <th>Duración Oficial</th>
                    <th>Estado</th>
                    <th style="width: 140px;" class="text-end">Acción</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="c" items="${careers}">
                    <tr>
                        <td>
                            <strong><c:out value="${c.name}"/></strong>
                            <small class="text-muted d-block"><c:out value="${c.description}"/></small>
                        </td>
                        <td><code><c:out value="${c.code}"/></code></td>
                        <td><c:out value="${c.durationYears}"/> años</td>
                        <td>
                            <c:choose>
                                <c:when test="${c.active}">
                                    <span class="badge bg-success">Activa</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary">Inactiva</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-end">
                            <c:if test="${c.active}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/careers" class="d-inline confirm-action">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id" value="${c.careerId}">
                                    <button class="btn btn-sm btn-outline-danger" type="submit">
                                        <i class="fa-solid fa-power-off me-1"></i> Desactivar
                                    </button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty careers}">
                    <tr>
                        <td colspan="5" class="text-center py-4 text-muted">
                            No hay carreras registradas en el catálogo.
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="../common/footer.jspf" %>
