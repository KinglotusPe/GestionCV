package com.cvmanager.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtro de codificación de caracteres.
 * Establece UTF-8 en todas las solicitudes y respuestas de la aplicación.
 */
public class Codificacion_Filtro implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (request instanceof HttpServletRequest req && response instanceof HttpServletResponse res) {
            String path = req.getServletPath();
            if (!path.startsWith("/assets/") && !path.startsWith("/files") && !path.startsWith("/uploads/")) {
                res.setContentType("text/html; charset=UTF-8");
            }
        }

        chain.doFilter(request, response);
    }
}

