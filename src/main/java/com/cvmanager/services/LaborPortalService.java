package com.cvmanager.services;

import java.sql.*;
import java.util.*;
import com.cvmanager.dao.interfaces.LaborPortalDAO;
import com.cvmanager.dao.impl.LaborPortalDAOImpl;
import com.cvmanager.dao.interfaces.JobDAO;
import com.cvmanager.dao.impl.JobDAOImpl;
import com.cvmanager.utils.*;

/** Reglas de la bolsa laboral: vigencia, administración, archivos y aprobación. */
public class LaborPortalService {
    public static Map<String,String> validate(Map<String,String> input) {
        Map<String,String> p=new HashMap<>(input);
        for(String field:List.of("title","description","city","salary","requirements","benefits","conditions"))
            p.put(field,ValidacionUtil.text(p.get(field),switch(field){case "title"->"Título";case "city"->"Lugar de trabajo";default->field;},switch(field){case "title"->150;case "city"->80;case "salary"->100;default->10000;},Set.of("title","description","city").contains(field)));
        if(!Set.of("onsite","remote","hybrid").contains(Objects.toString(p.get("modality"),""))||!Set.of("open","closed").contains(Objects.toString(p.get("status"),"")))throw new IllegalArgumentException("Modalidad o estado inválidos.");
        p.put("shift",ValidacionUtil.text(p.getOrDefault("shift","Completo"),"Turno",40,true));
        p.put("positions",String.valueOf(ValidacionUtil.integer(p.getOrDefault("positions","1"),"Vacantes",1,10000)));
        ValidacionUtil.dates(p.get("openingDate"),p.get("closingDate"));
        p.put("externalUrl",ValidacionUtil.url(p.get("externalUrl")));
        return p;
    }
    public void save(int actor,String role,Integer id,Map<String,String> input,String[] selected)throws SQLException {
        if(!Set.of("company","admin").contains(role))throw new IllegalArgumentException("No puede administrar convocatorias.");
        List<Integer> careers=new ArrayList<>();
        if(selected!=null)for(String value:selected)if(!value.isBlank())careers.add(ValidacionUtil.integer(value,"Carrera",1,Integer.MAX_VALUE));
        new JobDAOImpl().save(actor,role,id,validate(input),careers.stream().distinct().toList());
    }
    private final LaborPortalDAO dao=new LaborPortalDAOImpl();
    public void archive(int actor,String role,int job,boolean archive)throws SQLException{dao.archive(actor,role,job,archive);}
    public List<Map<String,Object>> documents(int actor,String role,int job)throws SQLException{return dao.documents(actor,role,job);}
    public void addDocument(int actor,String role,int job,String title,byte[] bytes)throws Exception {
        title=ValidacionUtil.text(title,"Título del documento",150,true);
        String stored=ArchivoUtil.save("pdf",bytes);boolean saved=false;
        try{dao.addDocument(actor,role,job,title,stored);saved=true;}finally{if(!saved)ArchivoUtil.delete("pdf",stored);}
    }
    public String download(int actor,String role,int document)throws SQLException{return dao.download(actor,role,document);}
    public void removeDocument(int actor,String role,int job,int document)throws Exception{ArchivoUtil.delete("pdf",dao.removeDocument(actor,role,job,document));}
    public List<Map<String,Object>> companies()throws SQLException{return dao.companies();}
    public List<Map<String,Object>> reviews()throws SQLException{return dao.reviews();}
    public com.cvmanager.models.CompanyReview review(int actor,int company,String decision,String reason)throws SQLException{
        if(!Set.of("active","inactive","suspended").contains(Objects.toString(decision,"")))throw new IllegalArgumentException("Decisión inválida.");
        return dao.review(actor,company,decision,ValidacionUtil.text(reason,"Sustento",1000,true));
    }
    public List<Map<String,Object>> report(String kind,String start,String end)throws SQLException{
        ValidacionUtil.dates(start,end);
        if(ValidacionUtil.clean(start).isEmpty()||ValidacionUtil.clean(end).isEmpty())throw new IllegalArgumentException("Seleccione ambas fechas del reporte.");
        return dao.report(kind,start,end);
    }
}
