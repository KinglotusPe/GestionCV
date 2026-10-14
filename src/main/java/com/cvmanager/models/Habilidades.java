package com.cvmanager.models;
import java.util.*;
/** Datos tipados de la sección skill; admite fechas opcionales nulas. */
public record Habilidades(String skillName, String category, Integer proficiencyLevel) {
 public List<Object> values() { return Arrays.asList(skillName, category, proficiencyLevel); }
 public static Habilidades fromValidated(List<Object> values) {return new Habilidades((String) values.get(0), (String) values.get(1), (Integer) values.get(2));}
}
