package com.cvmanager.utils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FileStorageTest {
    @Test void preventsPathTraversal(){assertThrows(IllegalArgumentException.class,()->ArchivoUtil.resolve("pdf","../../config.properties"));}
    @Test void rejectsEmptyUploads(){assertThrows(IllegalArgumentException.class,()->ArchivoUtil.save("photo",new byte[0]));}
    @Test void rejectsForgedImages(){assertThrows(IllegalArgumentException.class,()->ArchivoUtil.save("photo","<script>alert(1)</script>".getBytes()));}
    @Test void rejectsForgedPdf(){assertThrows(IllegalArgumentException.class,()->ArchivoUtil.save("pdf","This is not a PDF".getBytes()));}
    @Test void rejectsOversizeUploads(){assertThrows(IllegalArgumentException.class,()->ArchivoUtil.save("pdf",new byte[5*1024*1024+1]));}
}
