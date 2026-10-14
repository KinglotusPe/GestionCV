package com.cvmanager.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.cvmanager.dao.interfaces.CVDAO;
import com.cvmanager.dao.impl.CVDAOImpl;
import com.cvmanager.dao.interfaces.EgresadosDAO;
import com.cvmanager.dao.impl.EgresadoDAOImpl;
import com.cvmanager.models.CV;
import com.cvmanager.models.Egresados;
import com.cvmanager.services.BusquedaServicio;

/**
 * Controlador para la visualización del CV publicado por parte de empresas registradas.
 */
@WebServlet("/company/cv")
public class PublicCVServlet extends HttpServlet {

    private final CVDAO curriculumDAO = new CVDAOImpl();
    private final EgresadosDAO graduateDAO = new EgresadoDAOImpl();
    private final BusquedaServicio searchService = new BusquedaServicio();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int cvId = Integer.parseInt(request.getParameter("id"));
            int companyUserId=(Integer)request.getSession().getAttribute("userId");
            boolean applicationAccess=new com.cvmanager.services.JobService().canReadCv(companyUserId,cvId);
            CV curriculum = curriculumDAO.findPublished(cvId);
            if(curriculum==null&&applicationAccess) {
                Integer graduateId=new com.cvmanager.services.ProfileService().graduateForCv(cvId);
                if(graduateId!=null)curriculum=curriculumDAO.findByGraduate(graduateId);
            }

            if (curriculum == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "El currículum solicitado no existe o no está publicado.");
                return;
            }

            Egresados graduate = graduateDAO.findById(curriculum.getGraduateId());
            int userId = (Integer) request.getSession().getAttribute("userId");

            // Registrar visualización de la empresa en el historial
            searchService.recordView(userId, cvId);

            com.cvmanager.services.ProfileService profileService = new com.cvmanager.services.ProfileService();
            boolean accepted=applicationAccess||profileService.contactAccepted(graduate.getGraduateId(),userId);
            String contactStatus = applicationAccess ? "accepted" : profileService.contactStatus(graduate.getGraduateId(), userId);
            com.cvmanager.models.User gradUser = new com.cvmanager.dao.impl.UsuarioDAOImpl().findById(graduate.getUserId());
            request.setAttribute("graduateEmail", gradUser != null ? gradUser.getEmail() : "");
            request.setAttribute("contactAccepted",accepted);
            request.setAttribute("contactStatus", contactStatus);
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
