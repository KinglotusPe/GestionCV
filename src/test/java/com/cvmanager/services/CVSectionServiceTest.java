package com.cvmanager.services;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class CVSectionServiceTest {
    @Test void rejectsUntrustedSectionNames(){assertThrows(IllegalArgumentException.class,()->CVService.definition("usuarios"));}
    @Test void requiresEducationInstitution(){assertThrows(IllegalArgumentException.class,()->CVService.validate("education",Map.of("degree","Técnico")));}
    @Test void rejectsReversedDates(){assertThrows(IllegalArgumentException.class,()->CVService.validate("experience",Map.of("companyName","Empresa","position","Técnico","startDate","2026-02-01","endDate","2025-01-01")));}
    @Test void rejectsInvalidSkillLevel(){assertThrows(IllegalArgumentException.class,()->CVService.validate("skill",Map.of("skillName","Java","category","technical","proficiencyLevel","9")));}
    @Test void rejectsUnknownLanguageLevel(){assertThrows(IllegalArgumentException.class,()->CVService.validate("language",Map.of("languageName","Inglés","proficiencyLevel","expert")));}
    @Test void acceptsEmptyOptionalDates(){assertEquals(5,CVService.validate("experience",Map.of("companyName","Empresa","position","Técnico")).size());}
    @Test void rejectsInvalidReferenceEmail(){assertThrows(IllegalArgumentException.class,()->CVService.validate("reference",Map.of("fullName","Contacto","email","no-es-correo")));}
}
