package pe.edu.hierropozo.cvmanager.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import pe.edu.hierropozo.cvmanager.config.DBConnection;
import pe.edu.hierropozo.cvmanager.util.ValidationUtil;

@WebServlet("/company/profile")
public class CompanyProfileServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
        try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement("SELECT company_name,ruc,industry,company_size,phone,website,description FROM empresas WHERE user_id=?")) {
            st.setInt(1,(Integer)req.getSession().getAttribute("userId"));
            try(ResultSet rs=st.executeQuery()) {
                if(!rs.next()){res.sendError(404);return;}
                Map<String,Object> company=new HashMap<>();
                for(String key:List.of("company_name","ruc","industry","company_size","phone","website","description"))company.put(key,rs.getObject(key));
                req.setAttribute("company",company);
            }
            req.setAttribute("pageTitle","Perfil de empresa");req.getRequestDispatcher("/WEB-INF/views/company/profile.jsp").forward(req,res);
        }catch(SQLException e){throw new ServletException(e);}
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
        String name=ValidationUtil.text(req.getParameter("companyName"),"Empresa",150,true);
        String ruc=ValidationUtil.nullable(req.getParameter("ruc")),phone=ValidationUtil.nullable(req.getParameter("phone"));
        if(ruc!=null&&!ValidationUtil.ruc(ruc))throw new IllegalArgumentException("RUC inválido.");
        if(phone!=null&&!ValidationUtil.phone(phone))throw new IllegalArgumentException("Teléfono inválido.");
        try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement("UPDATE empresas SET company_name=?,ruc=?,industry=?,company_size=?,phone=?,website=?,description=? WHERE user_id=?")) {
            st.setString(1,name);st.setString(2,ruc);st.setString(3,ValidationUtil.text(req.getParameter("industry"),"Sector",100,false));
            st.setString(4,ValidationUtil.text(req.getParameter("companySize"),"Tamaño",50,false));st.setString(5,phone);st.setString(6,ValidationUtil.url(req.getParameter("website")));
            st.setString(7,ValidationUtil.text(req.getParameter("description"),"Descripción",5000,false));st.setInt(8,(Integer)req.getSession().getAttribute("userId"));
            st.executeUpdate();res.sendRedirect(req.getContextPath()+"/company/profile?ok=1");
        }catch(SQLException e){throw new ServletException(e);}
    }
}
