package com.cvmanager.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.cvmanager.services.AdminService;

/**
 * Controlador para el panel principal de administración (Dashboard analítico).
 */
@WebServlet("/admin/dashboard")
public class AdminServlet extends HttpServlet {

    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Map<String, Integer> stats = adminService.stats();
            List<Map<String, Object>> byCareer = adminService.byCareer();

            request.setAttribute("stats", stats);
            request.setAttribute("byCareer", byCareer);
            request.setAttribute("pageTitle", "Panel de Administración - CVManager");

            request.getRequestDispatcher("/WEB-INF/views/admin/Panel.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Error al cargar datos del panel de administración", e);
        }
    }
}
