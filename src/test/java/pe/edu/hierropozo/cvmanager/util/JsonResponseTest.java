package pe.edu.hierropozo.cvmanager.util;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class JsonResponseTest {
 @Test void quotesAndControlsAreEscaped(){assertEquals("\"a\\\"b\\\\c\\n\\t\"",JsonResponse.quote("a\"b\\c\n\t"));}
 @Test void htmlCannotBecomeAnExecutableFragment(){assertEquals("\"\\u003cscript\\u003e\\u0026\"",JsonResponse.quote("<script>&"));}
}
