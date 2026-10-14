package pe.edu.hierropozo.cvmanager.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import pe.edu.hierropozo.cvmanager.util.ValidationUtil;
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
            request.setAttribute("cv",new pe.edu.hierropozo.cvmanager.dao.CurriculumDAOImpl().findByGraduate(graduate.getGraduateId()));
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

            graduate.setFirstName(ValidationUtil.text(request.getParameter("firstName"), "Nombres", 80, true));
            graduate.setLastName(ValidationUtil.text(request.getParameter("lastName"), "Apellidos", 100, true));
            graduate.setDocumentType(request.getParameter("documentType"));
            String document = ValidationUtil.nullable(request.getParameter("documentNumber"));
            if (!java.util.Set.of("DNI", "CE").contains(graduate.getDocumentType())) throw new IllegalArgumentException("Tipo de documento inválido.");
            if (document != null && ("DNI".equals(graduate.getDocumentType()) ? !ValidationUtil.dni(document) : !document.matches("[A-Za-z0-9]{9,12}"))) throw new IllegalArgumentException("Número de documento inválido.");
            graduate.setDocumentNumber(document);
            String phone = ValidationUtil.nullable(request.getParameter("phone"));
            if (phone != null && !ValidationUtil.phone(phone)) throw new IllegalArgumentException("Teléfono peruano inválido.");
            graduate.setPhone(phone);
            graduate.setAddress(ValidationUtil.text(request.getParameter("address"), "Address", 180, false));
            graduate.setCity(ValidationUtil.text(request.getParameter("city"), "City", 80, false));
            graduate.setCountry(ValidationUtil.text(request.getParameter("country"), "Country", 80, false));

            String birthDateStr = request.getParameter("birthDate");
            if (birthDateStr != null && !birthDateStr.isBlank()) {
                graduate.setBirthDate(Date.valueOf(birthDateStr.trim()));
                if (graduate.getBirthDate().toLocalDate().isAfter(java.time.LocalDate.now())) throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
            } else {
                graduate.setBirthDate(null);
            }

            graduate.setLinkedinUrl(ValidationUtil.url(request.getParameter("linkedinUrl")));
            graduate.setPortfolioUrl(ValidationUtil.url(request.getParameter("portfolioUrl")));

            String careerIdStr = request.getParameter("careerId");
            if (careerIdStr != null && !careerIdStr.isBlank()) {
                graduate.setCareerId(Integer.valueOf(careerIdStr.trim()));
            } else {
                graduate.setCareerId(null);
            }

            String graduationYearStr = request.getParameter("graduationYear");
            if (graduationYearStr != null && !graduationYearStr.isBlank()) {
                graduate.setGraduationYear(ValidationUtil.integer(graduationYearStr, "Año de graduación", 1950, java.time.Year.now().getValue()+5));
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
