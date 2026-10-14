package com.cvmanager.utils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProfileValidationTest {
    @Test void optionalDocumentsBecomeNull(){assertNull(ValidacionUtil.nullable("  "));}
    @Test void validatesDni(){assertTrue(ValidacionUtil.dni("12345678"));assertFalse(ValidacionUtil.dni("1234567A"));}
    @Test void validatesPeruvianPhone(){assertTrue(ValidacionUtil.phone("+51 999 123 456"));assertFalse(ValidacionUtil.phone("abc"));}
    @Test void validatesRucChecksum(){assertTrue(ValidacionUtil.ruc("20100047218"));assertFalse(ValidacionUtil.ruc("20100047219"));}
    @Test void rejectsExecutableUrls(){assertThrows(IllegalArgumentException.class,()->ValidacionUtil.url("javascript:alert(1)"));assertEquals("https://example.com",ValidacionUtil.url("https://example.com"));}
    @Test void respectsBcryptByteLimit(){assertFalse(ValidacionUtil.password("á".repeat(37)));assertTrue(ValidacionUtil.password("á".repeat(36)));}
}
