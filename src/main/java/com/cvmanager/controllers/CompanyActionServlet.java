package com.cvmanager.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.cvmanager.services.BusquedaServicio;

/**
 * Controlador para acciones de la empresa sobre egresados (guardar favorito o solicitar contacto).
 */
@WebServlet("/company/action")
public class CompanyActionServlet extends HttpServlet {

    private final BusquedaServicio searchService = new BusquedaServicio();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int userId = (Integer) request.getSession().getAttribute("userId");
            String action = request.getParameter("action");

            if ("favorite".equals(action)) {
                int cvId = Integer.parseInt(request.getParameter("cvId"));
                searchService.favorite(userId, cvId);
            } else if ("contact".equals(action)) {
                int graduateId = Integer.parseInt(request.getParameter("graduateId"));
                String message = request.getParameter("message");
                searchService.requestContact(userId, graduateId, message);
            } else {
                throw new IllegalArgumentException("Acción no válida: " + action);
            }

            response.sendRedirect(request.getContextPath() + "/company/search?ok=1");

        } catch (Exception e) {
            throw new ServletException("Error al procesar la acción de la empresa", e);
        }
    }
}
