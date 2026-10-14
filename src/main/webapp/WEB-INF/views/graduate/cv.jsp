<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jsp" %>

<!-- Cabecera del CV y Estado de Publicación -->
<div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
    <div>
        <h2 class="section-title mb-1">
            <i class="fa-solid fa-file-invoice me-2"></i> Mi Currículum Vitae
        </h2>
        <p class="text-muted small mb-0">Gestione la información profesional que verán las empresas contratantes</p>
    </div>
    <div>
        <form method="post" action="${pageContext.request.contextPath}/graduate/cv" class="d-inline">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <c:choose>
                <c:when test="${cv.published}">
                    <button type="submit" name="action" value="unpublish" class="btn btn-outline-danger fw-bold">
                        <i class="fa-solid fa-eye-slash me-1"></i> Despublicar CV
                    </button>
                    <span class="badge bg-success ms-2 py-2 px-3">
                        <i class="fa-solid fa-globe me-1"></i> Publicado (${cv.viewsCount} visualizaciones)
                    </span>
                </c:when>
                <c:otherwise>
                    <button type="submit" name="action" value="publish" class="btn btn-success fw-bold">
                        <i class="fa-solid fa-bullhorn me-1"></i> Publicar mi CV
                    </button>
                    <span class="badge bg-secondary ms-2 py-2 px-3">
                        <i class="fa-solid fa-lock me-1"></i> Borrador Privado
                    </span>
                </c:otherwise>
            </c:choose>
        </form>
    </div>
</div>

<!-- Tarjeta Principal: Título, Resumen Profesional y Subida de PDF -->
<div class="card p-4 shadow-sm mb-4">
    <h4 class="section-title mb-3">
        <i class="fa-solid fa-user-tie me-2"></i> Título y Resumen Profesional
    </h4>

    <form method="post" action="${pageContext.request.contextPath}/graduate/cv" class="mb-4">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <input type="hidden" name="action" value="main">
        
        <div class="mb-3">
            <label class="form-label fw-bold">Titular Profesional del CV</label>
            <input class="form-control" type="text" name="title" value="<c:out value='${cv.title}'/>" placeholder="Ej: Técnico en Redes y Telecomunicaciones | Soporte TI" required>
        </div>

        <div class="mb-3">
            <label class="form-label fw-bold">Extracto / Perfil Profesional</label>
            <textarea class="form-control" name="summary" rows="4" placeholder="Describa brevemente sus competencias técnicas, experiencia y objetivos profesionales..." required><c:out value="${cv.professionalSummary}"/></textarea>
        </div>

        <button class="btn btn-success fw-bold" type="submit">
            <i class="fa-solid fa-floppy-disk me-1"></i> Guardar Título y Resumen
        </button>
    </form>

    <hr>

    <!-- Subida de PDF -->
    <h5 class="fw-bold mb-3 text-secondary">
        <i class="fa-solid fa-file-pdf text-danger me-1"></i> Documento Adjunto (PDF)
    </h5>
    <c:if test="${not empty cv.cvPdfUrl}">
        <div class="alert alert-light border d-flex justify-content-between align-items-center mb-3">
            <div>
                <i class="fa-solid fa-file-pdf text-danger fa-lg me-2"></i>
                <span>Currículum adjunto cargado</span>
            </div>
            <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/files?type=pdf&amp;cvId=${cv.cvId}">Descargar PDF</a>
            <form method="post" action="${pageContext.request.contextPath}/graduate/upload">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"><input type="hidden" name="type" value="pdf"><button class="btn btn-outline-danger" name="action" value="remove">Eliminar PDF</button></form>
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/graduate/upload" enctype="multipart/form-data">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <input type="hidden" name="type" value="pdf">
        <label class="form-label small fw-bold">Adjuntar o actualizar archivo PDF:</label>
        <div class="input-group">
            <input class="form-control" type="file" name="file" accept="application/pdf" required>
            <button class="btn btn-outline-danger fw-bold" type="submit">
                <i class="fa-solid fa-upload me-1"></i> Subir Documento PDF
            </button>
        </div>
    </form>
</div>

