package com.cvmanager.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import com.cvmanager.dao.interfaces.UsuarioDAO;
import com.cvmanager.dao.impl.UsuarioDAOImpl;
import com.cvmanager.models.User;

/**
 * Controlador administrativo para la gestión de usuarios y cambio de estados
 * (activar, suspender, dar de baja).
 */
@WebServlet("/admin/users")
public class AdminUsersServlet extends HttpServlet {

    private final UsuarioDAO userDAO = new UsuarioDAOImpl();
    private static final Set<String> ALLOWED_STATUSES = Set.of("active", "inactive", "pending", "suspended");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String roleFilter = request.getParameter("role");
            String statusFilter = request.getParameter("status");

            List<User> users = userDAO.findAll(roleFilter, statusFilter);

            request.setAttribute("users", users);
            request.setAttribute("pageTitle", "Gestión de Usuarios - CVManager");

            request.getRequestDispatcher("/WEB-INF/views/admin/usuarios.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Error al listar usuarios para administración", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int userId = Integer.parseInt(request.getParameter("id"));
            if ("resetPassword".equals(request.getParameter("action"))) {
                if(userDAO.findById(userId)==null)throw new IllegalArgumentException("Usuario inexistente.");
                String token=new com.cvmanager.services.PasswordService().issue(userId);
                request.setAttribute("resetLink",request.getContextPath()+"/reset-password?token="+token);
                request.setAttribute("resetEmail",userDAO.findById(userId).getEmail());
                String publicBase=System.getenv("CVMANAGER_PUBLIC_URL");
                if(publicBase!=null && publicBase.matches("https?://[^\\s]+")) request.setAttribute("resetDraft",new com.cvmanager.services.EmailServicio().recoveryDraft(userDAO.findById(userId).getEmail(),publicBase.replaceAll("/+$", "")+"/reset-password?token="+token));
                doGet(request,response);return;
            }
            new com.cvmanager.services.UserManagementService().update(
                (Integer)request.getSession().getAttribute("userId"),userId,request.getParameter("newRole"),request.getParameter("status"),
                request.getParameter("firstName"),request.getParameter("lastName"),request.getParameter("companyName"));

            response.sendRedirect(request.getContextPath() + "/admin/users?ok=1");

        } catch (Exception e) {
            throw new ServletException("Error al actualizar estado del usuario", e);
        }
    }
}
