<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jsp" %>

<div class="row">
    <!-- Columna Izquierda: Información de Perfil -->
    <div class="col-lg-8 mb-4">
        <div class="card p-4 shadow-sm">
            <h3 class="section-title mb-3">
                <i class="fa-solid fa-user-pen me-2"></i> Datos Personales y Profesionales
            </h3>

            <%@ include file="editarPerfil.jsp" %>
        </div>
    </div>

    <!-- Columna Derecha: Subida de Foto de Perfil -->
    <div class="col-lg-4 mb-4">
        <div class="card p-4 shadow-sm text-center">
            <h4 class="section-title mb-3">
                <i class="fa-solid fa-camera me-1"></i> Foto de Perfil
            </h4>

            <div class="mb-3">
                <c:choose>
                    <c:when test="${not empty graduate.photoUrl}">
                        <div class="alert alert-info py-2 small">
                            <i class="fa-solid fa-check me-1"></i> Foto registrada: <br>
                            <img class="rounded img-fluid" style="max-height:220px" alt="Foto de perfil" src="${pageContext.request.contextPath}/files?type=photo&amp;cvId=${cv.cvId}">
                            <form method="post" action="${pageContext.request.contextPath}/graduate/upload">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"><input type="hidden" name="type" value="photo"><button class="btn btn-outline-danger btn-sm mt-2" name="action" value="remove">Eliminar foto</button></form>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="p-4 bg-light rounded-circle d-inline-block mb-2 text-secondary">
                            <img src="${pageContext.request.contextPath}/assets/images/default-avatar.png" width="96" height="96" alt="Avatar predeterminado">
                        </div>
                        <p class="small text-muted">Aún no ha cargado una fotografía de perfil.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <form method="post" action="${pageContext.request.contextPath}/graduate/upload" enctype="multipart/form-data">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="type" value="photo">
                <div class="mb-3 text-start">
                    <label class="form-label small fw-bold">Seleccionar imagen (JPG o PNG):</label>
                    <input class="form-control form-control-sm" type="file" name="file" accept="image/png, image/jpeg" required>
                </div>
                <button class="btn btn-outline-success btn-sm w-100 fw-bold" type="submit">
                    <i class="fa-solid fa-upload me-1"></i> Subir Fotografía
                </button>
            </form>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
