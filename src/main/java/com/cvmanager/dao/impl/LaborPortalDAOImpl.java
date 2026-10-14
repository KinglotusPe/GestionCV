package com.cvmanager.dao.impl;
import com.cvmanager.dao.interfaces.*;
import java.sql.*;
import java.util.*;
import com.cvmanager.utils.DBConnection;
/** Persistencia de documentos, revisiones y reportes laborales. */
public class LaborPortalDAOImpl implements LaborPortalDAO {
    public void archive(int actor,String role,int job,boolean archive)throws SQLException {
        try(Connection c=DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                if(role.equals("admin")) {
                    if(JobDAOImpl.rows(c,"SELECT job_id FROM vacantes WHERE job_id=? FOR UPDATE",job).isEmpty())throw new IllegalArgumentException("Convocatoria inexistente.");
                } else JobDAOImpl.lockOwned(c,actor,role,job);
                JobDAOImpl.update(c,"INSERT IGNORE INTO vacante_detalles(job_id) VALUES(?)",job);
                JobDAOImpl.update(c,"UPDATE vacante_detalles SET archived=? WHERE job_id=?",archive,job);c.commit();
            }catch(SQLException|RuntimeException e){c.rollback();throw e;}
        }
    }
    public List<Map<String,Object>> documents(int actor,String role,int job)throws SQLException {
        new JobDAOImpl().find(actor,role,job);
        return JobDAOImpl.rows("SELECT document_id,title,created_at FROM vacante_documentos WHERE job_id=? ORDER BY document_id",job);
    }
    public void addDocument(int actor,String role,int job,String title,String stored)throws SQLException {
        try(Connection c=DBConnection.getConnection()){c.setAutoCommit(false);try{
            JobDAOImpl.lockOwned(c,actor,role,job);
            JobDAOImpl.update(c,"INSERT INTO vacante_documentos(job_id,title,storage_name) VALUES(?,?,?)",job,title,stored);c.commit();
        }catch(SQLException|RuntimeException e){c.rollback();throw e;}}
    }
    public String download(int actor,String role,int document)throws SQLException {
        var result=JobDAOImpl.rows("SELECT job_id,storage_name FROM vacante_documentos WHERE document_id=?",document);
        if(result.isEmpty())throw new IllegalArgumentException("Documento inexistente.");
        new JobDAOImpl().find(actor,role,((Number)result.get(0).get("job_id")).intValue());
        return (String)result.get(0).get("storage_name");
    }
    public String removeDocument(int actor,String role,int job,int document)throws Exception {
        String stored;
        try(Connection c=DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                JobDAOImpl.lockOwned(c,actor,role,job);
                var result=JobDAOImpl.rows(c,"SELECT storage_name FROM vacante_documentos WHERE document_id=? AND job_id=?",document,job);
                if(result.isEmpty())throw new IllegalArgumentException("Documento inexistente.");
                stored=(String)result.get(0).get("storage_name");
                JobDAOImpl.update(c,"DELETE FROM vacante_documentos WHERE document_id=? AND job_id=?",document,job);c.commit();
            }catch(Exception e){c.rollback();throw e;}
        }
        return stored;
    }
    public List<Map<String,Object>> companies()throws SQLException {
        return JobDAOImpl.rows("SELECT e.*,u.email,u.status FROM empresas e JOIN usuarios u ON u.user_id=e.user_id WHERE u.role='company' ORDER BY u.status='pending' DESC,e.company_name");
    }
    public List<Map<String,Object>> reviews()throws SQLException {
        return JobDAOImpl.rows("SELECT r.*,e.company_name FROM empresa_revisiones r JOIN empresas e ON e.company_id=r.company_id ORDER BY r.review_id DESC LIMIT 100");
    }
    public com.cvmanager.models.CompanyReview review(int actor,int company,String decision,String reason)throws SQLException {
        try(Connection c=DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                var companies=JobDAOImpl.rows(c,"SELECT e.company_name FROM empresas e JOIN usuarios u ON u.user_id=e.user_id WHERE e.company_id=? AND u.role='company' FOR UPDATE",company);
                if(companies.isEmpty())throw new IllegalArgumentException("Empresa inexistente.");
                JobDAOImpl.update(c,"UPDATE usuarios u JOIN empresas e ON e.user_id=u.user_id SET u.status=? WHERE e.company_id=?",decision,company);
                JobDAOImpl.update(c,"INSERT INTO empresa_revisiones(company_id,actor_id,decision,reason) VALUES(?,?,?,?)",company,actor,decision,reason);
                String reviewedAt=String.valueOf(JobDAOImpl.rows(c,"SELECT created_at FROM empresa_revisiones WHERE company_id=? ORDER BY review_id DESC LIMIT 1",company).get(0).get("created_at"));
                c.commit();
                return new com.cvmanager.models.CompanyReview(company,(String)companies.get(0).get("company_name"),decision,reason,reviewedAt);
            }catch(SQLException|RuntimeException e){c.rollback();throw e;}
        }
    }
    public List<Map<String,Object>> report(String kind,String start,String end)throws SQLException {
        String query=switch(Objects.toString(kind,"jobs")) {
            case "jobs" -> "SELECT j.title AS Convocatoria,e.company_name AS Empresa,j.created_at AS Registro,j.closing_date AS Cierre,COUNT(a.application_id) AS Postulaciones FROM vacantes j JOIN empresas e ON e.company_id=j.company_id LEFT JOIN postulaciones a ON a.job_id=j.job_id WHERE j.created_at>=? AND j.created_at<DATE_ADD(?,INTERVAL 1 DAY) GROUP BY j.job_id,j.title,e.company_name,j.created_at,j.closing_date ORDER BY j.created_at DESC";
            case "companies" -> "SELECT e.company_name AS Empresa,COUNT(DISTINCT a.graduate_id) AS Postulantes,COUNT(*) AS Postulaciones FROM postulaciones a JOIN vacantes j ON j.job_id=a.job_id JOIN empresas e ON e.company_id=j.company_id WHERE a.created_at>=? AND a.created_at<DATE_ADD(?,INTERVAL 1 DAY) AND a.status<>'withdrawn' GROUP BY e.company_id,e.company_name ORDER BY Postulantes DESC";
            case "careers" -> "SELECT COALESCE(c.name,'Sin carrera') AS Carrera,COUNT(DISTINCT a.graduate_id) AS Postulantes,COUNT(*) AS Postulaciones FROM postulaciones a JOIN egresados g ON g.graduate_id=a.graduate_id LEFT JOIN carreras c ON c.career_id=g.career_id WHERE a.created_at>=? AND a.created_at<DATE_ADD(?,INTERVAL 1 DAY) AND a.status<>'withdrawn' GROUP BY c.career_id,c.name ORDER BY Postulantes DESC";
            case "applicants" -> "SELECT CONCAT(g.first_name,' ',g.last_name) AS Postulante,j.title AS Convocatoria,e.company_name AS Empresa,a.status AS Estado,a.created_at AS Fecha FROM postulaciones a JOIN egresados g ON g.graduate_id=a.graduate_id JOIN vacantes j ON j.job_id=a.job_id JOIN empresas e ON e.company_id=j.company_id WHERE a.created_at>=? AND a.created_at<DATE_ADD(?,INTERVAL 1 DAY) ORDER BY a.created_at DESC";
            default -> throw new IllegalArgumentException("Reporte inválido.");
        };
        return JobDAOImpl.rows(query,start,end);
    }
}
