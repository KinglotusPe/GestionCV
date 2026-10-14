package com.cvmanager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.*;
import java.util.regex.Pattern;
class ArchitectureTest {
 @Test void jspIncludesResolveAfterReorganization()throws Exception {
  Path web=Path.of("src/main/webapp");
  var include=Pattern.compile("<%@\\s*include\\s+file=\"([^\"]+)\"");
  try(var files=Files.walk(web)) {
   for(Path file:files.filter(p->p.toString().endsWith(".jsp")||p.toString().endsWith(".jspf")).toList()) {
    var matcher=include.matcher(Files.readString(file));
    while(matcher.find()) {
     String target=matcher.group(1);
     Path resolved=target.startsWith("/")?web.resolve(target.substring(1)):file.getParent().resolve(target);
     assertTrue(Files.isRegularFile(resolved.normalize()),file+" incluye un archivo inexistente: "+target);
    }
   }
  }
 }
 @Test void controllersAndServicesDoNotEmbedSql()throws Exception {
  var sql=Pattern.compile("(?i)\\b(SELECT\\s+.+?\\s+FROM|INSERT\\s+INTO|UPDATE\\s+.+?\\s+SET|DELETE\\s+FROM)\\b|prepareStatement\\(");
  for(String layer:new String[]{"controllers","services"})try(var files=Files.list(Path.of("src/main/java/com/cvmanager/"+layer))){
   for(Path file:files.filter(p->p.toString().endsWith(".java")).toList())assertFalse(sql.matcher(Files.readString(file)).find(),"SQL fuera de DAO: "+file);
  }
 }
}
