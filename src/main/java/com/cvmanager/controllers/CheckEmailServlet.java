package com.cvmanager.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import com.cvmanager.dao.interfaces.UsuarioDAO;
import com.cvmanager.dao.impl.UsuarioDAOImpl;
import com.cvmanager.models.User;
import com.cvmanager.utils.ValidacionUtil;

/**
 * Endpoint asíncrono (Ajax) para verificar disponibilidad de correo electrónico en tiempo real.
 * Responde con JSON simple sin requerir recarga completa de la página.
 */
@WebServlet("/api/check-email")
public class CheckEmailServlet extends HttpServlet {

    private final UsuarioDAO userDAO = new UsuarioDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        String email = ValidacionUtil.clean(request.getParameter("email"));

        boolean isAvailable = false;
        boolean isValid = ValidacionUtil.email(email);

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
