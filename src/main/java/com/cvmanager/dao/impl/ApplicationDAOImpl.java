package com.cvmanager.dao.impl;
import com.cvmanager.dao.interfaces.*;
import java.sql.*;
import java.util.*;
import com.cvmanager.utils.DBConnection;
import com.cvmanager.utils.ValidacionUtil;

public class ApplicationDAOImpl implements ApplicationDAO {
    private final com.cvmanager.services.NotificacionService notifService = new com.cvmanager.services.NotificacionService();

    public List<Map<String,Object>> applications(int userId,boolean company)throws SQLException {
        String contactVisibility = "CASE "
            + "WHEN NOT (" + company + ") THEN gu.email "
            + "WHEN a.status IN ('PRESELECCIONADO','ENTREVISTA','SELECCIONADO','accepted') "
            + "  OR EXISTS(SELECT 1 FROM solicitudes_contacto sc WHERE sc.company_id=e.company_id AND sc.graduate_id=g.graduate_id AND sc.status='accepted') THEN gu.email "
            + "ELSE NULL END AS email, "
            + "CASE "
            + "WHEN NOT (" + company + ") THEN g.phone "
            + "WHEN a.status IN ('PRESELECCIONADO','ENTREVISTA','SELECCIONADO','accepted') "
            + "  OR EXISTS(SELECT 1 FROM solicitudes_contacto sc WHERE sc.company_id=e.company_id AND sc.graduate_id=g.graduate_id AND sc.status='accepted') THEN g.phone "
            + "ELSE NULL END AS phone, "
            + "CASE "
            + "WHEN a.status IN ('PRESELECCIONADO','ENTREVISTA','SELECCIONADO','accepted') "
            + "  OR EXISTS(SELECT 1 FROM solicitudes_contacto sc WHERE sc.company_id=e.company_id AND sc.graduate_id=g.graduate_id AND sc.status='accepted') THEN TRUE "
            + "ELSE FALSE END AS contact_unlocked ";

        return rows("SELECT a.*,j.title,j.city AS job_city,e.company_name,e.phone AS company_phone,e.website AS company_website,eu.email AS company_email,g.first_name,g.last_name,ca.name AS career_name,cv.professional_summary,cv.cv_id,"
            + contactVisibility
            + "FROM postulaciones a JOIN vacantes j ON j.job_id=a.job_id JOIN empresas e ON e.company_id=j.company_id JOIN usuarios eu ON eu.user_id=e.user_id JOIN egresados g ON g.graduate_id=a.graduate_id LEFT JOIN carreras ca ON ca.career_id=g.career_id JOIN usuarios gu ON gu.user_id=g.user_id JOIN cv ON cv.graduate_id=g.graduate_id WHERE "
            + (company?"e.user_id=?":"g.user_id=?")+" AND a.status NOT IN ('withdrawn','RETIRADO') ORDER BY a.created_at DESC",userId);
    }

