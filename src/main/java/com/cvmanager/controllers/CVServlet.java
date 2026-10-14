package com.cvmanager.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.cvmanager.services.CVService;
import com.cvmanager.utils.ValidacionUtil;
import com.cvmanager.dao.interfaces.CVDAO;
import com.cvmanager.dao.impl.CVDAOImpl;
import com.cvmanager.dao.interfaces.EgresadosDAO;
import com.cvmanager.dao.impl.EgresadoDAOImpl;
import com.cvmanager.models.CV;
import com.cvmanager.models.Egresados;

/**
 * Controlador integral para la administración del Currículum Vítae del egresado:
 * Cabecera, publicación/despublicación y CRUD de subsecciones
 * (Educación, Experiencia, Habilidades, Idiomas, Certificaciones, Referencias).
 */
@WebServlet("/graduate/cv")
public class CVServlet extends HttpServlet {

    private final EgresadosDAO graduateDAO = new EgresadoDAOImpl();
    private final CVDAO curriculumDAO = new CVDAOImpl();

    private CV loadCurriculum(HttpServletRequest request) throws Exception {
        int userId = (Integer) request.getSession().getAttribute("userId");
        Egresados graduate = graduateDAO.findByUserId(userId);
        return curriculumDAO.findByGraduate(graduate.getGraduateId());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            CV curriculum = loadCurriculum(request);

            if ("edit".equals(request.getParameter("action"))) {
                String type=request.getParameter("type");
                var definition=CVService.definition(type);
                int id=Integer.parseInt(request.getParameter("id"));
                var item=curriculumDAO.section(definition.table(),"cv_id",curriculum.getCvId()).stream()
                    .filter(row -> ((Number)row.get(definition.id())).intValue()==id).findFirst();
                if(item.isEmpty()){response.sendError(404);return;}
                request.setAttribute("item",item.get());request.setAttribute("type",type);request.setAttribute("itemId",id);
                request.setAttribute("pageTitle","Editar sección del CV");
                request.getRequestDispatcher("/WEB-INF/views/graduate/editarCV.jsp").forward(request,response);return;
            }
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
            CV curriculum = loadCurriculum(request);
            String action = request.getParameter("action");

            Map<String, String> params = new HashMap<>();
            request.getParameterMap().forEach((key, values) -> {
                params.put(key, (values != null && values.length > 0) ? values[0] : "");
            });

            switch (action) {
                case "main" -> {
                    curriculum.setTitle(ValidacionUtil.text(request.getParameter("title"),"Título",150,true));
                    curriculum.setProfessionalSummary(ValidacionUtil.text(request.getParameter("summary"),"Resumen",10000,true));
                    curriculumDAO.updateMain(curriculum);
                }
                case "publish" -> curriculumDAO.setPublished(curriculum.getCvId(), true);
                case "unpublish" -> curriculumDAO.setPublished(curriculum.getCvId(), false);
                case "addEducation" -> new CVService().save(curriculum.getCvId(), "education", null, params);
                case "addExperience" -> new CVService().save(curriculum.getCvId(), "experience", null, params);
                case "addSkill" -> new CVService().save(curriculum.getCvId(), "skill", null, params);
                case "addLanguage" -> new CVService().save(curriculum.getCvId(), "language", null, params);
                case "addCertification" -> new CVService().save(curriculum.getCvId(), "certification", null, params);
                case "addReference" -> new CVService().save(curriculum.getCvId(), "reference", null, params);
                case "update" -> new CVService().save(curriculum.getCvId(), request.getParameter("type"), Integer.valueOf(request.getParameter("id")), params);
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
