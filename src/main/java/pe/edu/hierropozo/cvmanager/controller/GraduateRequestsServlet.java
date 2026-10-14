package pe.edu.hierropozo.cvmanager.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import pe.edu.hierropozo.cvmanager.service.ProfileService;
@WebServlet("/graduate/requests")
public class GraduateRequestsServlet extends HttpServlet {
 private final ProfileService service=new ProfileService();
 protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
  try{req.setAttribute("requests",service.requests((Integer)req.getSession().getAttribute("userId")));req.setAttribute("pageTitle","Mis propuestas laborales");req.getRequestDispatcher("/WEB-INF/views/graduate/requests.jsp").forward(req,res);}catch(Exception e){throw new ServletException(e);}
 }
 protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
  try{if(!service.respond((Integer)req.getSession().getAttribute("userId"),Integer.parseInt(req.getParameter("id")),req.getParameter("status"))){res.sendError(404);return;}res.sendRedirect(req.getContextPath()+"/graduate/requests?ok=1");}catch(Exception e){throw new ServletException(e);}
 }
}
