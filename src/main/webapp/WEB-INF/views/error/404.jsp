<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jsp" %>

<div class="row justify-content-center py-5 text-center">
    <div class="col-md-6">
        <div class="p-5 bg-white rounded-4 shadow-sm border-top border-4 border-warning">
            <div class="text-warning mb-3">
                <i class="fa-solid fa-triangle-exclamation fa-4x"></i>
            </div>
            <h1 class="display-5 fw-bold text-dark mb-2">404</h1>
            <h4 class="fw-bold mb-3">Página No Encontrada</h4>
            <p class="text-muted mb-4">
                El recurso o enlace solicitado no existe en la plataforma CVManager.
            </p>
            <a class="btn btn-warning text-dark px-4 fw-bold" href="${pageContext.request.contextPath}/home">
                <i class="fa-solid fa-house me-1"></i> Regresar al Inicio
            </a>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>