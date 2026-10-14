package com.cvmanager.controllers;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import com.cvmanager.services.ProfileService;
@WebServlet("/company/profile")
public class CompanyProfileServlet extends HttpServlet {
 private final ProfileService service=new ProfileService();
 protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
  try{var company=service.company((Integer)req.getSession().getAttribute("userId"));if(company==null){res.sendError(404);return;}req.setAttribute("company",company);req.setAttribute("pageTitle","Perfil de empresa");req.getRequestDispatcher("/WEB-INF/views/company/profile.jsp").forward(req,res);}catch(Exception e){throw new ServletException(e);}
 }
 protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
  try{var p=new java.util.HashMap<String,String>();req.getParameterMap().forEach((k,v)->p.put(k,v[0]));service.saveCompany((Integer)req.getSession().getAttribute("userId"),p);res.sendRedirect(req.getContextPath()+"/company/profile?ok=1");}catch(Exception e){throw new ServletException(e);}
 }
}
