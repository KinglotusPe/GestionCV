package com.cvmanager.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.cvmanager.dao.interfaces.UsuarioDAO;
import com.cvmanager.dao.impl.UsuarioDAOImpl;
import com.cvmanager.services.RegistrationService;
import com.cvmanager.utils.ValidacionUtil;

/**
 * Controlador para el registro de nuevos egresados y empresas.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UsuarioDAO userDAO = new UsuarioDAOImpl();
    private final RegistrationService registrationService = new RegistrationService();

    private final com.cvmanager.dao.interfaces.CareerDAO careerDAO = new com.cvmanager.dao.impl.CareerDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            request.setAttribute("careers", careerDAO.findAll(true));
        } catch (Exception ignored) {}
        request.setAttribute("pageTitle", "Registro de Cuenta - CVManager");
        request.getRequestDispatcher("/WEB-INF/views/auth/registro.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = ValidacionUtil.clean(request.getParameter("email"));
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        try {
            if (!ValidacionUtil.email(email) || !ValidacionUtil.password(password) ||
                (!"graduate".equals(role) && !"company".equals(role))) {
                response.setStatus(400);
                request.setAttribute("error", "Datos no válidos. La contraseña debe tener al menos 8 caracteres y el correo ser válido.");
                doGet(request, response);
                return;
            }

            if (userDAO.findByEmail(email) != null) {
                response.setStatus(400);
                request.setAttribute("error", "El correo electrónico ya se encuentra registrado.");
                doGet(request, response);
                return;
            }

            if ("graduate".equals(role)) {
                com.cvmanager.models.Egresados graduate = new com.cvmanager.models.Egresados();
                graduate.setFirstName(ValidacionUtil.clean(request.getParameter("firstName")));
                graduate.setLastName(ValidacionUtil.clean(request.getParameter("lastName")));
                graduate.setDocumentType("DNI");
                graduate.setDocumentNumber(ValidacionUtil.clean(request.getParameter("documentNumber")));
                graduate.setPhone(ValidacionUtil.clean(request.getParameter("phone")));
                graduate.setInstitutionalCode(ValidacionUtil.clean(request.getParameter("institutionalCode")));
                
                String careerIdStr = request.getParameter("careerId");
                if (careerIdStr != null && !careerIdStr.isBlank()) {
                    graduate.setCareerId(Integer.valueOf(careerIdStr.trim()));
                }
                
                String entryYearStr = request.getParameter("entryYear");
                if (entryYearStr != null && !entryYearStr.isBlank()) {
                    graduate.setEntryYear(Integer.valueOf(entryYearStr.trim()));
                }
                
                String graduationYearStr = request.getParameter("graduationYear");
                if (graduationYearStr != null && !graduationYearStr.isBlank()) {
                    graduate.setGraduationYear(Integer.valueOf(graduationYearStr.trim()));
                }
                
                String academicStatus = request.getParameter("academicStatus");
                graduate.setAcademicStatus("ESTUDIANTE".equalsIgnoreCase(academicStatus) ? "ESTUDIANTE" : "EGRESADO");

                registrationService.registerGraduate(email, password, graduate);
            } else {
                java.util.Map<String, String> data = new java.util.HashMap<>();
                data.put("companyName", ValidacionUtil.clean(request.getParameter("companyName")));
                data.put("tradeName", ValidacionUtil.clean(request.getParameter("tradeName")));
                data.put("ruc", ValidacionUtil.clean(request.getParameter("ruc")));
                data.put("industry", ValidacionUtil.clean(request.getParameter("industry")));
                data.put("address", ValidacionUtil.clean(request.getParameter("address")));
                data.put("phone", ValidacionUtil.clean(request.getParameter("phone")));
                data.put("representativeName", ValidacionUtil.clean(request.getParameter("representativeName")));
                data.put("representativeDni", ValidacionUtil.clean(request.getParameter("representativeDni")));
                data.put("representativePosition", ValidacionUtil.clean(request.getParameter("representativePosition")));

                registrationService.registerCompany(email, password, data);
            }

            request.setAttribute("success", "Registro completado con éxito. Su cuenta se encuentra en estado 'pendiente' a la espera de verificación y aprobación por el administrador.");
            doGet(request, response);

        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            throw new ServletException("Error al procesar el registro de usuario", e);
        }
    }
}
