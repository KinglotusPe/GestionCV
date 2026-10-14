package com.cvmanager.models;
import java.util.*;
public final class CVSectionDefinition {
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
}
