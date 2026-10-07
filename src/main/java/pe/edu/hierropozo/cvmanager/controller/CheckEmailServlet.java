package pe.edu.hierropozo.cvmanager.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import pe.edu.hierropozo.cvmanager.dao.UserDAO;
import pe.edu.hierropozo.cvmanager.dao.UserDAOImpl;
import pe.edu.hierropozo.cvmanager.model.User;
import pe.edu.hierropozo.cvmanager.util.ValidationUtil;

/**
 * Endpoint asíncrono (Ajax) para verificar disponibilidad de correo electrónico en tiempo real.
 * Responde con JSON simple sin requerir recarga completa de la página.
 */
@WebServlet("/api/check-email")
public class CheckEmailServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        String email = ValidationUtil.clean(request.getParameter("email"));

        boolean isAvailable = false;
        boolean isValid = ValidationUtil.email(email);

        if (isValid) {
            try {
                User existing = userDAO.findByEmail(email);
                isAvailable = (existing == null);
            } catch (Exception e) {
                isAvailable = false;
            }
        }

        try (PrintWriter out = response.getWriter()) {
            out.printf("{\"valid\": %b, \"available\": %b}", isValid, isAvailable);
        }
    }
}
