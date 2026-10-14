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
 * Controlador para el registro de nuevos egresados y empresas.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAOImpl();
    private final RegistrationService registrationService = new RegistrationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("pageTitle", "Registro de Cuenta - CVManager");
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = ValidationUtil.clean(request.getParameter("email"));
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        String firstName = ValidationUtil.clean(request.getParameter("firstName"));
        String lastName = ValidationUtil.clean(request.getParameter("lastName"));
        String companyName = ValidationUtil.clean(request.getParameter("companyName"));

        try {
            // Validaciones del servidor
            if (!ValidationUtil.email(email) || !ValidationUtil.password(password) ||
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
                ValidationUtil.text(firstName, "Nombres", 80, true);
                ValidationUtil.text(lastName, "Apellidos", 100, true);
            } else ValidationUtil.text(companyName, "Empresa", 150, true);
            ValidationUtil.text(email, "Correo", 150, true);
            registrationService.register(email, password, role, firstName, lastName, companyName);
            request.setAttribute("success", "Registro completado con éxito. Su cuenta se encuentra en estado 'pendiente' a la espera de aprobación por el administrador.");
            doGet(request, response);

        } catch (Exception e) {
            throw new ServletException("Error al procesar el registro de usuario", e);
        }
    }
}
