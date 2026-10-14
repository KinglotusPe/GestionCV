<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jsp" %>

<div class="row justify-content-center py-5 text-center">
    <div class="col-md-6">
        <div class="p-5 bg-white rounded-4 shadow-sm border-top border-4 border-danger">
            <div class="text-danger mb-3">
                <i class="fa-solid fa-ban fa-4x"></i>
            </div>
            <h1 class="display-5 fw-bold text-danger mb-2">403</h1>
            <h4 class="fw-bold mb-3">Acceso Denegado</h4>
            <p class="text-muted mb-4">
                No cuenta con los privilegios o el rol necesario para acceder a este recurso del sistema.
            </p>
            <a class="btn btn-outline-danger px-4 fw-bold" href="${pageContext.request.contextPath}/home">
                <i class="fa-solid fa-house me-1"></i> Regresar al Inicio
            </a>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>