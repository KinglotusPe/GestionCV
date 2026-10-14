package com.cvmanager.dao.impl;
import com.cvmanager.dao.interfaces.*;
import java.sql.*;
import java.util.*;
import com.cvmanager.utils.DBConnection;
import com.cvmanager.utils.ValidacionUtil;

public class ApplicationDAOImpl implements ApplicationDAO {
    public List<Map<String,Object>> applications(int userId,boolean company)throws SQLException {
        return rows("SELECT a.*,j.title,j.city AS job_city,e.company_name,e.phone AS company_phone,e.website AS company_website,eu.email AS company_email,g.first_name,g.last_name,cv.cv_id,"+
            "CASE WHEN a.status <> 'withdrawn' AND gu.status='active' AND gu.role='graduate' THEN gu.email END AS email,"+
            "CASE WHEN a.status <> 'withdrawn' AND gu.status='active' AND gu.role='graduate' THEN g.phone END AS phone "+
            "FROM postulaciones a JOIN vacantes j ON j.job_id=a.job_id JOIN empresas e ON e.company_id=j.company_id JOIN usuarios eu ON eu.user_id=e.user_id JOIN egresados g ON g.graduate_id=a.graduate_id JOIN usuarios gu ON gu.user_id=g.user_id JOIN cv ON cv.graduate_id=g.graduate_id WHERE "+
            (company?"e.user_id=?":"g.user_id=?")+" ORDER BY a.created_at DESC",userId);
    }
    public void apply(int userId,int jobId,String message)throws SQLException {
        // Lock the vacancy, serializing applications with closing the offer.
        try(Connection c=DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                try(PreparedStatement st=c.prepareStatement("SELECT j.job_id FROM vacantes j JOIN empresas e ON e.company_id=j.company_id JOIN usuarios u ON u.user_id=e.user_id LEFT JOIN vacante_detalles d ON d.job_id=j.job_id WHERE j.job_id=? AND COALESCE(d.archived,0)=0 AND (d.opening_date IS NULL OR d.opening_date<=CURRENT_DATE) AND j.status='open' AND (j.closing_date IS NULL OR j.closing_date>=CURRENT_DATE) AND u.status='active' AND u.role='company' FOR UPDATE")) {
                    st.setInt(1,jobId);try(ResultSet rs=st.executeQuery()){if(!rs.next())throw new IllegalArgumentException("La vacante está cerrada o no disponible.");}
                }
                try(PreparedStatement st=c.prepareStatement("INSERT INTO postulaciones(job_id,graduate_id,message) SELECT ?,graduate_id,? FROM egresados WHERE user_id=?")) {
                    st.setInt(1,jobId);st.setString(2,message);st.setInt(3,userId);if(st.executeUpdate()!=1)throw new IllegalArgumentException("Perfil inexistente.");
                }
                c.commit();
            }catch(SQLException|RuntimeException e){c.rollback();throw e;}
        }
    }
    public void respond(int userId,boolean company,int id,String status)throws SQLException {
        String sql=company?"UPDATE postulaciones a JOIN vacantes j ON j.job_id=a.job_id JOIN empresas e ON e.company_id=j.company_id SET a.status=? WHERE a.application_id=? AND e.user_id=? AND a.status<>'withdrawn'":
            "UPDATE postulaciones a JOIN egresados g ON g.graduate_id=a.graduate_id SET a.status=? WHERE a.application_id=? AND g.user_id=?";
        try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement(sql)) {
            st.setString(1,status);st.setInt(2,id);st.setInt(3,userId);if(st.executeUpdate()!=1)throw new IllegalArgumentException("La postulación no está disponible.");
        }
    }
    public boolean canReadCv(int companyUserId,int cvId)throws SQLException {
        return !rows("SELECT cv.cv_id FROM cv JOIN egresados g ON g.graduate_id=cv.graduate_id JOIN usuarios u ON u.user_id=g.user_id JOIN postulaciones a ON a.graduate_id=g.graduate_id JOIN vacantes j ON j.job_id=a.job_id JOIN empresas e ON e.company_id=j.company_id WHERE cv.cv_id=? AND e.user_id=? AND a.status<>'withdrawn' AND u.status='active' AND u.role='graduate'",cvId,companyUserId).isEmpty();
    }
    public static List<Map<String,Object>> rows(String sql,Object... values)throws SQLException {
        try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement(sql)) {
            for(int i=0;i<values.length;i++)st.setObject(i+1,values[i]);
            try(ResultSet rs=st.executeQuery()) {
                List<Map<String,Object>> rows=new ArrayList<>();
                while(rs.next()){Map<String,Object> row=new LinkedHashMap<>();for(int i=1;i<=rs.getMetaData().getColumnCount();i++)row.put(rs.getMetaData().getColumnLabel(i),rs.getObject(i));rows.add(row);}return rows;
            }
        }
    }
}
