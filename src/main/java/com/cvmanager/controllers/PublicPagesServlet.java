package com.cvmanager.controllers;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
@WebServlet(urlPatterns={"/about","/contact","/forgot-password"})
public class PublicPagesServlet extends HttpServlet {
 @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
  String view=switch(req.getServletPath()) {case "/about"->"public/Acerca_de.jsp";case "/contact"->"public/contactos.jsp";default->"auth/Olvidado_Password.jsp";};
  req.getRequestDispatcher("/WEB-INF/views/"+view).forward(req,res);
 }
}
