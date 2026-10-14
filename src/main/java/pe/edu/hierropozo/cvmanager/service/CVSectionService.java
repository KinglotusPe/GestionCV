package pe.edu.hierropozo.cvmanager.service;
import java.sql.*;
import java.util.*;
import pe.edu.hierropozo.cvmanager.config.DBConnection;
import pe.edu.hierropozo.cvmanager.util.ValidationUtil;

public class CVSectionService {
    public record Field(String name,String column,String label,int max,String kind,boolean required) {}
    public record Section(String table,String id,List<Field> fields) {}
    private static final Map<String,Section> SECTIONS=Map.ofEntries(
Map.entry("education",new Section("educacion","education_id",List.of(new Field("institution","institution","Institución",150,"text",true),
new Field("degree","degree","Grado o carrera",120,"text",true),
new Field("fieldOfStudy","field_of_study","Campo de estudio",120,"text",false),
new Field("startDate","start_date","Fecha de inicio",10,"date",false),
new Field("endDate","end_date","Fecha final",10,"date",false),
new Field("description","description","Descripción",5000,"textarea",false)))),
Map.entry("experience",new Section("experiencia","experience_id",List.of(new Field("companyName","company_name","Empresa",150,"text",true),
new Field("position","position","Cargo",120,"text",true),
new Field("startDate","start_date","Fecha de inicio",10,"date",false),
new Field("endDate","end_date","Fecha final",10,"date",false),
new Field("responsibilities","responsibilities","Responsabilidades",5000,"textarea",false)))),
Map.entry("skill",new Section("habilidades","skill_id",List.of(new Field("skillName","skill_name","Habilidad",100,"text",true),
new Field("category","skill_category","Categoría",20,"category",true),
new Field("proficiencyLevel","proficiency_level","Nivel de dominio",1,"number",true)))),
Map.entry("language",new Section("idiomas","language_id",List.of(new Field("languageName","language_name","Idioma",80,"text",true),
new Field("proficiencyLevel","proficiency_level","Dominio",20,"language",true)))),
Map.entry("certification",new Section("certificaciones","certification_id",List.of(new Field("name","name","Certificación",150,"text",true),
new Field("organization","issuing_organization","Entidad emisora",150,"text",false),
new Field("issueDate","issue_date","Fecha de emisión",10,"date",false)))),
Map.entry("reference",new Section("referencias","reference_id",List.of(new Field("fullName","full_name","Nombre completo",150,"text",true),
new Field("position","position","Cargo",120,"text",false),
new Field("companyName","company_name","Empresa",150,"text",false),
new Field("phone","phone","Teléfono",30,"tel",false),
new Field("email","email","Correo",150,"email",false)))));
    public static Section definition(String type) {
        Section s=SECTIONS.get(type);
        if(s==null)throw new IllegalArgumentException("Sección inválida.");
        return s;
    }
    public static List<Object> validate(String type,Map<String,String> params) {
        List<Object> values=new ArrayList<>();
        for(Field f:definition(type).fields()) {
            String value=ValidationUtil.text(params.get(f.name()),f.label(),f.max(),f.required());
            Object parsed=value;
            switch(f.kind()) {
                case "date" -> parsed=value.isEmpty()?null:java.sql.Date.valueOf(value);
                case "number" -> parsed=ValidationUtil.integer(value,f.label(),1,5);
                case "category" -> {if(!Set.of("technical","soft","other").contains(value))throw new IllegalArgumentException("Categoría inválida.");}
                case "language" -> {if(!Set.of("basic","intermediate","advanced","native").contains(value))throw new IllegalArgumentException("Nivel de idioma inválido.");}
                case "email" -> {if(!value.isEmpty()&&!ValidationUtil.email(value))throw new IllegalArgumentException("Correo inválido.");}
                case "tel" -> {if(!value.isEmpty()&&!ValidationUtil.phone(value))throw new IllegalArgumentException("Teléfono inválido.");}
            }
            values.add(parsed);
        }
        ValidationUtil.dates(params.get("startDate"),params.get("endDate"));
        return values;
    }
    public void save(int cvId,String type,Integer id,Map<String,String> params) throws Exception {
        Section section=definition(type);
        List<Object> values=validate(type,params);
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
