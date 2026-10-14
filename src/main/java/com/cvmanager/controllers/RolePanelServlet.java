package com.cvmanager.controllers;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
@WebServlet(urlPatterns={"/graduate/dashboard","/company/dashboard"})
public class RolePanelServlet extends HttpServlet {
 @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
  String role=req.getServletPath().startsWith("/graduate/")?"graduate":"company";
  req.getRequestDispatcher("/WEB-INF/views/"+role+"/Panel.jsp").forward(req,res);
 }
}
