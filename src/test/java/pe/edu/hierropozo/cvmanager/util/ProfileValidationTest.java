package pe.edu.hierropozo.cvmanager.util;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProfileValidationTest {
    @Test void optionalDocumentsBecomeNull(){assertNull(ValidationUtil.nullable("  "));}
    @Test void validatesDni(){assertTrue(ValidationUtil.dni("12345678"));assertFalse(ValidationUtil.dni("1234567A"));}
    @Test void validatesPeruvianPhone(){assertTrue(ValidationUtil.phone("+51 999 123 456"));assertFalse(ValidationUtil.phone("abc"));}
    @Test void validatesRucChecksum(){assertTrue(ValidationUtil.ruc("20100047218"));assertFalse(ValidationUtil.ruc("20100047219"));}
    @Test void rejectsExecutableUrls(){assertThrows(IllegalArgumentException.class,()->ValidationUtil.url("javascript:alert(1)"));assertEquals("https://example.com",ValidationUtil.url("https://example.com"));}
    @Test void respectsBcryptByteLimit(){assertFalse(ValidationUtil.password("á".repeat(37)));assertTrue(ValidationUtil.password("á".repeat(36)));}
}
