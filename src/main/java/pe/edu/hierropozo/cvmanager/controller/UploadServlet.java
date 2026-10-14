package pe.edu.hierropozo.cvmanager.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.Set;
import pe.edu.hierropozo.cvmanager.config.DBConnection;
import pe.edu.hierropozo.cvmanager.util.FileStorage;

@WebServlet("/graduate/upload")
@MultipartConfig(fileSizeThreshold=1024*1024,maxFileSize=5*1024*1024,maxRequestSize=6*1024*1024)
public class UploadServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException {
        String type=req.getParameter("type");
        if(type==null||!Set.of("photo","pdf").contains(type))throw new IllegalArgumentException("Tipo de archivo inválido.");
        String newName=null,oldName=null; boolean committed=false;
        try(Connection c=DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                int userId=(Integer)req.getSession().getAttribute("userId");
                try(PreparedStatement st=c.prepareStatement("SELECT g.photo_url, cv.cv_pdf_url FROM egresados g JOIN cv ON cv.graduate_id=g.graduate_id WHERE g.user_id=? FOR UPDATE")) {
                    st.setInt(1,userId);
                    try(ResultSet rs=st.executeQuery()) {
                        if(!rs.next())throw new IllegalArgumentException("Perfil no encontrado.");
                        oldName=rs.getString(type.equals("photo")?"photo_url":"cv_pdf_url");
                    }
                }
                if(!"remove".equals(req.getParameter("action"))) {
                    Part file=req.getPart("file");
                    if(file==null)throw new IllegalArgumentException("Seleccione un archivo.");
                    try(var in=file.getInputStream()){newName=FileStorage.save(type,in.readNBytes(5*1024*1024+1));}
                    finally{file.delete();}
                }
                String sql=type.equals("photo")?"UPDATE egresados SET photo_url=? WHERE user_id=?":"UPDATE cv JOIN egresados g ON g.graduate_id=cv.graduate_id SET cv.cv_pdf_url=? WHERE g.user_id=?";
                try(PreparedStatement st=c.prepareStatement(sql)){st.setString(1,newName);st.setInt(2,userId);st.executeUpdate();}
                c.commit();committed=true;
            } catch(Exception e){c.rollback();throw e;}
        } catch(Exception e){throw new ServletException(e);}
        finally{if(!committed)FileStorage.delete(type,newName);}
        FileStorage.delete(type,oldName);
        res.sendRedirect(req.getContextPath()+"/graduate/"+(type.equals("photo")?"profile":"cv")+"?ok=1");
    }
}