<!-- Acordeón con Secciones Detalladas del CV -->
<div class="accordion shadow-sm mb-4" id="cvAccordion">

    <!-- 1. Formación Académica -->
    <div class="accordion-item">
        <h2 class="accordion-header">
            <button class="accordion-button fw-bold text-success" type="button" data-bs-toggle="collapse" data-bs-target="#collapseEdu">
                <i class="fa-solid fa-graduation-cap me-2"></i> 1. Formación Académica
            </button>
        </h2>
        <div id="collapseEdu" class="accordion-collapse collapse show" data-bs-parent="#cvAccordion">
            <div class="accordion-body">
                <form method="post" action="${pageContext.request.contextPath}/graduate/cv" class="row g-2 mb-3">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="action" value="addEducation">
                    <div class="col-md-4">
                        <input class="form-control" type="text" name="institution" placeholder="Institución / Universidad" required>
                    </div>
                    <div class="col-md-4">
                        <input class="form-control" type="text" name="degree" placeholder="Grado / Carrera / Especialidad" required>
                    </div>
                    <div class="col-md-4">
                        <input class="form-control" type="text" name="fieldOfStudy" placeholder="Campo de Estudio">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small text-muted mb-0">Fecha Inicio</label>
                        <input class="form-control" type="date" name="startDate">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small text-muted mb-0">Fecha Fin</label>
                        <input class="form-control" type="date" name="endDate">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label small text-muted mb-0">Descripción</label>
                        <input class="form-control" type="text" name="description" placeholder="Logros o menciones">
                    </div>
                    <div class="col-md-2 d-flex align-items-end">
                        <button class="btn btn-success w-100 fw-bold" type="submit">
                            <i class="fa-solid fa-plus me-1"></i> Agregar
                        </button>
                    </div>
                </form>

                <div class="list-group">
                    <c:forEach var="item" items="${education}">
                        <div class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="mb-0 fw-bold"><c:out value="${item.degree}"/></h6>
                                <small class="text-muted"><c:out value="${item.institution}"/> | <c:out value="${item.field_of_study}"/></small>
                            </div>
                            <form method="post" action="${pageContext.request.contextPath}/graduate/cv" class="d-inline confirm-action">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="action" value="delete">
                                <a class="btn btn-outline-primary btn-sm me-2" href="${pageContext.request.contextPath}/graduate/cv?action=edit&amp;type=education&amp;id=${item.education_id}">Editar</a>
<input type="hidden" name="type" value="education">
                                <input type="hidden" name="id" value="${item.education_id}">
                                <button class="btn btn-outline-danger btn-sm" type="submit">
                                    <i class="fa-solid fa-trash"></i>
                                </button>
                            </form>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>

    <!-- 2. Experiencia Laboral -->
    <div class="accordion-item">
        <h2 class="accordion-header">
            <button class="accordion-button collapsed fw-bold text-success" type="button" data-bs-toggle="collapse" data-bs-target="#collapseExp">
                <i class="fa-solid fa-briefcase me-2"></i> 2. Experiencia Laboral
            </button>
        </h2>
        <div id="collapseExp" class="accordion-collapse collapse" data-bs-parent="#cvAccordion">
            <div class="accordion-body">
                <form method="post" action="${pageContext.request.contextPath}/graduate/cv" class="row g-2 mb-3">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="action" value="addExperience">
                    <div class="col-md-4">
                        <input class="form-control" type="text" name="companyName" placeholder="Empresa / Organización" required>
                    </div>
                    <div class="col-md-4">
                        <input class="form-control" type="text" name="position" placeholder="Cargo desempeñado" required>
                    </div>
                    <div class="col-md-4">
                        <input class="form-control" type="text" name="responsibilities" placeholder="Responsabilidades clave">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small text-muted mb-0">Fecha Inicio</label>
                        <input class="form-control" type="date" name="startDate">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small text-muted mb-0">Fecha Fin</label>
                        <input class="form-control" type="date" name="endDate">
                    </div>
                    <div class="col-md-6 d-flex align-items-end">
                        <button class="btn btn-success fw-bold px-4" type="submit">
                            <i class="fa-solid fa-plus me-1"></i> Agregar Experiencia
                        </button>
                    </div>
                </form>

                <div class="list-group">
                    <c:forEach var="item" items="${experience}">
                        <div class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="mb-0 fw-bold"><c:out value="${item.position}"/></h6>
                                <small class="text-muted"><c:out value="${item.company_name}"/> | <c:out value="${item.responsibilities}"/></small>
                            </div>
                            <form method="post" action="${pageContext.request.contextPath}/graduate/cv" class="d-inline confirm-action">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="action" value="delete">
                                <a class="btn btn-outline-primary btn-sm me-2" href="${pageContext.request.contextPath}/graduate/cv?action=edit&amp;type=experience&amp;id=${item.experience_id}">Editar</a>
