package com.cvmanager.controllers;

import com.cvmanager.models.Notificacion;
import com.cvmanager.services.NotificacionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "NotificacionesServlet", urlPatterns = {"/notifications", "/api/notifications"})
public class NotificacionesServlet extends HttpServlet {

    private final NotificacionService notificacionService = new NotificacionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String action = req.getParameter("action");

        if ("unreadCount".equals(action)) {
            int unread = notificacionService.getUnreadCount(userId);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write("{\"unread\":" + unread + ",\"unreadCount\":" + unread + "}");
            return;
        }

        String readIdParam = req.getParameter("read");
        if (readIdParam != null && !readIdParam.isBlank()) {
            try {
                int notifId = Integer.parseInt(readIdParam);
                notificacionService.markRead(notifId, userId);
            } catch (NumberFormatException ignored) {}

            String redirect = req.getParameter("redirect");
            if (redirect != null && !redirect.isBlank() && redirect.startsWith("/")) {
                res.sendRedirect(req.getContextPath() + redirect);
                return;
            }
        }

        List<Notificacion> notifications = notificacionService.getRecentNotifications(userId, 50);
        int unreadCount = notificacionService.getUnreadCount(userId);

        req.setAttribute("notifications", notifications);
        req.setAttribute("unreadCount", unreadCount);
        req.setAttribute("pageTitle", "Centro de Notificaciones");
        req.getRequestDispatcher("/WEB-INF/views/common/notificaciones.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String action = req.getParameter("action");

        if ("markAllRead".equals(action)) {
            notificacionService.markAllRead(userId);
            session.setAttribute("flashSuccess", "Todas las notificaciones han sido marcadas como leídas.");
        } else if ("markRead".equals(action)) {
            String idStr = req.getParameter("id");
            if (idStr != null) {
                try {
                    int notifId = Integer.parseInt(idStr);
                    notificacionService.markRead(notifId, userId);
                } catch (NumberFormatException ignored) {}
            }
        }

        String redirect = req.getParameter("returnUrl");
        if (redirect != null && !redirect.isBlank() && redirect.startsWith("/")) {
            res.sendRedirect(req.getContextPath() + redirect);
        } else {
            res.sendRedirect(req.getContextPath() + "/notifications");
        }
    }
}
