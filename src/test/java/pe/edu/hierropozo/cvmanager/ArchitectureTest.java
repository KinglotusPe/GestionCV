package pe.edu.hierropozo.cvmanager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.*;
import java.util.regex.Pattern;
class ArchitectureTest {
 @Test void controllersAndServicesDoNotEmbedSql()throws Exception {
  var sql=Pattern.compile("(?i)\\b(SELECT\\s+.+?\\s+FROM|INSERT\\s+INTO|UPDATE\\s+.+?\\s+SET|DELETE\\s+FROM)\\b|prepareStatement\\(");
  for(String layer:new String[]{"controller","service"})try(var files=Files.list(Path.of("src/main/java/pe/edu/hierropozo/cvmanager/"+layer))){
   for(Path file:files.filter(p->p.toString().endsWith(".java")).toList())assertFalse(sql.matcher(Files.readString(file)).find(),"SQL fuera de DAO: "+file);
  }
 }
}
