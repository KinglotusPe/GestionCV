package pe.edu.hierropozo.cvmanager.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import pe.edu.hierropozo.cvmanager.dao.UserDAO;
import pe.edu.hierropozo.cvmanager.dao.UserDAOImpl;
import pe.edu.hierropozo.cvmanager.service.RegistrationService;
import pe.edu.hierropozo.cvmanager.util.ValidationUtil;

/**
 * Controlador para la configuración segura del administrador inicial del sistema.
 * Se bloquea automáticamente una vez que existe al menos un usuario administrador.
 */
@WebServlet("/setup-admin")
public class SetupAdminServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAOImpl();
    private final RegistrationService registrationService = new RegistrationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            boolean hasAdmin = userDAO.hasAdmin();
            request.setAttribute("locked", hasAdmin);
            request.setAttribute("pageTitle", "Configuración Inicial de Administrador - CVManager");
            request.getRequestDispatcher("/WEB-INF/views/auth/setupAdmin.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error al verificar estado de administración", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            if (userDAO.hasAdmin()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Ya existe un administrador en el sistema.");
                return;
            }

            String email = request.getParameter("email");
            String password = request.getParameter("password");

            if (!ValidationUtil.email(email) || !ValidationUtil.password(password)) {
                request.setAttribute("error", "Correo o contraseña no válidos (mínimo 8 caracteres).");
                doGet(request, response);
                return;
            }

            registrationService.createAdmin(email, password);
            response.sendRedirect(request.getContextPath() + "/login?adminCreated=1");

        } catch (Exception e) {
            throw new ServletException("Error al inicializar la cuenta administrativa", e);
        }
    }
}
