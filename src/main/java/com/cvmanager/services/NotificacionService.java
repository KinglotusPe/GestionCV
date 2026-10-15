package com.cvmanager.services;

import com.cvmanager.dao.impl.NotificacionDAOImpl;
import com.cvmanager.dao.interfaces.NotificacionDAO;
import com.cvmanager.models.Notificacion;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificacionService {
    private static final Logger LOGGER = Logger.getLogger(NotificacionService.class.getName());
    private final NotificacionDAO dao;

    public NotificacionService() {
        this.dao = new NotificacionDAOImpl();
    }

    public NotificacionService(NotificacionDAO dao) {
        this.dao = dao;
    }

    public void notify(int userId, String title, String message, String type, String referenceUrl) {
        try {
            dao.create(userId, title, message, type, referenceUrl);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error al crear notificación para usuario " + userId, e);
        }
    }

    public List<Notificacion> getRecentNotifications(int userId, int limit) {
        try {
            return dao.findByUser(userId, limit);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error al obtener notificaciones del usuario " + userId, e);
            return Collections.emptyList();
        }
    }

    public int getUnreadCount(int userId) {
        try {
            return dao.countUnread(userId);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error al contar notificaciones no leídas para usuario " + userId, e);
            return 0;
        }
    }

    public void markRead(int notificationId, int userId) {
        try {
            dao.markAsRead(notificationId, userId);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error al marcar notificación como leída", e);
        }
    }

    public void markAllRead(int userId) {
        try {
            dao.markAllAsRead(userId);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error al marcar todas las notificaciones como leídas", e);
        }
    }

    public void notifyApplicationStatusChange(int graduateUserId, String jobTitle, String companyName, String newStatus) {
        String title;
        String message;
        String type;
        
        switch (newStatus != null ? newStatus.toUpperCase() : "") {
            case "EN_REVISION":
            case "REVIEWING":
                title = "Postulación en evaluación";
                message = "Tu postulación a '" + jobTitle + "' en " + companyName + " ahora está en etapa de revisión.";
                type = "CAMBIO_ESTADO_POSTULACION";
                break;
            case "PRESELECCIONADO":
            case "SHORTLISTED":
                title = "¡Has sido preseleccionado!";
                message = "Tu perfil para la vacante '" + jobTitle + "' en " + companyName + " fue preseleccionado con éxito.";
                type = "PRESELECCION";
                break;
            case "ENTREVISTA":
            case "INTERVIEW":
                title = "¡Convocatoria a entrevista laboral!";
                message = companyName + " te ha convocado a una entrevista para el puesto '" + jobTitle + "'.";
                type = "ENTREVISTA";
                break;
            case "SELECCIONADO":
            case "ACCEPTED":
                title = "¡Felicitaciones! Has sido seleccionado/a";
                message = "¡La empresa " + companyName + " ha aprobado tu postulación para '" + jobTitle + "'! Revisa los datos de contacto para coordinar tu incorporación.";
                type = "SELECCION";
                break;
            case "NO_SELECCIONADO":
            case "REJECTED":
                title = "Actualización de tu postulación";
                message = "La empresa " + companyName + " ha concluido el proceso para '" + jobTitle + "'. Tu perfil no fue seleccionado en esta oportunidad.";
                type = "RECHAZO";
                break;
            default:
                title = "Estado de postulación actualizado";
                message = "Tu postulación a '" + jobTitle + "' en " + companyName + " ha cambiado de estado a: " + newStatus;
                type = "CAMBIO_ESTADO_POSTULACION";
                break;
        }

        notify(graduateUserId, title, message, type, "/graduate/jobs?tab=applications");
    }

    public void notifyNewApplication(int companyUserId, String applicantName, String jobTitle) {
        String title = "Nueva postulación recibida";
        String message = applicantName + " se ha postulado a tu convocatoria: '" + jobTitle + "'.";
        notify(companyUserId, title, message, "POSTULACION_RECIBIDA", "/company/jobs?tab=applications");
    }

    public void notifyContactRequested(int graduateUserId, String companyName) {
        String title = "Nueva propuesta de contacto";
        String message = "La empresa " + companyName + " está interesada en tu perfil y ha solicitado tus datos de contacto directo.";
        notify(graduateUserId, title, message, "SOLICITUD_CONTACTO", "/graduate/requests");
    }

    public void notifyContactAccepted(int companyUserId, String applicantName) {
        String title = "Contacto autorizado por el candidato";
        String message = applicantName + " ha aprobado tu solicitud de contacto directo. Puedes consultar su teléfono y correo en tu panel de contactos.";
        notify(companyUserId, title, message, "CONTACTO_ACEPTADO", "/company/favorites?tab=requests");
    }

    public void notifyAccountApproved(int userId, String role) {
        String title = "Cuenta institucional aprobada";
        String message = "Un administrador ha verificado y aprobado tu cuenta institucional. Ya tienes acceso completo a la plataforma.";
        String url = "company".equalsIgnoreCase(role) ? "/company/dashboard" : "/graduate/dashboard";
        notify(userId, title, message, "CUENTA_APROBADA", url);
    }
}
