package com.cvmanager.models;
import java.util.*;
/** Datos tipados de la sección language; admite fechas opcionales nulas. */
public record Idioma(String languageName, String proficiencyLevel) {
 public List<Object> values() { return Arrays.asList(languageName, proficiencyLevel); }
 public static Idioma fromValidated(List<Object> values) {return new Idioma((String) values.get(0), (String) values.get(1));}
}
