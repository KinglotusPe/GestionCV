package pe.edu.hierropozo.cvmanager.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
import pe.edu.hierropozo.cvmanager.service.JobService;
import pe.edu.hierropozo.cvmanager.dao.CareerDAOImpl;

@WebServlet(urlPatterns={"/graduate/jobs","/company/jobs"})
public class JobsServlet extends HttpServlet {
    private final JobService service=new JobService();
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
        int id=(Integer)req.getSession().getAttribute("userId");boolean company="company".equals(req.getSession().getAttribute("role"));
        try {
            req.setAttribute("companyMode",company);req.setAttribute("jobs",service.list(id,company,req.getParameter("q")));
            req.setAttribute("applications",service.applications(id,company));req.setAttribute("careers",new CareerDAOImpl().findAll(true));
            if(company&&req.getParameter("edit")!=null)req.setAttribute("editing",service.findOwn(id,Integer.parseInt(req.getParameter("edit"))));
            req.setAttribute("pageTitle",company?"Mis vacantes y candidatos":"Vacantes y postulaciones");req.getRequestDispatcher("/WEB-INF/views/common/jobs.jsp").forward(req,res);
        }catch(Exception e){throw new ServletException(e);}
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
        int id=(Integer)req.getSession().getAttribute("userId");boolean company="company".equals(req.getSession().getAttribute("role"));
        try {
            String action=Objects.toString(req.getParameter("action"),"");
            if(company&&action.equals("save")) {
                Map<String,String> params=new HashMap<>();req.getParameterMap().forEach((k,v)->params.put(k,v[0]));
                String jobId=req.getParameter("id");service.save(id,jobId==null||jobId.isBlank()?null:Integer.valueOf(jobId),params);
            } else if(!company&&action.equals("apply")) {
                if(!"yes".equals(req.getParameter("consent")))throw new IllegalArgumentException("Confirme que desea compartir su CV y datos con la empresa.");
                service.apply(id,Integer.parseInt(req.getParameter("id")),req.getParameter("message"));
            } else if(action.equals("respond"))service.respond(id,company,Integer.parseInt(req.getParameter("id")),req.getParameter("status"));
            else throw new IllegalArgumentException("Acción inválida.");
            res.sendRedirect(req.getContextPath()+(company?"/company/jobs":"/graduate/jobs")+"?ok=1");
        }catch(Exception e){throw new ServletException(e);}
    }
}
