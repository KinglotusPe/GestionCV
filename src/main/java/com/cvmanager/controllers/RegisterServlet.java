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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("pageTitle", "Registro de Cuenta - CVManager");
        request.getRequestDispatcher("/WEB-INF/views/auth/registro.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = ValidacionUtil.clean(request.getParameter("email"));
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        String firstName = ValidacionUtil.clean(request.getParameter("firstName"));
        String lastName = ValidacionUtil.clean(request.getParameter("lastName"));
        String companyName = ValidacionUtil.clean(request.getParameter("companyName"));

        try {
            // Validaciones del servidor
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
                ValidacionUtil.text(firstName, "Nombres", 80, true);
                ValidacionUtil.text(lastName, "Apellidos", 100, true);
            } else ValidacionUtil.text(companyName, "Empresa", 150, true);
            ValidacionUtil.text(email, "Correo", 150, true);
            registrationService.register(email, password, role, firstName, lastName, companyName);
            request.setAttribute("success", "Registro completado con éxito. Su cuenta se encuentra en estado 'pendiente' a la espera de aprobación por el administrador.");
            doGet(request, response);

        } catch (Exception e) {
            throw new ServletException("Error al procesar el registro de usuario", e);
        }
    }
}
