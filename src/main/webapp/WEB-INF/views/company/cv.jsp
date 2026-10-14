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

            <c:if test="${contactAccepted}">
                <div class="alert alert-success border-2 border-success p-3 rounded mb-4 shadow-sm">
                    <div class="d-flex align-items-center gap-2 mb-2">
                        <i class="fa-solid fa-circle-check fa-lg text-success"></i>
                        <strong class="text-success fs-6">Contacto Directo Aprobado por el Candidato</strong>
                    </div>
                    <div class="row g-2 text-dark small">
                        <c:if test="${not empty graduate.phone}">
                            <div class="col-sm-6">
                                <strong>Teléfono:</strong> 
                                <a href="tel:${graduate.phone}" class="fw-bold text-decoration-none text-dark ms-1"><i class="fa-solid fa-phone text-success me-1"></i> <c:out value="${graduate.phone}"/></a>
                                <a href="https://wa.me/51${graduate.phone}" target="_blank" class="btn btn-sm btn-success ms-2 py-0 px-2 small">
                                    <i class="fa-brands fa-whatsapp"></i> WhatsApp
                                </a>
                            </div>
                        </c:if>
                        <c:if test="${not empty graduateEmail}">
                            <div class="col-sm-6">
                                <strong>Correo:</strong> 
                                <a href="mailto:${graduateEmail}" class="fw-bold text-decoration-none text-dark ms-1"><i class="fa-solid fa-envelope text-primary me-1"></i> <c:out value="${graduateEmail}"/></a>
                            </div>
                        </c:if>
                        <c:if test="${not empty graduate.linkedinUrl}">
                            <div class="col-sm-6">
                                <strong>LinkedIn:</strong> 
                                <a href="<c:out value='${graduate.linkedinUrl}'/>" target="_blank" class="text-decoration-none ms-1"><i class="fa-brands fa-linkedin text-primary me-1"></i> Ver perfil</a>
                            </div>
                        </c:if>
                        <c:if test="${not empty graduate.portfolioUrl}">
                            <div class="col-sm-6">
                                <strong>Portafolio:</strong> 
                                <a href="<c:out value='${graduate.portfolioUrl}'/>" target="_blank" class="text-decoration-none ms-1"><i class="fa-solid fa-globe text-secondary me-1"></i> Sitio web</a>
                            </div>
                        </c:if>
                    </div>
                </div>
            </c:if>

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
            <c:choose>
                <c:when test="${contactAccepted}">
                    <div class="text-center mb-3">
                        <div class="p-3 bg-success-subtle rounded-circle d-inline-block text-success mb-2">
                            <i class="fa-solid fa-circle-check fa-2x"></i>
                        </div>
                        <h5 class="fw-bold text-success mb-1">Contacto Autorizado</h5>
                        <p class="small text-muted mb-0">El egresado ha aceptado compartir sus datos de contacto directo con su empresa.</p>
                    </div>

                    <div class="card bg-light p-3 border mb-3">
                        <h6 class="fw-bold text-dark mb-2"><i class="fa-solid fa-address-book text-success me-1"></i> Canales de Comunicación</h6>
                        <c:if test="${not empty graduate.phone}">
                            <p class="mb-2 small">
                                <strong>Teléfono:</strong> <a href="tel:${graduate.phone}" class="fw-bold text-decoration-none text-dark ms-1"><c:out value="${graduate.phone}"/></a>
                            </p>
                            <a href="https://wa.me/51${graduate.phone}" target="_blank" class="btn btn-success btn-sm w-100 fw-bold mb-2">
                                <i class="fa-brands fa-whatsapp me-1"></i> Abrir Chat en WhatsApp
                            </a>
                        </c:if>
                        <c:if test="${not empty graduateEmail}">
                            <p class="mb-2 small">
                                <strong>Correo:</strong> <a href="mailto:${graduateEmail}" class="fw-bold text-decoration-none text-dark ms-1"><c:out value="${graduateEmail}"/></a>
                            </p>
                            <a href="mailto:${graduateEmail}" class="btn btn-outline-primary btn-sm w-100 fw-bold mb-1">
                                <i class="fa-solid fa-envelope me-1"></i> Enviar Correo
                            </a>
                        </c:if>
                    </div>
                </c:when>

                <c:when test="${contactStatus == 'pending'}">
                    <div class="text-center mb-3">
                        <div class="p-3 bg-warning-subtle rounded-circle d-inline-block text-warning mb-2">
                            <i class="fa-solid fa-clock fa-2x"></i>
                        </div>
                        <h5 class="fw-bold text-dark mb-1">Solicitud Enviada</h5>
                        <p class="small text-muted mb-2">Ya enviaste una solicitud de contacto a este candidato. En cuanto la acepte, sus teléfonos y correos aparecerán aquí y en tu lista de contactos.</p>
                    </div>
                    <a href="${pageContext.request.contextPath}/company/favorites#tab-req" class="btn btn-outline-secondary btn-sm w-100 mb-2">
                        <i class="fa-solid fa-list-check me-1"></i> Ver Estado en Mis Solicitudes
                    </a>
                </c:when>

                <c:otherwise>
                    <h5 class="fw-bold mb-3 text-success">
                        <i class="fa-solid fa-envelope me-1"></i> Solicitar Contacto
                    </h5>
                    <p class="small text-muted mb-3">
                        Envíe una propuesta laboral para que el candidato le comparta su teléfono y correo directo.
                    </p>

                    <form method="post" action="${pageContext.request.contextPath}/company/action">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="action" value="contact">
                        <input type="hidden" name="graduateId" value="${graduate.graduateId}">
                        <input type="hidden" name="cvId" value="${cv.cvId}">
                        
                        <div class="mb-3">
                            <label class="form-label small fw-bold">Mensaje de propuesta</label>
                            <textarea class="form-control" name="message" rows="4" placeholder="Estimado/a <c:out value='${graduate.firstName}'/>, nos interesa su perfil técnico para..." required></textarea>
                        </div>

                        <button class="btn btn-success w-100 fw-bold" type="submit">
                            <i class="fa-solid fa-paper-plane me-1"></i> Enviar Solicitud de Contacto
                        </button>
                    </form>
                </c:otherwise>
            </c:choose>

            <hr>

            <form method="post" action="${pageContext.request.contextPath}/company/action" class="mb-2">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="favorite">
                <input type="hidden" name="cvId" value="${cv.cvId}">
                <button class="btn btn-outline-warning text-dark w-100 btn-sm fw-bold" type="submit">
                    <i class="fa-solid fa-star me-1 text-warning"></i> Guardar en Favoritos
                </button>
            </form>

            <div class="small text-muted mt-2">
                <i class="fa-solid fa-chart-line me-1"></i> Visualizaciones acumuladas del CV: <strong>${cv.viewsCount}</strong>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
