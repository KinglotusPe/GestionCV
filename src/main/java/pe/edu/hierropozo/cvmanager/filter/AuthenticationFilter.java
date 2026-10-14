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
 * Filtro de autenticación.
 * Protege las rutas privadas (/graduate/*, /company/*, /admin/*) verificando
 * que el usuario posea una sesión HTTP activa.
 */
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        boolean isAuthenticated = (session != null && session.getAttribute("userId") != null);

        if (!isAuthenticated) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?auth=1");
            return;
        }

        chain.doFilter(request, response);
    }
}
