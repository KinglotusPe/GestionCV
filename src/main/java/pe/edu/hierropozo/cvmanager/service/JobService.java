package pe.edu.hierropozo.cvmanager.service;
import java.sql.*;
import java.util.*;
import pe.edu.hierropozo.cvmanager.config.DBConnection;
import pe.edu.hierropozo.cvmanager.util.ValidationUtil;

public class JobService {
    public List<Map<String,Object>> list(int userId,boolean company,String query)throws SQLException {
        String sql="SELECT j.*,e.company_name,c.name AS career FROM vacantes j JOIN empresas e ON e.company_id=j.company_id JOIN usuarios u ON u.user_id=e.user_id LEFT JOIN carreras c ON c.career_id=j.career_id WHERE "+
            (company?"e.user_id=?":"u.status='active' AND u.role='company' AND j.status='open' AND (j.closing_date IS NULL OR j.closing_date>=CURRENT_DATE) AND ? > 0")+
            " AND (j.title LIKE ? OR j.description LIKE ? OR e.company_name LIKE ?) ORDER BY j.created_at DESC,j.job_id DESC";
        return rows(sql,userId,"%"+ValidationUtil.clean(query)+"%","%"+ValidationUtil.clean(query)+"%","%"+ValidationUtil.clean(query)+"%");
    }
    public List<Map<String,Object>> applications(int userId,boolean company)throws SQLException {
        return rows("SELECT a.*,j.title,e.company_name,g.first_name,g.last_name,cv.cv_id,"+
            "CASE WHEN a.status <> 'withdrawn' AND gu.status='active' AND gu.role='graduate' THEN gu.email END AS email,"+
            "CASE WHEN a.status <> 'withdrawn' AND gu.status='active' AND gu.role='graduate' THEN g.phone END AS phone "+
            "FROM postulaciones a JOIN vacantes j ON j.job_id=a.job_id JOIN empresas e ON e.company_id=j.company_id JOIN egresados g ON g.graduate_id=a.graduate_id JOIN usuarios gu ON gu.user_id=g.user_id JOIN cv ON cv.graduate_id=g.graduate_id WHERE "+
            (company?"e.user_id=?":"g.user_id=?")+" ORDER BY a.created_at DESC",userId);
    }
    public Map<String,Object> findOwn(int userId,int id)throws SQLException {
        var result=rows("SELECT j.* FROM vacantes j JOIN empresas e ON e.company_id=j.company_id WHERE e.user_id=? AND j.job_id=?",userId,id);
        if(result.isEmpty())throw new IllegalArgumentException("La vacante no existe o no pertenece a su empresa.");
        return result.get(0);
    }
    public void save(int userId,Integer id,Map<String,String> p)throws SQLException {
        String title=ValidationUtil.text(p.get("title"),"Título",150,true), description=ValidationUtil.text(p.get("description"),"Descripción",10000,true);
        String city=ValidationUtil.text(p.get("city"),"Ciudad",80,true), salary=ValidationUtil.text(p.get("salary"),"Remuneración",100,false);
        String mode=p.get("modality"),status=p.get("status");
        if(mode==null||!Set.of("onsite","remote","hybrid").contains(mode)||status==null||!Set.of("open","closed").contains(status))throw new IllegalArgumentException("Modalidad o estado inválidos.");
        java.sql.Date closing=ValidationUtil.clean(p.get("closingDate")).isEmpty()?null:java.sql.Date.valueOf(p.get("closingDate"));
        if(status.equals("open")&&closing!=null&&closing.toLocalDate().isBefore(java.time.LocalDate.now()))throw new IllegalArgumentException("La fecha de cierre ya pasó.");
        Integer career=ValidationUtil.clean(p.get("careerId")).isEmpty()?null:ValidationUtil.integer(p.get("careerId"),"Carrera",1,Integer.MAX_VALUE);
        String sql=id==null?"INSERT INTO vacantes(title,description,city,salary,modality,status,closing_date,career_id,company_id) SELECT ?,?,?,?,?,?,?,?,company_id FROM empresas WHERE user_id=?":
            "UPDATE vacantes j JOIN empresas e ON e.company_id=j.company_id SET j.title=?,j.description=?,j.city=?,j.salary=?,j.modality=?,j.status=?,j.closing_date=?,j.career_id=? WHERE e.user_id=? AND j.job_id=?";
        try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement(sql)) {
            Object[] values={title,description,city,salary,mode,status,closing,career,userId};for(int i=0;i<values.length;i++)st.setObject(i+1,values[i]);if(id!=null)st.setInt(10,id);
            if(st.executeUpdate()!=1)throw new IllegalArgumentException("Vacante no disponible.");
        }
    }
    public void apply(int userId,int jobId,String message)throws SQLException {
        message=ValidationUtil.text(message,"Presentación",5000,true);
        // Lock the vacancy, serializing applications with closing the offer.
        try(Connection c=DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                try(PreparedStatement st=c.prepareStatement("SELECT j.job_id FROM vacantes j JOIN empresas e ON e.company_id=j.company_id JOIN usuarios u ON u.user_id=e.user_id WHERE j.job_id=? AND j.status='open' AND (j.closing_date IS NULL OR j.closing_date>=CURRENT_DATE) AND u.status='active' AND u.role='company' FOR UPDATE")) {
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
        if(status==null||!(company?Set.of("pending","reviewing","accepted","rejected"):Set.of("withdrawn")).contains(status))throw new IllegalArgumentException("Estado inválido.");
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
