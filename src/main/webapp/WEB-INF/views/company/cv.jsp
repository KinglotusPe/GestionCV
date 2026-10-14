<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jsp" %>

<div class="row">
    <!-- Columna Principal: Currículum Vítae Completo -->
    <div class="col-lg-8 mb-4">
        <div class="card p-4 p-md-5 shadow-sm cv-document">
            <div class="d-flex align-items-center gap-3 pb-3 border-bottom mb-4">
                <div class="bg-light p-3 rounded-circle text-success">
                    <c:choose><c:when test="${not empty graduate.photoUrl}"><img class="rounded-circle" width="80" height="80" style="object-fit:cover" alt="Foto del candidato" src="${pageContext.request.contextPath}/files?type=photo&amp;cvId=${cv.cvId}"></c:when><c:otherwise><img src="${pageContext.request.contextPath}/assets/images/default-avatar.png" width="80" height="80" alt="Avatar predeterminado"></c:otherwise></c:choose>
                </div>
                <div>
                    <h2 class="fw-bold mb-1"><c:out value="${graduate.fullName}"/></h2>
                    <p class="text-muted mb-0">
                        <i class="fa-solid fa-graduation-cap me-1"></i> <c:out value="${graduate.careerName}"/> · 
                        <i class="fa-solid fa-location-dot me-1"></i> <c:out value="${graduate.city}"/>, <c:out value="${graduate.country}"/>
                    </p>
                </div>
            </div>

            <!-- Titular y Extracto -->
            <div class="mb-4">
                <h4 class="cv-section"><c:out value="${cv.title}"/></h4>
                <p class="text-secondary lead fs-6"><c:out value="${cv.professionalSummary}"/></p>
            </div>

            <!-- Formación Académica -->
            <div class="mb-4">
                <h5 class="cv-section"><i class="fa-solid fa-book-open me-2"></i> Formación Académica</h5>
                <c:choose>
                    <c:when test="${not empty education}">
                        <div class="list-group list-group-flush">
                            <c:forEach var="item" items="${education}">
                                <div class="list-group-item px-0">
                                    <h6 class="mb-1 fw-bold"><c:out value="${item.degree}"/></h6>
                                    <p class="mb-0 text-muted small"><c:out value="${item.institution}"/> | <c:out value="${item.field_of_study}"/></p>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="small text-muted">No se ha registrado información de educación.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Experiencia Laboral -->
            <div class="mb-4">
                <h5 class="cv-section"><i class="fa-solid fa-briefcase me-2"></i> Experiencia Laboral</h5>
                <c:choose>
                    <c:when test="${not empty experience}">
                        <div class="list-group list-group-flush">
                            <c:forEach var="item" items="${experience}">
                                <div class="list-group-item px-0">
                                    <h6 class="mb-1 fw-bold"><c:out value="${item.position}"/></h6>
                                    <p class="mb-0 text-muted small"><c:out value="${item.company_name}"/> — <c:out value="${item.responsibilities}"/></p>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="small text-muted">No se ha registrado experiencia previa.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Habilidades -->
            <div class="mb-4">
                <h5 class="cv-section"><i class="fa-solid fa-screwdriver-wrench me-2"></i> Habilidades</h5>
                <div class="d-flex flex-wrap gap-2">
                    <c:forEach var="item" items="${skills}">
                        <span class="badge badge-soft px-3 py-2">
                            <c:out value="${item.skill_name}"/> (${item.proficiency_level}/5)
                        </span>
                    </c:forEach>
                </div>
            </div>

            <!-- Idiomas -->
            <div class="mb-4">
                <h5 class="cv-section"><i class="fa-solid fa-language me-2"></i> Idiomas</h5>
                <div class="d-flex flex-wrap gap-3">
                    <c:forEach var="item" items="${languages}">
                        <span class="text-secondary small">
                            <strong><c:out value="${item.language_name}"/>:</strong> <c:out value="${item.proficiency_level}"/>
                        </span>
                    </c:forEach>
                </div>
            </div>

            <!-- Certificaciones -->
            <c:if test="${not empty certifications}">
                <div class="mb-4">
                    <h5 class="cv-section"><i class="fa-solid fa-certificate me-2"></i> Certificaciones</h5>
                    <ul class="list-unstyled mb-0">
                        <c:forEach var="item" items="${certifications}">
                            <li class="mb-2 small">
                                <strong><c:out value="${item.name}"/></strong> — <span class="text-muted"><c:out value="${item.issuing_organization}"/></span>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>

            <c:if test="${not empty cv.cvPdfUrl}"><c:choose><c:when test="${contactAccepted}"><a class="btn btn-outline-primary mb-3" href="${pageContext.request.contextPath}/files?type=pdf&amp;cvId=${cv.cvId}">Descargar CV en PDF</a></c:when><c:otherwise><p class="text-muted">El PDF estará disponible cuando el candidato acepte su solicitud.</p></c:otherwise></c:choose></c:if>
            <!-- Referencias Profesionales -->
            <c:if test="${not empty references and contactAccepted}">
                <div class="mb-4">
                    <h5 class="cv-section"><i class="fa-solid fa-user-check me-2"></i> Referencias Profesionales</h5>
                    <div class="row g-2">
                        <c:forEach var="item" items="${references}">
                            <div class="col-md-6">
                                <div class="p-3 bg-light rounded border">
                                    <strong><c:out value="${item.full_name}"/></strong>
                                    <div class="small text-muted"><c:out value="${item.position}"/> (<c:out value="${item.company_name}"/>)</div>
                                    <div class="small text-secondary mt-1">
                                        <i class="fa-solid fa-phone me-1"></i> <c:out value="${item.phone}"/><br>
                                        <i class="fa-solid fa-envelope me-1"></i> <c:out value="${item.email}"/>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
        </div>
    </div>

    <!-- Columna Lateral: Acciones y Formulario de Contacto -->
    <div class="col-lg-4 mb-4">
        <div class="card p-4 shadow-sm sticky-actions">
            <h5 class="fw-bold mb-3 text-success">
                <i class="fa-solid fa-envelope me-1"></i> Contactar al Candidato
            </h5>
            <p class="small text-muted mb-3">
                Envíe una propuesta laboral o solicitud de entrevista a este egresado.
            </p>

            <form method="post" action="${pageContext.request.contextPath}/company/action">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="contact">
                <input type="hidden" name="graduateId" value="${graduate.graduateId}">
                
                <div class="mb-3">
                    <label class="form-label small fw-bold">Mensaje de propuesta</label>
                    <textarea class="form-control" name="message" rows="4" placeholder="Estimado/a <c:out value='${graduate.firstName}'/>, nos interesa su perfil técnico para..." required></textarea>
                </div>

                <button class="btn btn-success w-100 fw-bold" type="submit">
                    <i class="fa-solid fa-paper-plane me-1"></i> Enviar Solicitud
                </button>
            </form>

            <hr>

            <div class="small text-muted">
                <i class="fa-solid fa-chart-line me-1"></i> Visualizaciones acumuladas del CV: <strong>${cv.viewsCount}</strong>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
