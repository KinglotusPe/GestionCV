<%-- Redirección inicial hacia el controlador institucional Home --%>
<%
    response.sendRedirect(request.getContextPath() + "/home");
%>