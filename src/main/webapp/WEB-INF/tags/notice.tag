<%@ tag pageEncoding="UTF-8" body-content="empty" description="Mensaje accesible con tipo y texto parametrizados." %>
<%@ attribute name="message" required="true" type="java.lang.String" %>
<%@ attribute name="error" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div class="alert ${error ? 'alert-danger' : 'alert-success'}" role="${error ? 'alert' : 'status'}"><c:out value="${message}"/></div>
