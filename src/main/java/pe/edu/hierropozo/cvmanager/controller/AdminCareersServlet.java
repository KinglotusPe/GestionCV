package pe.edu.hierropozo.cvmanager.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import pe.edu.hierropozo.cvmanager.dao.CareerDAO;
import pe.edu.hierropozo.cvmanager.dao.CareerDAOImpl;
import pe.edu.hierropozo.cvmanager.model.Career;

/**
 * Controlador administrativo para el CRUD de carreras institucionales.
 */
@WebServlet("/admin/careers")
public class AdminCareersServlet extends HttpServlet {

    private final CareerDAO careerDAO = new CareerDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Career> careers = careerDAO.findAll(false);
            request.setAttribute("careers", careers);
            request.setAttribute("pageTitle", "Gestión de Carreras - CVManager");

            request.getRequestDispatcher("/WEB-INF/views/admin/careers.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Error al listar carreras institucionales", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String action = request.getParameter("action");

            if ("delete".equals(action)) {
                int careerId = Integer.parseInt(request.getParameter("id"));
                careerDAO.delete(careerId);
            } else {
                Career career = new Career();
                String idParam = request.getParameter("id");
                if (idParam != null && !idParam.isBlank()) {
                    career.setCareerId(Integer.parseInt(idParam.trim()));
                }

                career.setName(request.getParameter("name"));
                career.setCode(request.getParameter("code"));
                career.setDescription(request.getParameter("description"));
                career.setDurationYears(Integer.parseInt(request.getParameter("durationYears")));
                career.setActive(request.getParameter("active") != null);

                if ("create".equals(action)) {
                    careerDAO.create(career);
                } else {
                    careerDAO.update(career);
                }
            }

            response.sendRedirect(request.getContextPath() + "/admin/careers?ok=1");

        } catch (Exception e) {
            throw new ServletException("Error al guardar información de la carrera", e);
        }
    }
}
