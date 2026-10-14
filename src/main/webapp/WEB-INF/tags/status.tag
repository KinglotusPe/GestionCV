<%@ tag pageEncoding="UTF-8" body-content="empty" description="Estado traducido y escapado, reutilizable en listados." %>
<%@ attribute name="value" required="true" type="java.lang.String" %>
<%@ attribute name="label" required="false" type="java.lang.String" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tone" value="finished"/>
<c:set var="text" value="${value}"/>
<c:choose>
 <c:when test="${value == 'ongoing' or value == 'active' or value == 'accepted'}"><c:set var="tone" value="ongoing"/><c:set var="text" value="${value == 'ongoing' ? 'En proceso' : value == 'active' ? 'Activa' : 'Aceptada'}"/></c:when>
 <c:when test="${value == 'upcoming' or value == 'pending' or value == 'reviewing'}"><c:set var="tone" value="upcoming"/><c:set var="text" value="${value == 'upcoming' ? 'Por comenzar' : value == 'pending' ? 'Pendiente' : 'En evaluación'}"/></c:when>
 <c:when test="${value == 'archived'}"><c:set var="tone" value="archived"/><c:set var="text" value="Archivada"/></c:when>
 <c:when test="${value == 'finished'}"><c:set var="text" value="Finalizado"/></c:when>
 <c:when test="${value == 'suspended'}"><c:set var="text" value="Suspendida"/></c:when>
 <c:when test="${value == 'inactive'}"><c:set var="text" value="Inactiva / rechazada"/></c:when>
 <c:when test="${value == 'rejected'}"><c:set var="text" value="Rechazada"/></c:when>
 <c:when test="${value == 'withdrawn'}"><c:set var="text" value="Retirada"/></c:when>
</c:choose>
<span class="stage-badge stage-${tone}"><c:out value="${empty label ? text : label}"/></span>
