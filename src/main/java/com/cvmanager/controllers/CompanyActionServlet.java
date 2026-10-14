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

        String returnUrl = request.getParameter("returnUrl");
        String cvId = request.getParameter("cvId");
        String defaultRedirect = cvId != null && !cvId.isBlank() 
            ? request.getContextPath() + "/company/cv?id=" + cvId 
            : request.getContextPath() + "/company/search";

        try {
            int userId = (Integer) request.getSession().getAttribute("userId");
            String action = request.getParameter("action");

            if ("favorite".equals(action)) {
                int id = Integer.parseInt(cvId);
                searchService.favorite(userId, id);
                request.getSession().setAttribute("success", "Candidato guardado en favoritos.");
            } else if ("contact".equals(action)) {
                int graduateId = Integer.parseInt(request.getParameter("graduateId"));
                String message = request.getParameter("message");
                searchService.requestContact(userId, graduateId, message);
                request.getSession().setAttribute("success", "Solicitud de contacto enviada con éxito al candidato.");
            } else {
                throw new IllegalArgumentException("Acción no válida: " + action);
            }

            if (returnUrl != null && !returnUrl.isBlank()) {
                response.sendRedirect(returnUrl + (returnUrl.contains("?") ? "&ok=1" : "?ok=1"));
            } else {
                response.sendRedirect(defaultRedirect + (defaultRedirect.contains("?") ? "&ok=1" : "?ok=1"));
            }

        } catch (IllegalArgumentException e) {
            request.getSession().setAttribute("flashError", e.getMessage());
            response.sendRedirect(defaultRedirect);
        } catch (Exception e) {
            request.getSession().setAttribute("flashError", "Error al procesar la acción: " + e.getMessage());
            response.sendRedirect(defaultRedirect);
        }
    }
}
