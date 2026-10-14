package pe.edu.hierropozo.cvmanager.dao;
import java.sql.*;
import java.util.*;
import pe.edu.hierropozo.cvmanager.config.DBConnection;
import pe.edu.hierropozo.cvmanager.model.CVSectionDefinition;
import pe.edu.hierropozo.cvmanager.model.CVSectionDefinition.*;
public class CVSectionDAO {
 public void save(int cvId,String type,Integer id,List<Object> validated)throws Exception {
  Section section=CVSectionDefinition.definition(type);
  List<Object> values=new ArrayList<>(validated);
        String sql;
        if(id==null) {
            sql="INSERT INTO "+section.table()+" ("+String.join(",",section.fields().stream().map(Field::column).toList())+",cv_id) VALUES ("+String.join(",",Collections.nCopies(values.size()+1,"?"))+")";
            values.add(cvId);
        } else {
            sql="UPDATE "+section.table()+" SET "+String.join(",",section.fields().stream().map(f->f.column()+"=?").toList())+" WHERE "+section.id()+"=? AND cv_id=?";
            values.add(id);values.add(cvId);
        }
        try(Connection c=DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                try(PreparedStatement st=c.prepareStatement(sql)) {
                    for(int i=0;i<values.size();i++)st.setObject(i+1,values.get(i));
                    if(st.executeUpdate()!=1)throw new IllegalArgumentException("El registro no existe o no pertenece a su CV.");
                }
                try(PreparedStatement st=c.prepareStatement("UPDATE cv SET updated_at=CURRENT_TIMESTAMP WHERE cv_id=?")){st.setInt(1,cvId);st.executeUpdate();}
                c.commit();
            } catch(Exception e){c.rollback();throw e;}
        }
    }
}
