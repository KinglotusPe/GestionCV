<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jspf" %>

<div class="mb-4">
    <h2 class="section-title mb-1">
        <i class="fa-solid fa-chart-pie me-2"></i> Panel de Control Administrativo
    </h2>
    <p class="text-muted small">Indicadores generales y estadísticas de empleabilidad institucional</p>
</div>

<!-- Tarjetas de Métricas Estadísticas -->
<div class="row g-3 mb-4">
    <div class="col-sm-6 col-lg-3">
        <div class="card p-3 shadow-sm border-start border-4 border-success">
            <div class="text-muted small text-uppercase fw-bold">Egresados Registrados</div>
            <div class="stat mt-2">${stats.graduates}</div>
        </div>
    </div>
    <div class="col-sm-6 col-lg-3">
        <div class="card p-3 shadow-sm border-start border-4 border-primary">
            <div class="text-muted small text-uppercase fw-bold">Empresas Aliadas</div>
            <div class="stat mt-2 text-primary">${stats.companies}</div>
        </div>
    </div>
    <div class="col-sm-6 col-lg-3">
        <div class="card p-3 shadow-sm border-start border-4 border-warning">
            <div class="text-muted small text-uppercase fw-bold">Currículums Publicados</div>
            <div class="stat mt-2 text-warning">${stats.published}</div>
        </div>
    </div>
    <div class="col-sm-6 col-lg-3">
        <div class="card p-3 shadow-sm border-start border-4 border-danger">
            <div class="text-muted small text-uppercase fw-bold">Cuentas Pendientes</div>
            <div class="stat mt-2 text-danger">${stats.pending}</div>
        </div>
    </div>
</div>

<!-- Gráfico de Egresados por Programa de Estudios -->
<div class="card p-4 shadow-sm mb-4">
    <h4 class="section-title mb-3">
        <i class="fa-solid fa-chart-column me-2"></i> Egresados por Programa de Estudios
    </h4>
    <div style="position: relative; height: 320px;">
        <canvas id="careerChart"></canvas>
    </div>
</div>

<!-- Script Chart.js -->
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        const ctx = document.getElementById('careerChart').getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: [
                    <c:forEach var="item" items="${byCareer}" varStatus="loop">
                        '<c:out value="${item.name}"/>'${!loop.last ? ',' : ''}
                    </c:forEach>
                ],
                datasets: [{
                    label: 'Total de Egresados',
                    data: [
                        <c:forEach var="item" items="${byCareer}" varStatus="loop">
                            ${item.total}${!loop.last ? ',' : ''}
                        </c:forEach>
                    ],
                    backgroundColor: [
                        'rgba(11, 61, 46, 0.8)',
                        'rgba(22, 101, 52, 0.8)',
                        'rgba(212, 160, 23, 0.8)',
                        'rgba(30, 64, 175, 0.8)'
                    ],
                    borderColor: [
                        '#0b3d2e',
                        '#166534',
                        '#d4a017',
                        '#1e40af'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            precision: 0
                        }
                    }
                }
            }
        });
    });
</script>

<%@ include file="../common/footer.jspf" %>
