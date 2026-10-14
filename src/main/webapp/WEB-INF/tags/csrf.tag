<%@ tag pageEncoding="UTF-8" body-content="empty" description="Campo de protección CSRF de formularios." %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrfToken}'/>">
