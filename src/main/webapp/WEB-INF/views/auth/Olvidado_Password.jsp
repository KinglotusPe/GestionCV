<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="../common/header.jsp" %>
<section class="portal-panel"><h1>Recuperar contraseña</h1><p>Solicite al administrador institucional un enlace de recuperación. Antes de generarlo, verificará su identidad.</p><p>El enlace caduca en 30 minutos y solo puede utilizarse una vez. Si ya tiene uno, ábralo para elegir su nueva contraseña.</p><a class="btn btn-success" href="${pageContext.request.contextPath}/contact">Contacto institucional</a> <a href="${pageContext.request.contextPath}/login">Volver al inicio de sesión</a></section>
<%@ include file="../common/footer.jsp" %>
