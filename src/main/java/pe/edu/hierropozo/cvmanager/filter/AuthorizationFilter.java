package pe.edu.hierropozo.cvmanager.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtro de autorización basado en roles.
 * Comprueba que el rol del usuario autenticado coincida con el módulo al que intenta acceder:
 * /graduate/* -> role: graduate
 * /company/*  -> role: company
 * /admin/*    -> role: admin
 */
public class AuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        if (session == null) {
            chain.doFilter(request, response);
            return;
        }

        String role = (String) session.getAttribute("role");
        String uri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = uri.substring(contextPath.length());

        boolean isAuthorized = false;

        if (path.startsWith("/graduate/") && "graduate".equals(role)) {
            isAuthorized = true;
        } else if (path.startsWith("/company/") && "company".equals(role)) {
            isAuthorized = true;
        } else if (path.startsWith("/admin/") && "admin".equals(role)) {
            isAuthorized = true;
        }

        if (!isAuthorized) {
            if(pe.edu.hierropozo.cvmanager.util.JsonResponse.requested(httpRequest))pe.edu.hierropozo.cvmanager.util.JsonResponse.send(httpResponse,403,java.util.Map.of("message","Acceso no autorizado para este rol."));
            else httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso no autorizado para este rol.");
            return;
        }

        chain.doFilter(request, response);
    }
}
