<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jsp" %>

<div class="row justify-content-center py-5 text-center">
    <div class="col-md-7">
        <div class="p-5 bg-white rounded-4 shadow-sm border-top border-4 border-secondary">
            <div class="text-secondary mb-3">
                <i class="fa-solid fa-server fa-4x"></i>
            </div>
            <h1 class="display-5 fw-bold text-dark mb-2">500</h1>
            <h4 class="fw-bold mb-3">Error Interno del Servidor</h4>
            <p class="text-muted mb-4">
                Ocurrió un inconveniente al procesar su solicitud en el servidor Tomcat. Si el problema persiste, contacte con el soporte técnico de la institución.
            </p>
            <a class="btn btn-secondary px-4 fw-bold" href="${pageContext.request.contextPath}/home">
                <i class="fa-solid fa-house me-1"></i> Ir a la Portada
            </a>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>