<input type="hidden" name="type" value="experience">
                                <input type="hidden" name="id" value="${item.experience_id}">
                                <button class="btn btn-outline-danger btn-sm" type="submit">
                                    <i class="fa-solid fa-trash"></i>
                                </button>
                            </form>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>

    <!-- 3. Habilidades -->
    <div class="accordion-item">
        <h2 class="accordion-header">
            <button class="accordion-button collapsed fw-bold text-success" type="button" data-bs-toggle="collapse" data-bs-target="#collapseSkills">
                <i class="fa-solid fa-screwdriver-wrench me-2"></i> 3. Habilidades Técnicas y Blandas
            </button>
        </h2>
        <div id="collapseSkills" class="accordion-collapse collapse" data-bs-parent="#cvAccordion">
            <div class="accordion-body">
                <form method="post" action="${pageContext.request.contextPath}/graduate/cv" class="row g-2 mb-3">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="action" value="addSkill">
                    <div class="col-md-5">
                        <input class="form-control" type="text" name="skillName" placeholder="Ej: Java, Redes Cisco, Diagnóstico Automotriz..." required>
                    </div>
                    <div class="col-md-3">
                        <select class="form-select" name="category">
                            <option value="technical" selected>Técnica</option>
                            <option value="soft">Blanda</option>
                            <option value="other">Otra</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <select class="form-select" name="proficiencyLevel">
                            <option value="1">Nivel 1 (Inicial)</option>
                            <option value="2">Nivel 2 (Básico)</option>
                            <option value="3" selected>Nivel 3 (Intermedio)</option>
                            <option value="4">Nivel 4 (Avanzado)</option>
                            <option value="5">Nivel 5 (Experto)</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <button class="btn btn-success w-100 fw-bold" type="submit">
                            <i class="fa-solid fa-plus me-1"></i> Agregar
                        </button>
                    </div>
                </form>

                <div class="d-flex flex-wrap gap-2 mt-3">
                    <c:forEach var="item" items="${skills}">
                        <div class="badge bg-light text-dark border p-2 d-flex align-items-center gap-2">
                            <span><strong><c:out value="${item.skill_name}"/></strong> (${item.proficiency_level}/5)</span>
                            <form method="post" action="${pageContext.request.contextPath}/graduate/cv" class="d-inline">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="action" value="delete">
                                <a class="btn btn-outline-primary btn-sm me-2" href="${pageContext.request.contextPath}/graduate/cv?action=edit&amp;type=skill&amp;id=${item.skill_id}">Editar</a>
<input type="hidden" name="type" value="skill">
                                <input type="hidden" name="id" value="${item.skill_id}">
                                <button class="btn btn-sm btn-link text-danger p-0 ms-1" type="submit" style="line-height:1;">
                                    <i class="fa-solid fa-xmark"></i>
                                </button>
                            </form>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>

    <!-- 4. Idiomas -->
    <div class="accordion-item">
        <h2 class="accordion-header">
            <button class="accordion-button collapsed fw-bold text-success" type="button" data-bs-toggle="collapse" data-bs-target="#collapseLangs">
                <i class="fa-solid fa-language me-2"></i> 4. Idiomas
            </button>
        </h2>
        <div id="collapseLangs" class="accordion-collapse collapse" data-bs-parent="#cvAccordion">
            <div class="accordion-body">
                <form method="post" action="${pageContext.request.contextPath}/graduate/cv" class="row g-2 mb-3">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="action" value="addLanguage">
                    <div class="col-md-5">
                        <input class="form-control" type="text" name="languageName" placeholder="Ej: Quechua, Inglés..." required>
                    </div>
                    <div class="col-md-4">
                        <select class="form-select" name="proficiencyLevel">
                            <option value="basic" selected>Básico</option>
                            <option value="intermediate">Intermedio</option>
                            <option value="advanced">Avanzado</option>
                            <option value="native">Nativo / Materno</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <button class="btn btn-success w-100 fw-bold" type="submit">
                            <i class="fa-solid fa-plus me-1"></i> Agregar Idioma
                        </button>
                    </div>
                </form>

                <div class="list-group">
                    <c:forEach var="item" items="${languages}">
                        <div class="list-group-item d-flex justify-content-between align-items-center">
                            <span><strong><c:out value="${item.language_name}"/></strong> — Nivel: <c:out value="${item.proficiency_level}"/></span>
                            <form method="post" action="${pageContext.request.contextPath}/graduate/cv" class="d-inline">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="action" value="delete">
                                <a class="btn btn-outline-primary btn-sm me-2" href="${pageContext.request.contextPath}/graduate/cv?action=edit&amp;type=language&amp;id=${item.language_id}">Editar</a>
