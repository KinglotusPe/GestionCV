package com.cvmanager.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.cvmanager.services.BusquedaServicio;

/**
 * Controlador para la gestión de candidatos favoritos y solicitudes de contacto de la empresa.
 */
@WebServlet("/company/favorites")
public class CompanyFavoritesServlet extends HttpServlet {

    private final BusquedaServicio searchService = new BusquedaServicio();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int userId = (Integer) request.getSession().getAttribute("userId");
            List<Map<String, Object>> favorites = searchService.getFavorites(userId);
            List<Map<String, Object>> contactRequests = searchService.getContactRequests(userId);

            request.setAttribute("favorites", favorites);
            request.setAttribute("requests", contactRequests);
            request.setAttribute("pageTitle", "Mis Favoritos y Solicitudes - Empresa");

            request.getRequestDispatcher("/WEB-INF/views/company/Preferencia.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Error al cargar favoritos y solicitudes de contacto", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int userId = (Integer) request.getSession().getAttribute("userId");
            String action = request.getParameter("action");

            if ("removeFavorite".equals(action)) {
                int favoriteId = Integer.parseInt(request.getParameter("favoriteId"));
                searchService.removeFavorite(userId, favoriteId);
            }

            response.sendRedirect(request.getContextPath() + "/company/favorites?ok=1");

        } catch (Exception e) {
            throw new ServletException("Error al procesar acción de favoritos", e);
        }
    }
}
