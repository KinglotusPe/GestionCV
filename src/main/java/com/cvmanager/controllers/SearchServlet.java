package com.cvmanager.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.cvmanager.dao.interfaces.CareerDAO;
import com.cvmanager.dao.impl.CareerDAOImpl;
import com.cvmanager.services.BusquedaServicio;

/**
 * Controlador de búsqueda de egresados para empresas con paginación y filtros.
 */
@WebServlet("/company/search")
public class SearchServlet extends HttpServlet {

    private final BusquedaServicio searchService = new BusquedaServicio();
    private final CareerDAO careerDAO = new CareerDAOImpl();
    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int page = 1;
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isBlank()) {
                try {
                    page = Math.min(100000, Math.max(1, Integer.parseInt(pageParam.trim())));
                } catch (NumberFormatException ignored) {
                    page = 1;
                }
            }

            String query = request.getParameter("q");
            String career = request.getParameter("career");
            String skill = request.getParameter("skill");
            String city = request.getParameter("city");

            List<Map<String, Object>> results = searchService.search(query, career, skill, city, request.getParameter("year"), page, PAGE_SIZE);

            request.setAttribute("hasNext",results.size()>PAGE_SIZE);
            request.setAttribute("results", results.subList(0,Math.min(results.size(),PAGE_SIZE)));
            request.setAttribute("careers", careerDAO.findAll(true));
            request.setAttribute("page", page);
            request.setAttribute("pageTitle", "Búsqueda de Candidatos - CVManager");

            request.getRequestDispatcher("/WEB-INF/views/company/Buscar.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Error al realizar la búsqueda de candidatos", e);
        }
    }
}
