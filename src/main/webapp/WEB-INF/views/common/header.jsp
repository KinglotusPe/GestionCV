<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="hp" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${pageTitle}" default="CVManager - IESP Manuel Antonio Hierro Pozo"/></title>

    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome Icons -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.dataTables.min.css" rel="stylesheet">
    <!-- Estilos Propios Institucionales -->
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/assets/css/admin.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/assets/css/cv-Plantillas.css" rel="stylesheet">
</head>
<body class="${not empty sessionScope.userId ? 'portal-body' : ''}" data-context="${pageContext.request.contextPath}">

<%@ include file="sidebar.jsp" %>
<%@ include file="nav.jsp" %>

<!-- Contenedor Principal de la Página -->
<main class="container py-4">

    <!-- Mensajes Globales de Alerta -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fa-solid fa-triangle-exclamation me-2"></i>
            <c:out value="${error}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:if test="${not empty success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fa-solid fa-circle-check me-2"></i>
            <c:out value="${success}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:if test="${param.ok == '1'}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fa-solid fa-circle-check me-2"></i>
            Operación realizada correctamente.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
