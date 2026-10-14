package com.cvmanager.dao.impl;
import com.cvmanager.dao.interfaces.*;

import java.sql.*;
import java.util.*;
import com.cvmanager.utils.DBConnection;
import com.cvmanager.utils.ValidacionUtil;

/** Persistencia de convocatorias y sus carreras; cada edición es atómica. */
public class JobDAOImpl implements JobDAO {
    private static final String SELECT = "SELECT j.*,e.company_name,d.opening_date,d.shift_name,d.positions,d.requirements,d.benefits,d.conditions_text,d.external_url,d.managed_by,COALESCE(d.archived,0) AS archived, "
        + "CASE WHEN d.archived=1 THEN 'archived' WHEN j.status='closed' OR j.closing_date<CURRENT_DATE THEN 'finished' WHEN d.opening_date>CURRENT_DATE THEN 'upcoming' ELSE 'ongoing' END AS stage, "
        + "(SELECT GROUP_CONCAT(c.name ORDER BY c.name SEPARATOR ', ') FROM vacante_carreras jc JOIN carreras c ON c.career_id=jc.career_id WHERE jc.job_id=j.job_id) AS career "
        + "FROM vacantes j JOIN empresas e ON e.company_id=j.company_id JOIN usuarios u ON u.user_id=e.user_id LEFT JOIN vacante_detalles d ON d.job_id=j.job_id ";

