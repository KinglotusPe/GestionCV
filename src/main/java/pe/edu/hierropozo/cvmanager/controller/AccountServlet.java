package pe.edu.hierropozo.cvmanager.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Objects;
import pe.edu.hierropozo.cvmanager.service.PasswordService;

@WebServlet(urlPatterns={"/account","/reset-password"})
public class AccountServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
        req.setAttribute("reset",req.getServletPath().equals("/reset-password"));
        req.setAttribute("pageTitle","Contraseña de la cuenta");
        req.getRequestDispatcher("/WEB-INF/views/auth/password.jsp").forward(req,res);
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
        if(!Objects.equals(req.getParameter("password"),req.getParameter("confirmPassword")))throw new IllegalArgumentException("Las contraseñas no coinciden.");
        try {
            PasswordService service=new PasswordService();
            if(req.getServletPath().equals("/reset-password")) {
                service.reset(req.getParameter("token"),req.getParameter("password"));
                req.getSession().invalidate();
                res.sendRedirect(req.getContextPath()+"/login?passwordChanged=1");
            } else {
                String hash=service.change((Integer)req.getSession().getAttribute("userId"),req.getParameter("currentPassword"),req.getParameter("password"));
                req.changeSessionId();req.getSession().setAttribute("authVersion",hash);
                res.sendRedirect(req.getContextPath()+"/account?ok=1");
            }
        }catch(Exception e){throw new ServletException(e);}
    }
}
