package com.cvmanager.models;
import java.util.*;
/** Datos tipados de la sección education; admite fechas opcionales nulas. */
public record Educación(String institution, String degree, String fieldOfStudy, java.sql.Date startDate, java.sql.Date endDate, String description) {
 public List<Object> values() { return Arrays.asList(institution, degree, fieldOfStudy, startDate, endDate, description); }
 public static Educación fromValidated(List<Object> values) {return new Educación((String) values.get(0), (String) values.get(1), (String) values.get(2), (java.sql.Date) values.get(3), (java.sql.Date) values.get(4), (String) values.get(5));}
}
