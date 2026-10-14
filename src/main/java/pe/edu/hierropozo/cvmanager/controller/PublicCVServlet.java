package pe.edu.hierropozo.cvmanager.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import pe.edu.hierropozo.cvmanager.dao.CurriculumDAO;
import pe.edu.hierropozo.cvmanager.dao.CurriculumDAOImpl;
import pe.edu.hierropozo.cvmanager.dao.GraduateDAO;
import pe.edu.hierropozo.cvmanager.dao.GraduateDAOImpl;
import pe.edu.hierropozo.cvmanager.model.Curriculum;
import pe.edu.hierropozo.cvmanager.model.Graduate;
import pe.edu.hierropozo.cvmanager.service.SearchService;

/**
 * Controlador para la visualización del CV publicado por parte de empresas registradas.
 */
@WebServlet("/company/cv")
public class PublicCVServlet extends HttpServlet {

    private final CurriculumDAO curriculumDAO = new CurriculumDAOImpl();
    private final GraduateDAO graduateDAO = new GraduateDAOImpl();
    private final SearchService searchService = new SearchService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int cvId = Integer.parseInt(request.getParameter("id"));
            int companyUserId=(Integer)request.getSession().getAttribute("userId");
            boolean applicationAccess=new pe.edu.hierropozo.cvmanager.service.JobService().canReadCv(companyUserId,cvId);
            Curriculum curriculum = curriculumDAO.findPublished(cvId);
            if(curriculum==null&&applicationAccess) {
                Integer graduateId=new pe.edu.hierropozo.cvmanager.service.ProfileService().graduateForCv(cvId);
                if(graduateId!=null)curriculum=curriculumDAO.findByGraduate(graduateId);
            }

            if (curriculum == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "El currículum solicitado no existe o no está publicado.");
                return;
            }

            Graduate graduate = graduateDAO.findById(curriculum.getGraduateId());
            int userId = (Integer) request.getSession().getAttribute("userId");

            // Registrar visualización de la empresa en el historial
            searchService.recordView(userId, cvId);

            boolean accepted=applicationAccess||new pe.edu.hierropozo.cvmanager.service.ProfileService().contactAccepted(graduate.getGraduateId(),userId);
            request.setAttribute("contactAccepted",accepted);
            request.setAttribute("cv", curriculum);
            request.setAttribute("graduate", graduate);
            request.setAttribute("education", curriculumDAO.section("educacion", "cv_id", cvId));
            request.setAttribute("experience", curriculumDAO.section("experiencia", "cv_id", cvId));
            request.setAttribute("skills", curriculumDAO.section("habilidades", "cv_id", cvId));
            request.setAttribute("languages", curriculumDAO.section("idiomas", "cv_id", cvId));
            request.setAttribute("certifications", curriculumDAO.section("certificaciones", "cv_id", cvId));
            request.setAttribute("references", curriculumDAO.section("referencias", "cv_id", cvId));
            request.setAttribute("pageTitle", "Perfil Profesional de " + graduate.getFullName());

            request.getRequestDispatcher("/WEB-INF/views/company/cv.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Error al consultar el currículum del candidato", e);
        }
    }
}
