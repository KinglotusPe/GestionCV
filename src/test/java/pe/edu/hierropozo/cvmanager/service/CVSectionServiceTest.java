package pe.edu.hierropozo.cvmanager.service;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class CVSectionServiceTest {
    @Test void rejectsUntrustedSectionNames(){assertThrows(IllegalArgumentException.class,()->CVSectionService.definition("usuarios"));}
    @Test void requiresEducationInstitution(){assertThrows(IllegalArgumentException.class,()->CVSectionService.validate("education",Map.of("degree","Técnico")));}
    @Test void rejectsReversedDates(){assertThrows(IllegalArgumentException.class,()->CVSectionService.validate("experience",Map.of("companyName","Empresa","position","Técnico","startDate","2026-02-01","endDate","2025-01-01")));}
    @Test void rejectsInvalidSkillLevel(){assertThrows(IllegalArgumentException.class,()->CVSectionService.validate("skill",Map.of("skillName","Java","category","technical","proficiencyLevel","9")));}
    @Test void rejectsUnknownLanguageLevel(){assertThrows(IllegalArgumentException.class,()->CVSectionService.validate("language",Map.of("languageName","Inglés","proficiencyLevel","expert")));}
    @Test void acceptsEmptyOptionalDates(){assertEquals(5,CVSectionService.validate("experience",Map.of("companyName","Empresa","position","Técnico")).size());}
    @Test void rejectsInvalidReferenceEmail(){assertThrows(IllegalArgumentException.class,()->CVSectionService.validate("reference",Map.of("fullName","Contacto","email","no-es-correo")));}
}
