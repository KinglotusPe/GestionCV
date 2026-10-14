package com.cvmanager.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import com.cvmanager.dao.interfaces.UsuarioDAO;
import com.cvmanager.dao.impl.UsuarioDAOImpl;
import com.cvmanager.models.User;
import com.cvmanager.utils.AuditUtil;
import com.cvmanager.utils.PasswordUtil;

/**
 * Controlador para la autenticación de usuarios y direccionamiento según rol.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final com.cvmanager.services.Servicio_Autenticacion authentication = new com.cvmanager.services.Servicio_Autenticacion();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("pageTitle", "Iniciar Sesión - CVManager");
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            User user = authentication.authenticate(email,password);

            if (user == null) {
                response.setStatus(401);
                request.setAttribute("error", com.cvmanager.config.AppConfig.message("auth.invalid"));
                doGet(request, response);
                return;
            }

            if (!"active".equals(user.getStatus())) {
                response.setStatus(401);
                request.setAttribute("error", "Su cuenta no se encuentra activa. Estado actual: " + user.getStatus());
                doGet(request, response);
                return;
            }

            // Crear sesión y almacenar atributos mínimos necesarios
            HttpSession session = request.getSession();
            request.changeSessionId();
            session.setAttribute("authVersion", user.getPasswordHash());
            session.setAttribute("csrfToken", java.util.UUID.randomUUID().toString());
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("role", user.getRole());

            // Actualizar fecha de último acceso y registrar auditoría
            authentication.recordLogin(user.getUserId());
            AuditUtil.log(user.getUserId(), "LOGIN", "usuario", user.getUserId(), "Inicio de sesión exitoso", request.getRemoteAddr());

            // Redirección dinámica según el rol del usuario
            String destination = switch (user.getRole()) {
                case "admin" -> "/admin/dashboard";
                case "company" -> "/company/search";
                default -> "/graduate/profile";
            };

            response.sendRedirect(request.getContextPath() + destination);

        } catch (Exception e) {
            throw new ServletException("Error durante el procesamiento del inicio de sesión", e);
        }
    }
}
