package pe.edu.hierropozo.cvmanager.service;

import java.sql.*;
import java.util.*;
import pe.edu.hierropozo.cvmanager.dao.LaborPortalDAO;
import pe.edu.hierropozo.cvmanager.dao.JobDAO;
import pe.edu.hierropozo.cvmanager.util.*;

/** Reglas de la bolsa laboral: vigencia, administración, archivos y aprobación. */
public class LaborPortalService {
    public static Map<String,String> validate(Map<String,String> input) {
        Map<String,String> p=new HashMap<>(input);
        for(String field:List.of("title","description","city","salary","requirements","benefits","conditions"))
            p.put(field,ValidationUtil.text(p.get(field),switch(field){case "title"->"Título";case "city"->"Lugar de trabajo";default->field;},switch(field){case "title"->150;case "city"->80;case "salary"->100;default->10000;},Set.of("title","description","city").contains(field)));
        if(!Set.of("onsite","remote","hybrid").contains(Objects.toString(p.get("modality"),""))||!Set.of("open","closed").contains(Objects.toString(p.get("status"),"")))throw new IllegalArgumentException("Modalidad o estado inválidos.");
        p.put("shift",ValidationUtil.text(p.getOrDefault("shift","Completo"),"Turno",40,true));
        p.put("positions",String.valueOf(ValidationUtil.integer(p.getOrDefault("positions","1"),"Vacantes",1,10000)));
        ValidationUtil.dates(p.get("openingDate"),p.get("closingDate"));
        p.put("externalUrl",ValidationUtil.url(p.get("externalUrl")));
        return p;
    }
    public void save(int actor,String role,Integer id,Map<String,String> input,String[] selected)throws SQLException {
        if(!Set.of("company","admin").contains(role))throw new IllegalArgumentException("No puede administrar convocatorias.");
        List<Integer> careers=new ArrayList<>();
        if(selected!=null)for(String value:selected)if(!value.isBlank())careers.add(ValidationUtil.integer(value,"Carrera",1,Integer.MAX_VALUE));
        new JobDAO().save(actor,role,id,validate(input),careers.stream().distinct().toList());
    }
    private final LaborPortalDAO dao=new LaborPortalDAO();
    public void archive(int actor,String role,int job,boolean archive)throws SQLException{dao.archive(actor,role,job,archive);}
    public List<Map<String,Object>> documents(int actor,String role,int job)throws SQLException{return dao.documents(actor,role,job);}
    public void addDocument(int actor,String role,int job,String title,byte[] bytes)throws Exception {
        title=ValidationUtil.text(title,"Título del documento",150,true);
        String stored=FileStorage.save("pdf",bytes);boolean saved=false;
        try{dao.addDocument(actor,role,job,title,stored);saved=true;}finally{if(!saved)FileStorage.delete("pdf",stored);}
    }
    public String download(int actor,String role,int document)throws SQLException{return dao.download(actor,role,document);}
    public void removeDocument(int actor,String role,int job,int document)throws Exception{FileStorage.delete("pdf",dao.removeDocument(actor,role,job,document));}
    public List<Map<String,Object>> companies()throws SQLException{return dao.companies();}
    public List<Map<String,Object>> reviews()throws SQLException{return dao.reviews();}
    public pe.edu.hierropozo.cvmanager.model.CompanyReview review(int actor,int company,String decision,String reason)throws SQLException{
        if(!Set.of("active","inactive","suspended").contains(Objects.toString(decision,"")))throw new IllegalArgumentException("Decisión inválida.");
        return dao.review(actor,company,decision,ValidationUtil.text(reason,"Sustento",1000,true));
    }
    public List<Map<String,Object>> report(String kind,String start,String end)throws SQLException{
        ValidationUtil.dates(start,end);
        if(ValidationUtil.clean(start).isEmpty()||ValidationUtil.clean(end).isEmpty())throw new IllegalArgumentException("Seleccione ambas fechas del reporte.");
        return dao.report(kind,start,end);
    }
}
