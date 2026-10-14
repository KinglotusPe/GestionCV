package pe.edu.hierropozo.cvmanager.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import pe.edu.hierropozo.cvmanager.config.DBConnection;

@WebServlet("/graduate/requests")
public class GraduateRequestsServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String sql = "SELECT s.request_id, s.message, s.status, s.created_at, e.company_name, e.phone, u.email "
                + "FROM solicitudes_contacto s JOIN egresados g ON g.graduate_id=s.graduate_id "
                + "JOIN empresas e ON e.company_id=s.company_id JOIN usuarios u ON u.user_id=e.user_id "
                + "WHERE g.user_id=? ORDER BY s.created_at DESC";
        try (Connection c=DBConnection.getConnection(); PreparedStatement st=c.prepareStatement(sql)) {
            st.setInt(1,(Integer)req.getSession().getAttribute("userId"));
            List<Map<String,Object>> rows=new ArrayList<>();
            try (ResultSet rs=st.executeQuery()) {
                while(rs.next()) {
                    Map<String,Object> row=new HashMap<>();
                    for(String key:List.of("request_id","message","status","created_at","company_name","phone","email")) row.put(key,rs.getObject(key));
                    rows.add(row);
                }
            }
            req.setAttribute("requests",rows);
            req.setAttribute("pageTitle","Mis propuestas laborales");
            req.getRequestDispatcher("/WEB-INF/views/graduate/requests.jsp").forward(req,res);
        } catch(SQLException e) { throw new ServletException(e); }
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException {
        String status=req.getParameter("status");
        if (!Set.of("accepted","rejected").contains(Objects.toString(status,""))) throw new IllegalArgumentException("Respuesta inválida.");
        // A graduate may also revoke a previously accepted request.
        try(Connection c=DBConnection.getConnection(); PreparedStatement st=c.prepareStatement(
                "UPDATE solicitudes_contacto s JOIN egresados g ON g.graduate_id=s.graduate_id SET s.status=? WHERE s.request_id=? AND g.user_id=?")) {
            st.setString(1,status); st.setInt(2,Integer.parseInt(req.getParameter("id"))); st.setInt(3,(Integer)req.getSession().getAttribute("userId"));
            if(st.executeUpdate()!=1) { res.sendError(404); return; }
            res.sendRedirect(req.getContextPath()+"/graduate/requests?ok=1");
        } catch(SQLException e) { throw new ServletException(e); }
    }
}
