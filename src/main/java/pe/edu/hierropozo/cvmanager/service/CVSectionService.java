package pe.edu.hierropozo.cvmanager.service;
import java.util.*;
import pe.edu.hierropozo.cvmanager.model.CVSectionDefinition;
import pe.edu.hierropozo.cvmanager.model.CVSectionDefinition.*;
import pe.edu.hierropozo.cvmanager.util.ValidationUtil;
import pe.edu.hierropozo.cvmanager.dao.CVSectionDAO;
public class CVSectionService {
 public static Section definition(String type){return CVSectionDefinition.definition(type);}
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
 public void save(int cvId,String type,Integer id,Map<String,String> params)throws Exception {new CVSectionDAO().save(cvId,type,id,validate(type,params));}
}
