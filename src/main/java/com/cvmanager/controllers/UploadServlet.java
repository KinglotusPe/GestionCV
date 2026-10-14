package com.cvmanager.controllers;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import com.cvmanager.services.Archivo_Servicio;
@WebServlet("/graduate/upload")
@MultipartConfig(fileSizeThreshold=1024*1024,maxFileSize=5*1024*1024,maxRequestSize=6*1024*1024)
public class UploadServlet extends HttpServlet {
 protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
  String type=req.getParameter("type");
  try{byte[] bytes=null;
   if(!"remove".equals(req.getParameter("action"))){Part file=req.getPart("file");if(file==null)throw new IllegalArgumentException("Seleccione un archivo.");try(var in=file.getInputStream()){bytes=in.readNBytes(5*1024*1024+1);}finally{file.delete();}}
   new Archivo_Servicio().replaceFile((Integer)req.getSession().getAttribute("userId"),type,bytes);
   res.sendRedirect(req.getContextPath()+"/graduate/"+("photo".equals(type)?"profile":"cv")+"?ok=1");
  }catch(Exception e){throw new ServletException(e);}
 }
}
