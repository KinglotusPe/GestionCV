package pe.edu.hierropozo.cvmanager.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import pe.edu.hierropozo.cvmanager.dao.CareerDAO;
import pe.edu.hierropozo.cvmanager.dao.CareerDAOImpl;
import pe.edu.hierropozo.cvmanager.dao.GraduateDAO;
import pe.edu.hierropozo.cvmanager.dao.GraduateDAOImpl;
import pe.edu.hierropozo.cvmanager.model.Graduate;

/**
 * Controlador para la visualización y edición del perfil personal del egresado.
 */
@WebServlet("/graduate/profile")
public class GraduateProfileServlet extends HttpServlet {

    private final GraduateDAO graduateDAO = new GraduateDAOImpl();
    private final CareerDAO careerDAO = new CareerDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int userId = (Integer) request.getSession().getAttribute("userId");
            Graduate graduate = graduateDAO.findByUserId(userId);

            request.setAttribute("graduate", graduate);
            request.setAttribute("careers", careerDAO.findAll(true));
            request.setAttribute("pageTitle", "Mi Perfil - Egresado");
            request.getRequestDispatcher("/WEB-INF/views/graduate/profile.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Error al cargar perfil del egresado", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int userId = (Integer) request.getSession().getAttribute("userId");
            Graduate graduate = graduateDAO.findByUserId(userId);

            graduate.setFirstName(request.getParameter("firstName"));
            graduate.setLastName(request.getParameter("lastName"));
            graduate.setDocumentType(request.getParameter("documentType"));
            graduate.setDocumentNumber(request.getParameter("documentNumber"));
            graduate.setPhone(request.getParameter("phone"));
            graduate.setAddress(request.getParameter("address"));
            graduate.setCity(request.getParameter("city"));
            graduate.setCountry(request.getParameter("country"));

            String birthDateStr = request.getParameter("birthDate");
            if (birthDateStr != null && !birthDateStr.isBlank()) {
                graduate.setBirthDate(Date.valueOf(birthDateStr.trim()));
            } else {
                graduate.setBirthDate(null);
            }

            graduate.setLinkedinUrl(request.getParameter("linkedinUrl"));
            graduate.setPortfolioUrl(request.getParameter("portfolioUrl"));

            String careerIdStr = request.getParameter("careerId");
            if (careerIdStr != null && !careerIdStr.isBlank()) {
                graduate.setCareerId(Integer.valueOf(careerIdStr.trim()));
            } else {
                graduate.setCareerId(null);
            }

            String graduationYearStr = request.getParameter("graduationYear");
            if (graduationYearStr != null && !graduationYearStr.isBlank()) {
                graduate.setGraduationYear(Integer.valueOf(graduationYearStr.trim()));
            } else {
                graduate.setGraduationYear(null);
            }

            graduate.setPublicProfile("on".equals(request.getParameter("publicProfile")));

            graduateDAO.update(graduate);
            response.sendRedirect(request.getContextPath() + "/graduate/profile?ok=1");

        } catch (Exception e) {
            throw new ServletException("Error al actualizar perfil del egresado", e);
        }
    }
}
