package pe.edu.hierropozo.cvmanager.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import pe.edu.hierropozo.cvmanager.service.AdminService;

/**
 * Controlador administrativo para la consulta del registro de bitácora y auditoría del sistema.
 */
@WebServlet("/admin/audit")
public class AdminAuditServlet extends HttpServlet {

    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int limit = 50;
            String limitParam = request.getParameter("limit");
            if (limitParam != null && !limitParam.isBlank()) {
                try {
                    limit = Math.min(200, Math.max(10, Integer.parseInt(limitParam.trim())));
                } catch (NumberFormatException ignored) {
                }
            }

            List<Map<String, Object>> logs = adminService.getAuditLogs(limit);

            request.setAttribute("logs", logs);
            request.setAttribute("limit", limit);
            request.setAttribute("pageTitle", "Bitácora de Auditoría - CVManager");

            request.getRequestDispatcher("/WEB-INF/views/admin/audit.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Error al consultar bitácora de auditoría", e);
        }
    }
}
