<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="../common/header.jsp" %>
<div class="card p-4"><h2>Editar sección del CV</h2>
<form method="post" action="${pageContext.request.contextPath}/graduate/cv">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
<input type="hidden" name="action" value="update"><input type="hidden" name="type" value="<c:out value='${type}'/>"><input type="hidden" name="id" value="${itemId}">
<c:choose>
<c:when test="${type == 'education'}"><h3 class="h5">Formación académica</h3>
<div class="mb-3"><label class="form-label" for="institution">Institución</label>
<input class="form-control" id="institution" name="institution" type="text" value="<c:out value='${item.institution}'/>" maxlength="150" required></div>
<div class="mb-3"><label class="form-label" for="degree">Grado o carrera</label>
<input class="form-control" id="degree" name="degree" type="text" value="<c:out value='${item.degree}'/>" maxlength="120" required></div>
<div class="mb-3"><label class="form-label" for="fieldOfStudy">Campo de estudio</label>
<input class="form-control" id="fieldOfStudy" name="fieldOfStudy" type="text" value="<c:out value='${item.field_of_study}'/>" maxlength="120"></div>
<div class="mb-3"><label class="form-label" for="startDate">Fecha de inicio</label>
<input class="form-control" id="startDate" name="startDate" type="date" value="<c:out value='${item.start_date}'/>" maxlength="10"></div>
<div class="mb-3"><label class="form-label" for="endDate">Fecha final</label>
<input class="form-control" id="endDate" name="endDate" type="date" value="<c:out value='${item.end_date}'/>" maxlength="10"></div>
<div class="mb-3"><label class="form-label" for="description">Descripción</label>
<textarea class="form-control" id="description" name="description" maxlength="5000"><c:out value="${item.description}"/></textarea></div>
</c:when>
<c:when test="${type == 'experience'}"><h3 class="h5">Experiencia laboral</h3>
<div class="mb-3"><label class="form-label" for="companyName">Empresa</label>
<input class="form-control" id="companyName" name="companyName" type="text" value="<c:out value='${item.company_name}'/>" maxlength="150" required></div>
<div class="mb-3"><label class="form-label" for="position">Cargo</label>
<input class="form-control" id="position" name="position" type="text" value="<c:out value='${item.position}'/>" maxlength="120" required></div>
<div class="mb-3"><label class="form-label" for="startDate">Fecha de inicio</label>
<input class="form-control" id="startDate" name="startDate" type="date" value="<c:out value='${item.start_date}'/>" maxlength="10"></div>
<div class="mb-3"><label class="form-label" for="endDate">Fecha final</label>
<input class="form-control" id="endDate" name="endDate" type="date" value="<c:out value='${item.end_date}'/>" maxlength="10"></div>
<div class="mb-3"><label class="form-label" for="responsibilities">Responsabilidades</label>
<textarea class="form-control" id="responsibilities" name="responsibilities" maxlength="5000"><c:out value="${item.responsibilities}"/></textarea></div>
</c:when>
<c:when test="${type == 'skill'}"><h3 class="h5">Habilidad</h3>
<div class="mb-3"><label class="form-label" for="skillName">Habilidad</label>
<input class="form-control" id="skillName" name="skillName" type="text" value="<c:out value='${item.skill_name}'/>" maxlength="100" required></div>
<div class="mb-3"><label class="form-label" for="category">Categoría</label>
<select class="form-select" id="category" name="category"><option value="technical" ${item.skill_category == 'technical' ? 'selected' : ''}>Técnica</option><option value="soft" ${item.skill_category == 'soft' ? 'selected' : ''}>Blanda</option><option value="other" ${item.skill_category == 'other' ? 'selected' : ''}>Otra</option></select></div>
<div class="mb-3"><label class="form-label" for="proficiencyLevel">Nivel de dominio</label>
<input class="form-control" id="proficiencyLevel" name="proficiencyLevel" type="number" value="<c:out value='${item.proficiency_level}'/>" maxlength="1" min="1" max="5" required></div>
</c:when>
<c:when test="${type == 'language'}"><h3 class="h5">Idioma</h3>
<div class="mb-3"><label class="form-label" for="languageName">Idioma</label>
<input class="form-control" id="languageName" name="languageName" type="text" value="<c:out value='${item.language_name}'/>" maxlength="80" required></div>
<div class="mb-3"><label class="form-label" for="proficiencyLevel">Dominio</label>
<select class="form-select" id="proficiencyLevel" name="proficiencyLevel"><option value="basic" ${item.proficiency_level == 'basic' ? 'selected' : ''}>Básico</option><option value="intermediate" ${item.proficiency_level == 'intermediate' ? 'selected' : ''}>Intermedio</option><option value="advanced" ${item.proficiency_level == 'advanced' ? 'selected' : ''}>Avanzado</option><option value="native" ${item.proficiency_level == 'native' ? 'selected' : ''}>Nativo</option></select></div>
</c:when>
<c:when test="${type == 'certification'}"><h3 class="h5">Certificación</h3>
<div class="mb-3"><label class="form-label" for="name">Certificación</label>
<input class="form-control" id="name" name="name" type="text" value="<c:out value='${item.name}'/>" maxlength="150" required></div>
<div class="mb-3"><label class="form-label" for="organization">Entidad emisora</label>
<input class="form-control" id="organization" name="organization" type="text" value="<c:out value='${item.issuing_organization}'/>" maxlength="150"></div>
<div class="mb-3"><label class="form-label" for="issueDate">Fecha de emisión</label>
<input class="form-control" id="issueDate" name="issueDate" type="date" value="<c:out value='${item.issue_date}'/>" maxlength="10"></div>
</c:when>
<c:when test="${type == 'reference'}"><h3 class="h5">Referencia profesional</h3>
<div class="mb-3"><label class="form-label" for="fullName">Nombre completo</label>
<input class="form-control" id="fullName" name="fullName" type="text" value="<c:out value='${item.full_name}'/>" maxlength="150" required></div>
<div class="mb-3"><label class="form-label" for="position">Cargo</label>
<input class="form-control" id="position" name="position" type="text" value="<c:out value='${item.position}'/>" maxlength="120"></div>
<div class="mb-3"><label class="form-label" for="companyName">Empresa</label>
<input class="form-control" id="companyName" name="companyName" type="text" value="<c:out value='${item.company_name}'/>" maxlength="150"></div>
<div class="mb-3"><label class="form-label" for="phone">Teléfono</label>
<input class="form-control" id="phone" name="phone" type="tel" value="<c:out value='${item.phone}'/>" maxlength="30"></div>
<div class="mb-3"><label class="form-label" for="email">Correo</label>
<input class="form-control" id="email" name="email" type="email" value="<c:out value='${item.email}'/>" maxlength="150"></div>
</c:when>
</c:choose><button class="btn btn-success">Guardar cambios</button> <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/graduate/cv">Cancelar</a></form></div>
<%@ include file="../common/footer.jsp" %>