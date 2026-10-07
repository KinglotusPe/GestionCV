<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jspf" %>

<div class="mb-4">
    <h2 class="section-title mb-1">
        <i class="fa-solid fa-clock-rotate-left me-2"></i> Bitácora de Auditoría del Sistema
    </h2>
    <p class="text-muted small">Trazabilidad de eventos críticos, accesos y operaciones registradas en el servidor</p>
</div>

<div class="card shadow-sm overflow-hidden mb-4">
    <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
                <tr>
                    <th style="width: 80px;">Log ID</th>
                    <th>Acción</th>
                    <th>Usuario Responsable</th>
                    <th>Entidad Afectada</th>
                    <th>Detalles de la Operación</th>
                    <th>Dirección IP</th>
                    <th>Fecha y Hora</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="l" items="${logs}">
                    <tr>
                        <td><code>#${l.log_id}</code></td>
                        <td>
                            <span class="badge bg-secondary font-monospace"><c:out value="${l.action}"/></span>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty l.email}">
                                    <strong><c:out value="${l.email}"/></strong>
                                    <small class="text-muted d-block">(<c:out value="${l.role}"/>)</small>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted fst-italic">Sistema / Anónimo</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <span class="badge bg-light text-dark border">
                                <c:out value="${l.entity_type}"/> <c:if test="${not empty l.entity_id}">#${l.entity_id}</c:if>
                            </span>
                        </td>
                        <td class="small text-muted"><c:out value="${l.details}"/></td>
                        <td class="small"><code><c:out value="${l.ip_address}"/></code></td>
                        <td class="small text-muted">${l.created_at}</td>
                    </tr>
                </c:forEach>

                <c:if test="${empty logs}">
                    <tr>
                        <td colspan="7" class="text-center py-4 text-muted">
                            No existen registros de auditoría registrados en este momento.
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="../common/footer.jspf" %>
