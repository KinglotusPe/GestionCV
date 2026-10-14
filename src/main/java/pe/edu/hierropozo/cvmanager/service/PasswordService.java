package pe.edu.hierropozo.cvmanager.service;
import java.security.*;
import java.util.*;
import pe.edu.hierropozo.cvmanager.dao.*;
import pe.edu.hierropozo.cvmanager.util.*;
public class PasswordService {
 private final PasswordDAO dao=new PasswordDAO();
    public static String tokenHash(String token) {
        try {return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8)));}
        catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}
    }
 public String issue(int userId)throws Exception{byte[] bytes=new byte[32];new SecureRandom().nextBytes(bytes);String token=HexFormat.of().formatHex(bytes);dao.issue(userId,tokenHash(token));return token;}
 public String change(int userId,String current,String password)throws Exception {
  if(!ValidationUtil.password(password))throw new IllegalArgumentException("Use al menos 8 caracteres y como máximo 72 bytes para la contraseña.");
  var user=new UserDAOImpl().findById(userId);
  if(user==null||!PasswordUtil.verify(current,user.getPasswordHash()))throw new IllegalArgumentException("Contraseña actual incorrecta.");
  String hash=PasswordUtil.hash(password);dao.change(userId,user.getPasswordHash(),hash);return hash;
 }
 public void reset(String token,String password)throws Exception{
  if(token==null||!token.matches("[a-f0-9]{64}")||!ValidationUtil.password(password))throw new IllegalArgumentException("Enlace o contraseña inválidos.");
  dao.reset(tokenHash(token),PasswordUtil.hash(password));
 }
}