    public void apply(int userId,int jobId,String message)throws SQLException {
        // Lock the vacancy, serializing applications with closing the offer.
        try(Connection c=DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                int companyUserId;
                String jobTitle;
                try(PreparedStatement st=c.prepareStatement("SELECT j.job_id,j.title,u.user_id AS company_user_id FROM vacantes j JOIN empresas e ON e.company_id=j.company_id JOIN usuarios u ON u.user_id=e.user_id LEFT JOIN vacante_detalles d ON d.job_id=j.job_id WHERE j.job_id=? AND COALESCE(d.archived,0)=0 AND (d.opening_date IS NULL OR d.opening_date<=CURRENT_DATE) AND j.status IN ('PUBLICADA','open') AND (j.closing_date IS NULL OR j.closing_date>=CURRENT_DATE) AND u.status='active' AND u.role='company' FOR UPDATE")) {
                    st.setInt(1,jobId);
                    try(ResultSet rs=st.executeQuery()){
                        if(!rs.next())throw new IllegalArgumentException("La vacante está cerrada o no disponible.");
                        jobTitle = rs.getString("title");
                        companyUserId = rs.getInt("company_user_id");
                    }
                }
                String applicantName = "Un candidato";
                try(PreparedStatement st=c.prepareStatement("SELECT CONCAT(first_name, ' ', last_name) AS full_name FROM egresados WHERE user_id=?")) {
                    st.setInt(1, userId);
                    try(ResultSet rs=st.executeQuery()){if(rs.next())applicantName=rs.getString("full_name");}
                }
                try(PreparedStatement st=c.prepareStatement("INSERT INTO postulaciones(job_id,graduate_id,message,status) SELECT ?,graduate_id,?,'POSTULADO' FROM egresados WHERE user_id=?")) {
                    st.setInt(1,jobId);st.setString(2,message);st.setInt(3,userId);
                    if(st.executeUpdate()!=1)throw new IllegalArgumentException("Perfil inexistente.");
                }
                c.commit();

                // Notificar a la empresa
                notifService.notifyNewApplication(companyUserId, applicantName, jobTitle);

            }catch(SQLException|RuntimeException e){c.rollback();throw e;}
        }
    }

    public void respond(int userId,boolean company,int id,String status)throws SQLException {
        // Normalizar estado
        String normalizedStatus = switch (status != null ? status.trim() : "") {
            case "pending" -> "POSTULADO";
            case "reviewing" -> "EN_REVISION";
            case "accepted" -> "SELECCIONADO";
            case "rejected" -> "NO_SELECCIONADO";
            case "withdrawn" -> "RETIRADO";
            default -> status != null ? status.trim() : "POSTULADO";
        };

        String sql=company?"UPDATE postulaciones a JOIN vacantes j ON j.job_id=a.job_id JOIN empresas e ON e.company_id=j.company_id SET a.status=? WHERE a.application_id=? AND e.user_id=? AND a.status NOT IN ('withdrawn','RETIRADO')":
            "UPDATE postulaciones a JOIN egresados g ON g.graduate_id=a.graduate_id SET a.status=? WHERE a.application_id=? AND g.user_id=?";
        
        try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement(sql)) {
            st.setString(1,normalizedStatus);st.setInt(2,id);st.setInt(3,userId);
            if(st.executeUpdate()!=1)throw new IllegalArgumentException("La postulación no está disponible.");
        }

        // Si fue una empresa quien cambio el estado, notificar al egresado
        if (company) {
            try {
                var info = rows("SELECT a.graduate_id,gu.user_id AS grad_user_id,j.title,e.company_name "
                    + "FROM postulaciones a JOIN vacantes j ON j.job_id=a.job_id "
                    + "JOIN empresas e ON e.company_id=j.company_id "
                    + "JOIN egresados g ON g.graduate_id=a.graduate_id "
                    + "JOIN usuarios gu ON gu.user_id=g.user_id "
                    + "WHERE a.application_id=?", id);
                if (!info.isEmpty()) {
                    int gradUserId = ((Number) info.get(0).get("grad_user_id")).intValue();
                    String jobTitle = (String) info.get(0).get("title");
                    String companyName = (String) info.get(0).get("company_name");
                    notifService.notifyApplicationStatusChange(gradUserId, jobTitle, companyName, normalizedStatus);
                }
            } catch (Exception ignored) {}
        }
    }

    public boolean canReadCv(int companyUserId,int cvId)throws SQLException {
        return !rows("SELECT cv.cv_id FROM cv JOIN egresados g ON g.graduate_id=cv.graduate_id JOIN usuarios u ON u.user_id=g.user_id JOIN postulaciones a ON a.graduate_id=g.graduate_id JOIN vacantes j ON j.job_id=a.job_id JOIN empresas e ON e.company_id=j.company_id WHERE cv.cv_id=? AND e.user_id=? AND a.status NOT IN ('withdrawn','RETIRADO') AND u.status='active' AND u.role='graduate'",cvId,companyUserId).isEmpty();
    }
    public static List<Map<String,Object>> rows(String sql,Object... values)throws SQLException {
        try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement(sql)) {
            for(int i=0;i<values.length;i++)st.setObject(i+1,values[i]);
            try(ResultSet rs=st.executeQuery()) {
                List<Map<String,Object>> rows=new ArrayList<>();
                while(rs.next()){
                    Map<String,Object> row=new LinkedHashMap<>();
                    for(int i=1;i<=rs.getMetaData().getColumnCount();i++) {
                        String col = rs.getMetaData().getColumnLabel(i);
                        Object val = rs.getObject(i);
                        if ("contact_unlocked".equalsIgnoreCase(col) && val instanceof Number n) {
                            val = n.intValue() == 1;
                        }
                        row.put(col, val);
                    }
                    rows.add(row);
                }
                return rows;
            }
        }
    }
}
