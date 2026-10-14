package pe.edu.hierropozo.cvmanager.util;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;

public final class FileStorage {
    private FileStorage() {}
    public static Path root() {
        String configured=System.getenv("CVMANAGER_UPLOAD_DIR");
        return Path.of(configured==null?System.getProperty("user.home")+"/cvmanager_uploads":configured).toAbsolutePath().normalize();
    }
    public static Path resolve(String type,String name) {
        if(!Set.of("photo","pdf").contains(type)||name==null||!name.matches("[a-fA-F0-9-]{36}\\.(?:png|jpg|pdf)")) throw new IllegalArgumentException("Archivo inválido.");
        return root().resolve(type.equals("photo")?"photos":"cvs").resolve(name);
    }
    public static String save(String type,byte[] data) throws IOException {
        if(data.length==0||data.length>5*1024*1024) throw new IllegalArgumentException("El archivo debe tener entre 1 byte y 5 MB.");
        String name=UUID.randomUUID()+(type.equals("photo")?".png":".pdf");
        Path path=resolve(type,name); Files.createDirectories(path.getParent());
        if(type.equals("photo")) {
            try(ImageInputStream input=ImageIO.createImageInputStream(new ByteArrayInputStream(data))) {
                Iterator<ImageReader> readers=ImageIO.getImageReaders(input);
                if(!readers.hasNext()) throw new IllegalArgumentException("Seleccione una imagen JPG o PNG válida.");
                ImageReader reader=readers.next();
                try {
                    reader.setInput(input);
                    if(!Set.of("JPEG","PNG").contains(reader.getFormatName().toUpperCase(Locale.ROOT))||(long)reader.getWidth(0)*reader.getHeight(0)>16_000_000) throw new IllegalArgumentException("Use JPG o PNG de hasta 16 megapíxeles.");
                    if(!ImageIO.write(reader.read(0),"png",path.toFile())) throw new IOException("No se pudo guardar la imagen");
                } finally {reader.dispose();}
            } catch(IOException|RuntimeException e) {Files.deleteIfExists(path);throw e;}
        } else {
            String header=new String(data,0,Math.min(data.length,8),java.nio.charset.StandardCharsets.US_ASCII);
            String tail=new String(data,Math.max(0,data.length-2048),Math.min(data.length,2048),java.nio.charset.StandardCharsets.US_ASCII);
            if(!header.startsWith("%PDF-")||!tail.contains("%%EOF")) throw new IllegalArgumentException("El archivo no tiene una estructura PDF válida.");
            Files.write(path,data,StandardOpenOption.CREATE_NEW);
        }
        return name;
    }
    public static void delete(String type,String name) {
        if(name==null)return;
        try {Files.deleteIfExists(resolve(type,name));}
        catch(Exception e) {org.apache.logging.log4j.LogManager.getLogger(FileStorage.class).warn("No se pudo retirar un archivo sustituido",e);}
    }
}
