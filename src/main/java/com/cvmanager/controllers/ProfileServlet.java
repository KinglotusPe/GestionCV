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

    private final com.cvmanager.dao.interfaces.UsuarioDAO userDAO = new com.cvmanager.dao.impl.UsuarioDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int userId = (Integer) request.getSession().getAttribute("userId");
            Egresados graduate = graduateDAO.findByUserId(userId);
            com.cvmanager.models.User user = userDAO.findById(userId);
            boolean isApproved = user != null && "active".equals(user.getStatus());

            request.setAttribute("graduate", graduate);
            request.setAttribute("isApproved", isApproved);
            request.setAttribute("cv", new com.cvmanager.dao.impl.CVDAOImpl().findByGraduate(graduate.getGraduateId()));
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
            if (graduate == null) throw new IllegalArgumentException("Perfil de egresado inexistente.");

            com.cvmanager.models.User user = userDAO.findById(userId);
            boolean isApproved = user != null && "active".equals(user.getStatus());

            if (isApproved) {
                // BACKEND LOCK: Si la cuenta está aprobada, verificar si se intentó modificar algún campo bloqueado
                String sentDni = ValidacionUtil.nullable(request.getParameter("documentNumber"));
                if (sentDni != null && graduate.getDocumentNumber() != null && !sentDni.trim().equals(graduate.getDocumentNumber().trim())) {
                    throw new IllegalArgumentException("No se permite modificar el DNI de una cuenta ya verificada y aprobada.");
                }
                String sentFirstName = ValidacionUtil.clean(request.getParameter("firstName"));
                if (sentFirstName != null && !sentFirstName.equalsIgnoreCase(graduate.getFirstName())) {
                    throw new IllegalArgumentException("No se permite modificar los nombres de una cuenta ya verificada.");
                }
                String sentLastName = ValidacionUtil.clean(request.getParameter("lastName"));
                if (sentLastName != null && !sentLastName.equalsIgnoreCase(graduate.getLastName())) {
                    throw new IllegalArgumentException("No se permite modificar los apellidos de una cuenta ya verificada.");
                }
                // Los campos clave de identidad se mantienen sin cambios
            } else {
                // Cuenta aún pendiente: permitir actualización de identidad
                graduate.setFirstName(ValidacionUtil.text(request.getParameter("firstName"), "Nombres", 80, true));
                graduate.setLastName(ValidacionUtil.text(request.getParameter("lastName"), "Apellidos", 100, true));
                graduate.setDocumentType("DNI");
                String document = ValidacionUtil.nullable(request.getParameter("documentNumber"));
                if (document != null && !ValidacionUtil.dni(document)) throw new IllegalArgumentException("DNI debe contener exactamente 8 dígitos numéricos.");
                graduate.setDocumentNumber(document);
                graduate.setInstitutionalCode(ValidacionUtil.text(request.getParameter("institutionalCode"), "Código institucional", 30, false));
                
                String careerIdStr = request.getParameter("careerId");
                if (careerIdStr != null && !careerIdStr.isBlank()) {
                    graduate.setCareerId(Integer.valueOf(careerIdStr.trim()));
                }
                
                String academicStatus = request.getParameter("academicStatus");
                if (academicStatus != null && (academicStatus.equals("ESTUDIANTE") || academicStatus.equals("EGRESADO"))) {
                    graduate.setAcademicStatus(academicStatus);
                }
            }

            // Datos de contacto y adicionales que siempre pueden actualizarse:
            String phone = ValidacionUtil.nullable(request.getParameter("phone"));
            if (phone != null && !ValidacionUtil.phone(phone)) throw new IllegalArgumentException("Teléfono peruano inválido.");
            graduate.setPhone(phone);
            graduate.setAddress(ValidacionUtil.text(request.getParameter("address"), "Dirección", 180, false));
            graduate.setCity(ValidacionUtil.text(request.getParameter("city"), "Ciudad", 80, false));
            graduate.setCountry(ValidacionUtil.text(request.getParameter("country"), "País", 80, false));

            String birthDateStr = request.getParameter("birthDate");
            if (birthDateStr != null && !birthDateStr.isBlank()) {
                graduate.setBirthDate(Date.valueOf(birthDateStr.trim()));
                if (graduate.getBirthDate().toLocalDate().isAfter(java.time.LocalDate.now())) throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
            } else {
                graduate.setBirthDate(null);
            }

            graduate.setLinkedinUrl(ValidacionUtil.url(request.getParameter("linkedinUrl")));
            graduate.setPortfolioUrl(ValidacionUtil.url(request.getParameter("portfolioUrl")));

            String entryYearStr = request.getParameter("entryYear");
            if (entryYearStr != null && !entryYearStr.isBlank()) {
                graduate.setEntryYear(ValidacionUtil.integer(entryYearStr, "Año de ingreso", 1990, java.time.Year.now().getValue() + 5));
            }

            String graduationYearStr = request.getParameter("graduationYear");
            if (graduationYearStr != null && !graduationYearStr.isBlank()) {
                graduate.setGraduationYear(ValidacionUtil.integer(graduationYearStr, "Año de graduación", 1990, java.time.Year.now().getValue() + 5));
            }

            graduate.setPublicProfile("on".equals(request.getParameter("publicProfile")));

            graduateDAO.update(graduate);
            response.sendRedirect(request.getContextPath() + "/graduate/profile?ok=1");

        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            throw new ServletException("Error al actualizar perfil del egresado", e);
        }
    }
}