<input type="hidden" name="type" value="language">
                                <input type="hidden" name="id" value="${item.language_id}">
                                <button class="btn btn-outline-danger btn-sm" type="submit">
                                    <i class="fa-solid fa-trash"></i>
                                </button>
                            </form>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>

    <!-- 5. Certificaciones -->
    <div class="accordion-item">
        <h2 class="accordion-header">
            <button class="accordion-button collapsed fw-bold text-success" type="button" data-bs-toggle="collapse" data-bs-target="#collapseCerts">
                <i class="fa-solid fa-certificate me-2"></i> 5. Certificaciones y Cursos
            </button>
        </h2>
        <div id="collapseCerts" class="accordion-collapse collapse" data-bs-parent="#cvAccordion">
            <div class="accordion-body">
                <form method="post" action="${pageContext.request.contextPath}/graduate/cv" class="row g-2 mb-3">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="action" value="addCertification">
                    <div class="col-md-5">
                        <input class="form-control" type="text" name="name" placeholder="Nombre del certificado / curso" required>
                    </div>
                    <div class="col-md-4">
                        <input class="form-control" type="text" name="organization" placeholder="Entidad emisora / Institución"><label class="form-label">Fecha de emisión</label><input class="form-control" type="date" name="issueDate">
                    </div>
                    <div class="col-md-3">
                        <button class="btn btn-success w-100 fw-bold" type="submit">
                            <i class="fa-solid fa-plus me-1"></i> Agregar Certificación
                        </button>
                    </div>
                </form>

                <div class="list-group">
                    <c:forEach var="item" items="${certificaciones}">
                        <div class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                <strong><c:out value="${item.name}"/></strong>
                                <small class="text-muted d-block"><c:out value="${item.issuing_organization}"/></small>
                            </div>
                            <form method="post" action="${pageContext.request.contextPath}/graduate/cv" class="d-inline">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="action" value="delete">
                                <a class="btn btn-outline-primary btn-sm me-2" href="${pageContext.request.contextPath}/graduate/cv?action=edit&amp;type=certification&amp;id=${item.certification_id}">Editar</a>
<input type="hidden" name="type" value="certification">
                                <input type="hidden" name="id" value="${item.certification_id}">
                                <button class="btn btn-outline-danger btn-sm" type="submit">
                                    <i class="fa-solid fa-trash"></i>
                                </button>
                            </form>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>

    <!-- 6. Referencias Profesionales -->
    <div class="accordion-item">
        <h2 class="accordion-header">
            <button class="accordion-button collapsed fw-bold text-success" type="button" data-bs-toggle="collapse" data-bs-target="#collapseRefs">
                <i class="fa-solid fa-user-check me-2"></i> 6. Referencias Profesionales
            </button>
        </h2>
        <div id="collapseRefs" class="accordion-collapse collapse" data-bs-parent="#cvAccordion">
            <div class="accordion-body">
                <form method="post" action="${pageContext.request.contextPath}/graduate/cv" class="row g-2 mb-3">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="action" value="addReference">
                    <div class="col-md-4">
                        <input class="form-control" type="text" name="fullName" placeholder="Nombre completo de la referencia" required>
                    </div>
                    <div class="col-md-4">
                        <input class="form-control" type="text" name="position" placeholder="Cargo / Puesto">
                    </div>
                    <div class="col-md-4">
                        <input class="form-control" type="text" name="companyName" placeholder="Empresa / Institución">
                    </div>
                    <div class="col-md-5">
                        <input class="form-control" type="text" name="phone" placeholder="Teléfono / Celular">
                    </div>
                    <div class="col-md-5">
                        <input class="form-control" type="email" name="email" placeholder="Correo electrónico">
                    </div>
                    <div class="col-md-2">
                        <button class="btn btn-success w-100 fw-bold" type="submit">
                            <i class="fa-solid fa-plus me-1"></i> Agregar
                        </button>
                    </div>
                </form>

                <div class="list-group">
                    <c:forEach var="item" items="${references}">
                        <div class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                <strong><c:out value="${item.full_name}"/></strong> — <span class="text-muted"><c:out value="${item.position}"/> (<c:out value="${item.company_name}"/>)</span>
                                <small class="text-secondary d-block"><i class="fa-solid fa-phone me-1"></i> <c:out value="${item.phone}"/> | <i class="fa-solid fa-envelope me-1"></i> <c:out value="${item.email}"/></small>
                            </div>
                            <form method="post" action="${pageContext.request.contextPath}/graduate/cv" class="d-inline confirm-action">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="action" value="delete">
                                <a class="btn btn-outline-primary btn-sm me-2" href="${pageContext.request.contextPath}/graduate/cv?action=edit&amp;type=reference&amp;id=${item.reference_id}">Editar</a>
<input type="hidden" name="type" value="reference">
                                <input type="hidden" name="id" value="${item.reference_id}">
                                <button class="btn btn-outline-danger btn-sm" type="submit">
                                    <i class="fa-solid fa-trash"></i>
                                </button>
                            </form>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>

</div>

<%@ include file="../common/footer.jsp" %>
