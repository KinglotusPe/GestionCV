package com.cvmanager.services;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.io.*;
import java.util.zip.*;
import com.cvmanager.utils.SpreadsheetExport;

class LaborPortalValidationTest {
    private Map<String,String> input(){return new HashMap<>(Map.of("title","Puesto","description","Funciones","city","Ayacucho","modality","onsite","status","open"));}
    @Test void rejectsInvertedDates(){var p=input();p.put("openingDate","2026-10-20");p.put("closingDate","2026-10-10");assertThrows(IllegalArgumentException.class,()->LaborPortalService.validate(p));}
    @Test void rejectsUnsafeExternalLink(){var p=input();p.put("externalUrl","javascript:alert(1)");assertThrows(IllegalArgumentException.class,()->LaborPortalService.validate(p));}
    @Test void rejectsZeroPositions(){var p=input();p.put("positions","0");assertThrows(IllegalArgumentException.class,()->LaborPortalService.validate(p));}
    @Test void exportsUserTextWithoutFormulas()throws Exception {
        var bytes=new ByteArrayOutputStream();SpreadsheetExport.write(bytes,List.of(Map.of("Empresa","=HYPERLINK(\"https://example.com\") & <empresa>")));
        String xml=null;try(var zip=new ZipInputStream(new ByteArrayInputStream(bytes.toByteArray()))){for(ZipEntry entry;(entry=zip.getNextEntry())!=null;)if(entry.getName().endsWith("sheet1.xml"))xml=new String(zip.readAllBytes(),java.nio.charset.StandardCharsets.UTF_8);}
        assertNotNull(xml);assertTrue(xml.contains("t=\"inlineStr\""));assertTrue(xml.contains("&amp; &lt;empresa&gt;"));assertFalse(xml.contains("<f>"));
    }
}