    public List<Map<String,Object>> list(int actor,String role,String query,String stage,String archive) throws SQLException {
        String access=role.equals("admin")?"? > 0":role.equals("company")?"e.user_id=?":"? > 0 AND u.status='active' AND u.role='company' AND COALESCE(d.archived,0)=0";
        String sql=SELECT+"WHERE "+access+" AND (j.title LIKE ? OR j.description LIKE ? OR e.company_name LIKE ?)";
        String q="%"+ValidacionUtil.clean(query)+"%";
        List<Map<String,Object>> result=rows(sql+" ORDER BY j.created_at DESC,j.job_id DESC",actor,q,q,q);
        return result.stream().filter(r-> stage==null||stage.isBlank()||stage.equals(r.get("stage")))
            .filter(r->role.equals("graduate")||("yes".equals(archive)?r.get("stage").equals("archived"):!r.get("stage").equals("archived"))).toList();
    }
    public Map<String,Object> find(int actor,String role,int id) throws SQLException {
        String access=role.equals("admin")?"? > 0":role.equals("company")?"e.user_id=?":"? > 0 AND u.status='active' AND u.role='company' AND COALESCE(d.archived,0)=0";
        var result=rows(SELECT+"WHERE "+access+" AND j.job_id=?",actor,id);
        if(result.isEmpty())throw new IllegalArgumentException("Convocatoria no disponible para esta cuenta.");
        var job=result.get(0);
        job.put("careerIds",rows("SELECT career_id FROM vacante_carreras WHERE job_id=?",id).stream().map(r->((Number)r.get("career_id")).intValue()).toList());
        return job;
    }
    public void save(int actor,String role,Integer id,Map<String,String> p,List<Integer> careers) throws SQLException {
        try(Connection c=DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                int companyId;
                if(id!=null) {
                    var own=lockOwned(c,actor,role,id);
                    companyId=((Number)own.get("company_id")).intValue();
                } else if(role.equals("admin")) {
                    companyId=ValidacionUtil.integer(p.get("companyId"),"Empresa",1,Integer.MAX_VALUE);
                    if(rows(c,"SELECT e.company_id FROM empresas e JOIN usuarios u ON u.user_id=e.user_id WHERE e.company_id=? AND u.role='company' AND u.status='active'",companyId).isEmpty())
                        throw new IllegalArgumentException("Seleccione una empresa activa.");
                } else {
                    var own=rows(c,"SELECT company_id FROM empresas WHERE user_id=?",actor);
                    if(own.isEmpty())throw new IllegalArgumentException("Empresa inexistente.");
                    companyId=((Number)own.get(0).get("company_id")).intValue();
                }
                for(int career:careers)if(rows(c,"SELECT career_id FROM carreras WHERE career_id=? AND (is_active=1 OR EXISTS(SELECT 1 FROM vacante_carreras WHERE job_id=? AND career_id=carreras.career_id))",career,id).isEmpty())throw new IllegalArgumentException("Programa de estudios no disponible.");
                Object[] values={p.get("title"),p.get("description"),p.get("city"),p.get("salary"),p.get("modality"),p.get("status"),nullable(p.get("closingDate")),careers.isEmpty()?null:careers.get(0),companyId};
                if(id==null) {
                    try(PreparedStatement st=c.prepareStatement("INSERT INTO vacantes(title,description,city,salary,modality,status,closing_date,career_id,company_id) VALUES(?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS)) {
                        bind(st,values);st.executeUpdate();try(ResultSet rs=st.getGeneratedKeys()){rs.next();id=rs.getInt(1);}
                    }
                    update(c,"INSERT INTO vacante_detalles(job_id,managed_by) VALUES(?,?)",id,role.equals("admin")?actor:null);
                } else {
                    update(c,"UPDATE vacantes SET title=?,description=?,city=?,salary=?,modality=?,status=?,closing_date=?,career_id=?,company_id=? WHERE job_id=?",append(values,id));
                    update(c,"INSERT IGNORE INTO vacante_detalles(job_id) VALUES(?)",id);
                }
                update(c,"UPDATE vacante_detalles SET opening_date=?,shift_name=?,positions=?,requirements=?,benefits=?,conditions_text=?,external_url=? WHERE job_id=?",
                    nullable(p.get("openingDate")),p.get("shift"),Integer.valueOf(p.get("positions")),p.get("requirements"),p.get("benefits"),p.get("conditions"),nullable(p.get("externalUrl")),id);
                update(c,"DELETE FROM vacante_carreras WHERE job_id=?",id);
                for(int career:careers)update(c,"INSERT INTO vacante_carreras(job_id,career_id) VALUES(?,?)",id,career);
                c.commit();
            }catch(SQLException|RuntimeException ex){c.rollback();throw ex;}
        }
    }
    public static Map<String,Object> lockOwned(Connection c,int actor,String role,int id)throws SQLException {
        var result=rows(c,"SELECT j.company_id,d.managed_by FROM vacantes j JOIN empresas e ON e.company_id=j.company_id LEFT JOIN vacante_detalles d ON d.job_id=j.job_id WHERE j.job_id=? AND "+(role.equals("admin")?"? > 0":"e.user_id=?")+" FOR UPDATE",id,actor);
        if(result.isEmpty())throw new IllegalArgumentException("Convocatoria no disponible.");
        Object manager=result.get(0).get("managed_by");
        if(role.equals("admin")?(manager==null||((Number)manager).intValue()!=actor):manager!=null)
            throw new IllegalArgumentException("Solo quien administra la convocatoria puede editarla.");
        return result.get(0);
    }
    public static Object nullable(String value){return value==null||value.isBlank()?null:value;}
    private static Object[] append(Object[] values,Object last){Object[] result=Arrays.copyOf(values,values.length+1);result[values.length]=last;return result;}
    public static void bind(PreparedStatement st,Object... values)throws SQLException {for(int i=0;i<values.length;i++)st.setObject(i+1,values[i]);}
    public static int update(Connection c,String sql,Object... values)throws SQLException {try(PreparedStatement st=c.prepareStatement(sql)){bind(st,values);return st.executeUpdate();}}
    public static List<Map<String,Object>> rows(String sql,Object... values)throws SQLException {try(Connection c=DBConnection.getConnection()){return rows(c,sql,values);}}
    public static List<Map<String,Object>> rows(Connection c,String sql,Object... values)throws SQLException {
        try(PreparedStatement st=c.prepareStatement(sql)){bind(st,values);try(ResultSet rs=st.executeQuery()){
            List<Map<String,Object>> result=new ArrayList<>();while(rs.next()){Map<String,Object> row=new LinkedHashMap<>();for(int i=1;i<=rs.getMetaData().getColumnCount();i++)row.put(rs.getMetaData().getColumnLabel(i),rs.getObject(i));result.add(row);}return result;
        }}
    }
}
