package com.cvmanager.services;
import java.util.*;
import com.cvmanager.models.CVSectionDefinition;
import com.cvmanager.models.CVSectionDefinition.*;
import com.cvmanager.utils.ValidacionUtil;
import com.cvmanager.dao.interfaces.CVSectionDAO;
import com.cvmanager.dao.impl.CVSectionDAOImpl;
public class CVService {
 public static Section definition(String type){return CVSectionDefinition.definition(type);}
    public static List<Object> validate(String type,Map<String,String> params) {
        List<Object> values=new ArrayList<>();
        for(Field f:definition(type).fields()) {
            String value=ValidacionUtil.text(params.get(f.name()),f.label(),f.max(),f.required());
            Object parsed=value;
            switch(f.kind()) {
                case "date" -> parsed=value.isEmpty()?null:java.sql.Date.valueOf(value);
                case "number" -> parsed=ValidacionUtil.integer(value,f.label(),1,5);
                case "category" -> {if(!Set.of("technical","soft","other").contains(value))throw new IllegalArgumentException("Categoría inválida.");}
                case "language" -> {if(!Set.of("basic","intermediate","advanced","native").contains(value))throw new IllegalArgumentException("Nivel de idioma inválido.");}
                case "email" -> {if(!value.isEmpty()&&!ValidacionUtil.email(value))throw new IllegalArgumentException("Correo inválido.");}
                case "tel" -> {if(!value.isEmpty()&&!ValidacionUtil.phone(value))throw new IllegalArgumentException("Teléfono inválido.");}
            }
            values.add(parsed);
        }
        ValidacionUtil.dates(params.get("startDate"),params.get("endDate"));
        return values;
    }
 public void save(int cvId,String type,Integer id,Map<String,String> params)throws Exception {
 var values=validate(type,params);
 switch(type) {
 case "education" -> new com.cvmanager.dao.impl.EducaciónDAOImpl().save(cvId,id,com.cvmanager.models.Educación.fromValidated(values));
 case "experience" -> new com.cvmanager.dao.impl.ExperienciaDAOImpl().save(cvId,id,com.cvmanager.models.Experiencia.fromValidated(values));
 case "skill" -> new com.cvmanager.dao.impl.HabilidadesDAOImpl().save(cvId,id,com.cvmanager.models.Habilidades.fromValidated(values));
 case "language" -> new com.cvmanager.dao.impl.IdiomaDAOImpl().save(cvId,id,com.cvmanager.models.Idioma.fromValidated(values));
 case "certification" -> new com.cvmanager.dao.impl.CertificacionDAOImpl().save(cvId,id,com.cvmanager.models.Certificacion.fromValidated(values));
 default -> new CVSectionDAOImpl().save(cvId,type,id,values);
 }
}
}
