<%@ tag pageEncoding="UTF-8" body-content="empty" description="Estado traducido y escapado, reutilizable en listados." %>
<%@ attribute name="value" required="true" type="java.lang.String" %>
<%@ attribute name="label" required="false" type="java.lang.String" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tone" value="finished"/>
<c:set var="text" value="${value}"/>
<c:choose>
 <c:when test="${value == 'PUBLICADA' or value == 'ongoing' or value == 'active' or value == 'open'}">
  <c:set var="tone" value="ongoing"/><c:set var="text" value="PUBLICADA"/>
 </c:when>
 <c:when test="${value == 'BORRADOR'}">
  <c:set var="tone" value="archived"/><c:set var="text" value="BORRADOR"/>
 </c:when>
 <c:when test="${value == 'PAUSADA'}">
  <c:set var="tone" value="upcoming"/><c:set var="text" value="PAUSADA"/>
 </c:when>
 <c:when test="${value == 'CERRADA' or value == 'closed' or value == 'finished'}">
  <c:set var="tone" value="finished"/><c:set var="text" value="CERRADA"/>
 </c:when>
 <c:when test="${value == 'VENCIDA'}">
  <c:set var="tone" value="rejected"/><c:set var="text" value="VENCIDA"/>
 </c:when>
 <c:when test="${value == 'POSTULADO' or value == 'pending'}">
  <c:set var="tone" value="upcoming"/><c:set var="text" value="POSTULADO"/>
 </c:when>
 <c:when test="${value == 'EN_REVISION' or value == 'reviewing'}">
  <c:set var="tone" value="upcoming"/><c:set var="text" value="EN EVALUACIÓN"/>
 </c:when>
 <c:when test="${value == 'PRESELECCIONADO'}">
  <c:set var="tone" value="ongoing"/><c:set var="text" value="PRESELECCIONADO"/>
 </c:when>
 <c:when test="${value == 'ENTREVISTA'}">
  <c:set var="tone" value="ongoing"/><c:set var="text" value="ENTREVISTA"/>
 </c:when>
 <c:when test="${value == 'SELECCIONADO' or value == 'accepted'}">
  <c:set var="tone" value="ongoing"/><c:set var="text" value="SELECCIONADO"/>
 </c:when>
 <c:when test="${value == 'NO_SELECCIONADO' or value == 'rejected'}">
  <c:set var="tone" value="rejected"/><c:set var="text" value="NO SELECCIONADO"/>
 </c:when>
 <c:when test="${value == 'RETIRADO' or value == 'withdrawn'}">
  <c:set var="tone" value="archived"/><c:set var="text" value="RETIRADO"/>
 </c:when>
 <c:when test="${value == 'archived'}">
  <c:set var="tone" value="archived"/><c:set var="text" value="Archivada"/>
 </c:when>
 <c:when test="${value == 'suspended'}">
  <c:set var="tone" value="rejected"/><c:set var="text" value="Suspendida"/>
 </c:when>
</c:choose>
<span class="stage-badge stage-${tone}"><c:out value="${empty label ? text : label}"/></span>
