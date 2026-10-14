package pe.edu.hierropozo.cvmanager.service;
import java.sql.*;
import java.security.*;
import java.util.*;
import pe.edu.hierropozo.cvmanager.config.DBConnection;
import pe.edu.hierropozo.cvmanager.util.*;

public class PasswordService {
    public static String tokenHash(String token) {
        try {return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8)));}
        catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}
    }
    public String issue(int userId)throws Exception {
        byte[] bytes=new byte[32];new SecureRandom().nextBytes(bytes);
        String token=HexFormat.of().formatHex(bytes);
        try(Connection c=DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                try(PreparedStatement lock=c.prepareStatement("SELECT user_id FROM usuarios WHERE user_id=? FOR UPDATE")){lock.setInt(1,userId);try(ResultSet rs=lock.executeQuery()){if(!rs.next())throw new IllegalArgumentException("Usuario inexistente.");}}
                try(PreparedStatement st=c.prepareStatement("DELETE FROM password_resets WHERE user_id=?")){st.setInt(1,userId);st.executeUpdate();}
                try(PreparedStatement st=c.prepareStatement("INSERT INTO password_resets(token_hash,user_id,expires_at) VALUES(?,?,DATE_ADD(NOW(),INTERVAL 30 MINUTE))")){st.setString(1,tokenHash(token));st.setInt(2,userId);st.executeUpdate();}
                c.commit();
            }catch(Exception e){c.rollback();throw e;}
        }
        return token;
    }
    public String change(int userId,String current,String password)throws Exception {
        if(!ValidationUtil.password(password))throw new IllegalArgumentException("Use al menos 8 caracteres y como máximo 72 bytes para la contraseña.");
        try(Connection c=DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                try(PreparedStatement st=c.prepareStatement("SELECT password_hash FROM usuarios WHERE user_id=? FOR UPDATE")) {
                    st.setInt(1,userId);try(ResultSet rs=st.executeQuery()){if(!rs.next()||!PasswordUtil.verify(current,rs.getString(1)))throw new IllegalArgumentException("Contraseña actual incorrecta.");}
                }
                String hash=PasswordUtil.hash(password);update(c,userId,hash);c.commit();return hash;
            }catch(Exception e){c.rollback();throw e;}
        }
    }
    public void reset(String token,String password)throws Exception {
        if(token==null||!token.matches("[a-f0-9]{64}")||!ValidationUtil.password(password))throw new IllegalArgumentException("Enlace o contraseña inválidos.");
        try(Connection c=DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                int id;
                try(PreparedStatement st=c.prepareStatement("SELECT user_id FROM password_resets WHERE token_hash=? AND expires_at>NOW()")) {
                    st.setString(1,tokenHash(token));try(ResultSet rs=st.executeQuery()){if(!rs.next())throw new IllegalArgumentException("Enlace expirado o ya utilizado.");id=rs.getInt(1);}
                }
                // Lock user first, matching issue/change lock order, then consume exactly once.
                try(PreparedStatement st=c.prepareStatement("SELECT user_id FROM usuarios WHERE user_id=? FOR UPDATE")){st.setInt(1,id);try(ResultSet rs=st.executeQuery()){if(!rs.next())throw new IllegalArgumentException("Usuario inexistente.");}}
                try(PreparedStatement st=c.prepareStatement("DELETE FROM password_resets WHERE token_hash=? AND expires_at>NOW()")){st.setString(1,tokenHash(token));if(st.executeUpdate()!=1)throw new IllegalArgumentException("Enlace expirado o ya utilizado.");}
                update(c,id,PasswordUtil.hash(password));c.commit();
            }catch(Exception e){c.rollback();throw e;}
        }
    }
    private void update(Connection c,int id,String hash)throws SQLException {
        try(PreparedStatement st=c.prepareStatement("UPDATE usuarios SET password_hash=? WHERE user_id=?")){st.setString(1,hash);st.setInt(2,id);st.executeUpdate();}
        try(PreparedStatement st=c.prepareStatement("DELETE FROM password_resets WHERE user_id=?")){st.setInt(1,id);st.executeUpdate();}
    }
}
