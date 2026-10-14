package com.cvmanager.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;
import com.cvmanager.dao.impl.UsuarioDAOImpl;
import com.cvmanager.models.User;
import com.cvmanager.utils.AuditUtil;
import com.cvmanager.utils.JsonResponse;

/** Ordered before authorization in web.xml. Rechecks access on every request. */
public class Autenticacion_Filtro implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Referrer-Policy", "same-origin");
        String path = request.getServletPath();
        if (path.startsWith("/assets/")) { chain.doFilter(req, res); return; }
        response.setHeader("Cache-Control", "no-store");
        HttpSession session = request.getSession();
        boolean privatePath = path.startsWith("/graduate/") || path.startsWith("/company/")
                || path.startsWith("/admin/") || path.equals("/files") || path.equals("/account");
        Integer actor = (Integer) session.getAttribute("userId");
        if (actor != null) {
            try {
                User user = new UsuarioDAOImpl().findById(actor);
                if (user == null || !"active".equals(user.getStatus())
                        || !user.getRole().equals(session.getAttribute("role"))
                        || !user.getPasswordHash().equals(session.getAttribute("authVersion"))) {
                    session.invalidate();
                    if(JsonResponse.requested(request))JsonResponse.send(response,401,java.util.Map.of("message","La sesión expiró. Inicie sesión nuevamente."));
                    else response.sendRedirect(request.getContextPath() + "/login?auth=1");
                    return;
                }
            } catch (Exception e) { throw new ServletException("No se pudo comprobar la sesión", e); }
        } else if (privatePath) {
            if(JsonResponse.requested(request))JsonResponse.send(response,401,java.util.Map.of("message","La sesión expiró. Inicie sesión nuevamente."));
                    else response.sendRedirect(request.getContextPath() + "/login?auth=1");
            return;
        }
        if (session.getAttribute("csrfToken") == null) session.setAttribute("csrfToken", UUID.randomUUID().toString());
        boolean mutation = !java.util.Set.of("GET", "HEAD", "OPTIONS").contains(request.getMethod());
        try {
            if (mutation) {
                String actual = request.getParameter("csrfToken");
                if (actual == null) actual = request.getHeader("X-CSRF-Token");
                String contentType = request.getContentType();
                if (actual == null && contentType != null && contentType.toLowerCase().startsWith("multipart/")) {
                    try {
                        Part part = request.getPart("csrfToken");
                        if (part != null) actual = new String(part.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
                    } catch (Exception ignored) {}
                }
                String expected = (String) session.getAttribute("csrfToken");
                if (actual == null || expected == null || !MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8))) {
                    if(JsonResponse.requested(request))JsonResponse.send(response,403,java.util.Map.of("message","El formulario expiró. Recargue la página."));
                    else response.sendError(403, "El formulario expiró. Recargue la página e intente nuevamente.");
                    return;
                }
            }
            chain.doFilter(req, res);
            if (mutation && response.getStatus() < 400 && !path.equals("/login")) {
                String action=java.util.Objects.toString(request.getParameter("action"),"guardar");
                action=action.substring(0,Math.min(action.length(),80));
                Integer entityId=null;
                try { entityId=Integer.valueOf(request.getParameter("id")); } catch(NumberFormatException ignored) { }
                AuditUtil.log(actor, request.getMethod()+" "+path, "operacion", entityId,
                        "Acción: "+action, request.getRemoteAddr());
            }
        } catch (Exception ex) {
            Throwable cause = ex;
            while (cause.getCause() != null) cause = cause.getCause();
            if (cause instanceof IllegalArgumentException || cause instanceof java.time.DateTimeException
                    || cause instanceof java.sql.SQLIntegrityConstraintViolationException
                    || cause instanceof IllegalStateException) {
                if (response.isCommitted()) throw new ServletException(ex);
                response.setStatus(400);
                request.setAttribute("error", cause instanceof java.sql.SQLIntegrityConstraintViolationException
                        ? "Ya existe un registro con esos datos, o el registro está en uso. Revise los campos."
                        : cause.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error/400.jsp").forward(request, response);
            } else { throw new ServletException(ex); }
        }
    }
}
