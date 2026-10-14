package pe.edu.hierropozo.cvmanager.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.nio.file.*;
import pe.edu.hierropozo.cvmanager.config.DBConnection;
import pe.edu.hierropozo.cvmanager.util.FileStorage;

@WebServlet("/files")
public class FileServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException {
        String type=req.getParameter("type");
        if(!"photo".equals(type)&&!"pdf".equals(type)){res.sendError(400);return;}
        int userId=(Integer)req.getSession().getAttribute("userId");
        String role=(String)req.getSession().getAttribute("role");
        String sql="SELECT g.photo_url, cv.cv_pdf_url FROM cv JOIN egresados g ON g.graduate_id=cv.graduate_id JOIN usuarios u ON u.user_id=g.user_id WHERE cv.cv_id=? AND (g.user_id=? OR (?='company' AND u.role='graduate' AND u.status='active' AND g.is_public=1 AND cv.is_published=1 AND (?='photo' OR EXISTS (SELECT 1 FROM solicitudes_contacto s JOIN empresas e ON e.company_id=s.company_id WHERE s.graduate_id=g.graduate_id AND e.user_id=? AND s.status='accepted'))) OR ?=1)";

        boolean applicationAccess;
        try {
            applicationAccess="company".equals(role)&&new pe.edu.hierropozo.cvmanager.service.JobService().canReadCv(userId,Integer.parseInt(req.getParameter("cvId")));
        } catch(SQLException e){throw new ServletException(e);}
        try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement(sql)) {
            st.setInt(1,Integer.parseInt(req.getParameter("cvId")));st.setInt(2,userId);st.setString(3,role);st.setString(4,type);st.setInt(5,userId);
            st.setInt(6,applicationAccess?1:0);
            String name;
            try(ResultSet rs=st.executeQuery()) {
                if(!rs.next()||(name=rs.getString(type.equals("photo")?"photo_url":"cv_pdf_url"))==null){res.sendError(404);return;}
            }
            Path file=FileStorage.resolve(type,name);
            if(!Files.isRegularFile(file)){res.sendError(404);return;}
            res.setHeader("Content-Security-Policy","sandbox");
            res.setContentType(type.equals("pdf")?"application/pdf":name.endsWith(".png")?"image/png":"image/jpeg");
            res.setHeader("Content-Disposition",type.equals("pdf")?"attachment; filename=curriculum.pdf":"inline");
            res.setContentLengthLong(Files.size(file));Files.copy(file,res.getOutputStream());
        } catch(SQLException e){throw new ServletException(e);}
    }
}
