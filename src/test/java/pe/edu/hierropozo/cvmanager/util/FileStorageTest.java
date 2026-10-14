package pe.edu.hierropozo.cvmanager.util;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FileStorageTest {
    @Test void preventsPathTraversal(){assertThrows(IllegalArgumentException.class,()->FileStorage.resolve("pdf","../../config.properties"));}
    @Test void rejectsEmptyUploads(){assertThrows(IllegalArgumentException.class,()->FileStorage.save("photo",new byte[0]));}
    @Test void rejectsForgedImages(){assertThrows(IllegalArgumentException.class,()->FileStorage.save("photo","<script>alert(1)</script>".getBytes()));}
    @Test void rejectsForgedPdf(){assertThrows(IllegalArgumentException.class,()->FileStorage.save("pdf","This is not a PDF".getBytes()));}
    @Test void rejectsOversizeUploads(){assertThrows(IllegalArgumentException.class,()->FileStorage.save("pdf",new byte[5*1024*1024+1]));}
}
