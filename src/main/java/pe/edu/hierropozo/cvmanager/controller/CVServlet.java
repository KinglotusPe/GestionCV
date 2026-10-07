package pe.edu.hierropozo.cvmanager.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import pe.edu.hierropozo.cvmanager.dao.CurriculumDAO;
import pe.edu.hierropozo.cvmanager.dao.CurriculumDAOImpl;
import pe.edu.hierropozo.cvmanager.dao.GraduateDAO;
import pe.edu.hierropozo.cvmanager.dao.GraduateDAOImpl;
import pe.edu.hierropozo.cvmanager.model.Curriculum;
import pe.edu.hierropozo.cvmanager.model.Graduate;

/**
 * Controlador integral para la administración del Currículum Vítae del egresado:
 * Cabecera, publicación/despublicación y CRUD de subsecciones
 * (Educación, Experiencia, Habilidades, Idiomas, Certificaciones, Referencias).
 */
@WebServlet("/graduate/cv")
public class CVServlet extends HttpServlet {

    private final GraduateDAO graduateDAO = new GraduateDAOImpl();
    private final CurriculumDAO curriculumDAO = new CurriculumDAOImpl();

    private Curriculum loadCurriculum(HttpServletRequest request) throws Exception {
        int userId = (Integer) request.getSession().getAttribute("userId");
        Graduate graduate = graduateDAO.findByUserId(userId);
        return curriculumDAO.findByGraduate(graduate.getGraduateId());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Curriculum curriculum = loadCurriculum(request);

            request.setAttribute("cv", curriculum);
            request.setAttribute("education", curriculumDAO.section("educacion", "cv_id", curriculum.getCvId()));
            request.setAttribute("experience", curriculumDAO.section("experiencia", "cv_id", curriculum.getCvId()));
            request.setAttribute("skills", curriculumDAO.section("habilidades", "cv_id", curriculum.getCvId()));
            request.setAttribute("languages", curriculumDAO.section("idiomas", "cv_id", curriculum.getCvId()));
            request.setAttribute("certificaciones", curriculumDAO.section("certificaciones", "cv_id", curriculum.getCvId()));
            request.setAttribute("references", curriculumDAO.section("referencias", "cv_id", curriculum.getCvId()));
            request.setAttribute("pageTitle", "Mi Currículum Vitae - Egresado");

            request.getRequestDispatcher("/WEB-INF/views/graduate/cv.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Error al cargar secciones del CV", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Curriculum curriculum = loadCurriculum(request);
            String action = request.getParameter("action");

            Map<String, String> params = new HashMap<>();
            request.getParameterMap().forEach((key, values) -> {
                params.put(key, (values != null && values.length > 0) ? values[0] : "");
            });

            switch (action) {
                case "main" -> {
                    curriculum.setTitle(request.getParameter("title"));
                    curriculum.setProfessionalSummary(request.getParameter("summary"));
                    curriculumDAO.updateMain(curriculum);
                }
                case "publish" -> curriculumDAO.setPublished(curriculum.getCvId(), true);
                case "unpublish" -> curriculumDAO.setPublished(curriculum.getCvId(), false);
                case "addEducation" -> curriculumDAO.addEducation(curriculum.getCvId(), params);
                case "addExperience" -> curriculumDAO.addExperience(curriculum.getCvId(), params);
                case "addSkill" -> curriculumDAO.addSkill(curriculum.getCvId(), params);
                case "addLanguage" -> curriculumDAO.addLanguage(curriculum.getCvId(), params);
                case "addCertification" -> curriculumDAO.addCertification(curriculum.getCvId(), params);
                case "addReference" -> curriculumDAO.addReference(curriculum.getCvId(), params);
                case "delete" -> {
                    String sectionType = request.getParameter("type");
                    int itemId = Integer.parseInt(request.getParameter("id"));
                    curriculumDAO.deleteSection(sectionType, itemId, curriculum.getCvId());
                }
                default -> throw new IllegalArgumentException("Acción de CV no reconocida: " + action);
            }

            response.sendRedirect(request.getContextPath() + "/graduate/cv?ok=1");

        } catch (Exception e) {
            throw new ServletException("Error al procesar operación sobre el CV", e);
        }
    }
}
