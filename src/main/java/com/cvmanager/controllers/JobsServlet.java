package com.cvmanager.controllers;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import com.cvmanager.services.*;
import com.cvmanager.dao.interfaces.*;
import com.cvmanager.dao.impl.*;
import com.cvmanager.utils.ArchivoUtil;

@WebServlet(urlPatterns={"/jobs","/graduate/jobs","/company/jobs","/admin/jobs"})
@MultipartConfig(maxFileSize=5*1024*1024,maxRequestSize=6*1024*1024,fileSizeThreshold=1024*1024)
public class JobsServlet extends HttpServlet {
    private final JobService service=new JobService();
    private final LaborPortalService portal=new LaborPortalService();
    private String role(HttpServletRequest req){return (String)req.getSession().getAttribute("role");}
    private int actor(HttpServletRequest req){return (Integer)req.getSession().getAttribute("userId");}
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
        HttpSession session=req.getSession(false);
        if(session==null||session.getAttribute("userId")==null){
            res.sendRedirect(req.getContextPath()+"/login?auth=1");
            return;
        }
        String role=role(req);
        if("/jobs".equals(req.getServletPath())){
            res.sendRedirect(req.getContextPath()+"/"+role+"/jobs");
            return;
        }
        int actor=actor(req);
        try {
            if(req.getParameter("document")!=null) {
                try {
                    String name=portal.download(actor,role,Integer.parseInt(req.getParameter("document")));
                    var path=ArchivoUtil.resolve("pdf",name);
                    if(!Files.isRegularFile(path)){res.sendError(404);return;}
                    res.setContentType("application/pdf");res.setHeader("Content-Disposition","attachment; filename=\"convocatoria.pdf\"");
                    res.setContentLengthLong(Files.size(path));Files.copy(path,res.getOutputStream());return;
                }catch(IllegalArgumentException e){res.sendError(404);return;}
            }
            req.setAttribute("companyMode",role.equals("company"));req.setAttribute("adminMode",role.equals("admin"));req.setAttribute("manageMode",!role.equals("graduate"));
            req.setAttribute("jobsPath",req.getContextPath()+"/"+role+"/jobs");
            req.setAttribute("jobs",new JobDAOImpl().list(actor,role,req.getParameter("q"),req.getParameter("stage"),req.getParameter("archive")));
            req.setAttribute("applications",role.equals("admin")?List.of():service.applications(actor,role.equals("company")));
            req.setAttribute("careers",new CareerDAOImpl().findAll(false));
            if(role.equals("admin"))req.setAttribute("companies",portal.companies());
            String selected=req.getParameter("edit")!=null?req.getParameter("edit"):req.getParameter("view");
            if(selected!=null) {
                int id=Integer.parseInt(selected);var job=new JobDAOImpl().find(actor,role,id);
                req.setAttribute("selected",job);
                Object manager=job.get("managed_by");
                boolean editable=role.equals("admin")?manager!=null&&((Number)manager).intValue()==actor:role.equals("company")&&manager==null;
                req.setAttribute("editable",editable);req.setAttribute("documents",portal.documents(actor,role,id));
                if(req.getParameter("edit")!=null){if(!editable)throw new IllegalArgumentException("Esta convocatoria tiene otro administrador.");req.setAttribute("editing",job);}
            }
            req.setAttribute("pageTitle","Bolsa laboral - Convocatorias");
            req.getRequestDispatcher("/WEB-INF/views/common/jobs.jsp").forward(req,res);
        }catch(Exception e){throw new ServletException(e);}
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
        int actor=actor(req);String role=role(req);boolean manager=!role.equals("graduate");
        try {
            String action=Objects.toString(req.getParameter("action"),"");
            String raw=req.getParameter("id");Integer id=raw==null||raw.isBlank()?null:Integer.valueOf(raw);
            if(manager&&action.equals("save")) {
                Map<String,String> params=new HashMap<>();req.getParameterMap().forEach((k,v)->params.put(k,v[0]));
                portal.save(actor,role,id,params,req.getParameterValues("careerId"));
                req.getSession().setAttribute("flashSuccess", "Convocatoria guardada exitosamente.");
            }else if(manager&&action.equals("changeJobStatus")) {
                String newStatus = req.getParameter("status");
                portal.updateJobStatus(actor, role, id, newStatus);
                req.getSession().setAttribute("flashSuccess", "Estado de la convocatoria actualizado a: " + newStatus);
            }else if(manager&&action.equals("archive"))portal.archive(actor,role,id,"yes".equals(req.getParameter("archived")));
            else if(manager&&action.equals("upload")) {
                Part file=req.getPart("file");if(file==null)throw new IllegalArgumentException("Seleccione un PDF.");
                try(var in=file.getInputStream()){portal.addDocument(actor,role,id,req.getParameter("title"),in.readNBytes(5*1024*1024+1));}finally{file.delete();}
            }else if(manager&&action.equals("removeDocument"))portal.removeDocument(actor,role,id,Integer.parseInt(req.getParameter("documentId")));
            else if(role.equals("graduate")&&action.equals("apply")) {
                if(!"yes".equals(req.getParameter("consent")))throw new IllegalArgumentException("Confirme que desea compartir su CV y datos con la empresa.");
                service.apply(actor,id,req.getParameter("message"));
                req.getSession().setAttribute("flashSuccess", "¡Tu postulación ha sido enviada exitosamente a la empresa!");
            }else if(!role.equals("admin")&&action.equals("respond")) {
                service.respond(actor,role.equals("company"),id,req.getParameter("status"));
                req.getSession().setAttribute("flashSuccess", "Estado de la postulación actualizado exitosamente.");
            }else throw new IllegalArgumentException("Acción inválida.");
            res.sendRedirect(req.getContextPath()+"/"+role+"/jobs?ok=1"+(Set.of("upload","removeDocument").contains(action)?"&view="+id:""));
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("flashError", e.getMessage());
            String redir = req.getContextPath() + "/" + role + "/jobs?error=" + java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
            if ("save".equals(req.getParameter("action"))) {
                redir += (req.getParameter("id") != null && !req.getParameter("id").isBlank() ? "&edit=" + req.getParameter("id") : "&new=1");
            } else if (Set.of("upload", "removeDocument").contains(req.getParameter("action"))) {
                redir += "&view=" + req.getParameter("id");
            }
            res.sendRedirect(redir);
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Ocurrió un error al procesar la solicitud: " + e.getMessage());
            res.sendRedirect(req.getContextPath() + "/" + role + "/jobs");
        }
    }
}
