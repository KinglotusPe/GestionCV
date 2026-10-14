package com.cvmanager.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import com.cvmanager.utils.ValidacionUtil;
import com.cvmanager.dao.interfaces.CareerDAO;
import com.cvmanager.dao.impl.CareerDAOImpl;
import com.cvmanager.dao.interfaces.EgresadosDAO;
import com.cvmanager.dao.impl.EgresadoDAOImpl;
import com.cvmanager.models.Egresados;

/**
 * Controlador para la visualización y edición del perfil personal del egresado.
 */
@WebServlet("/graduate/profile")
public class ProfileServlet extends HttpServlet {

    private final EgresadosDAO graduateDAO = new EgresadoDAOImpl();
    private final CareerDAO careerDAO = new CareerDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int userId = (Integer) request.getSession().getAttribute("userId");
            Egresados graduate = graduateDAO.findByUserId(userId);

            request.setAttribute("graduate", graduate);
            request.setAttribute("cv",new com.cvmanager.dao.impl.CVDAOImpl().findByGraduate(graduate.getGraduateId()));
            request.setAttribute("careers", careerDAO.findAll(true));
            request.setAttribute("pageTitle", "Mi Perfil - Egresado");
            request.getRequestDispatcher("/WEB-INF/views/graduate/Perfil.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Error al cargar perfil del egresado", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int userId = (Integer) request.getSession().getAttribute("userId");
            Egresados graduate = graduateDAO.findByUserId(userId);

            graduate.setFirstName(ValidacionUtil.text(request.getParameter("firstName"), "Nombres", 80, true));
            graduate.setLastName(ValidacionUtil.text(request.getParameter("lastName"), "Apellidos", 100, true));
            graduate.setDocumentType(request.getParameter("documentType"));
            String document = ValidacionUtil.nullable(request.getParameter("documentNumber"));
            if (!java.util.Set.of("DNI", "CE").contains(graduate.getDocumentType())) throw new IllegalArgumentException("Tipo de documento inválido.");
            if (document != null && ("DNI".equals(graduate.getDocumentType()) ? !ValidacionUtil.dni(document) : !document.matches("[A-Za-z0-9]{9,12}"))) throw new IllegalArgumentException("Número de documento inválido.");
            graduate.setDocumentNumber(document);
            String phone = ValidacionUtil.nullable(request.getParameter("phone"));
            if (phone != null && !ValidacionUtil.phone(phone)) throw new IllegalArgumentException("Teléfono peruano inválido.");
            graduate.setPhone(phone);
            graduate.setAddress(ValidacionUtil.text(request.getParameter("address"), "Address", 180, false));
            graduate.setCity(ValidacionUtil.text(request.getParameter("city"), "City", 80, false));
            graduate.setCountry(ValidacionUtil.text(request.getParameter("country"), "Country", 80, false));

            String birthDateStr = request.getParameter("birthDate");
            if (birthDateStr != null && !birthDateStr.isBlank()) {
                graduate.setBirthDate(Date.valueOf(birthDateStr.trim()));
                if (graduate.getBirthDate().toLocalDate().isAfter(java.time.LocalDate.now())) throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
            } else {
                graduate.setBirthDate(null);
            }

            graduate.setLinkedinUrl(ValidacionUtil.url(request.getParameter("linkedinUrl")));
            graduate.setPortfolioUrl(ValidacionUtil.url(request.getParameter("portfolioUrl")));

            String careerIdStr = request.getParameter("careerId");
            if (careerIdStr != null && !careerIdStr.isBlank()) {
                graduate.setCareerId(Integer.valueOf(careerIdStr.trim()));
            } else {
                graduate.setCareerId(null);
            }

            String graduationYearStr = request.getParameter("graduationYear");
            if (graduationYearStr != null && !graduationYearStr.isBlank()) {
                graduate.setGraduationYear(ValidacionUtil.integer(graduationYearStr, "Año de graduación", 1950, java.time.Year.now().getValue()+5));
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
