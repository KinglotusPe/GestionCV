package pe.edu.hierropozo.cvmanager.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

import java.nio.file.*;

import pe.edu.hierropozo.cvmanager.util.FileStorage;

@WebServlet("/files")
public class FileServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException {
        String type=req.getParameter("type");
        if(!"photo".equals(type)&&!"pdf".equals(type)){res.sendError(400);return;}
        int userId=(Integer)req.getSession().getAttribute("userId");
        String role=(String)req.getSession().getAttribute("role");
        try {
            String name=new pe.edu.hierropozo.cvmanager.service.ProfileService().file(Integer.parseInt(req.getParameter("cvId")),userId,role,type);
            if(name==null){res.sendError(404);return;}
            Path file=FileStorage.resolve(type,name);
            if(!Files.isRegularFile(file)){res.sendError(404);return;}
            res.setHeader("Content-Security-Policy","sandbox");
            res.setContentType(type.equals("pdf")?"application/pdf":name.endsWith(".png")?"image/png":"image/jpeg");
            res.setHeader("Content-Disposition",type.equals("pdf")?"attachment; filename=curriculum.pdf":"inline");
            res.setContentLengthLong(Files.size(file));Files.copy(file,res.getOutputStream());
        } catch(Exception e){throw new ServletException(e);}
    }
}
