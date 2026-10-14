package pe.edu.hierropozo.cvmanager.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import pe.edu.hierropozo.cvmanager.service.LaborPortalService;
import pe.edu.hierropozo.cvmanager.util.SpreadsheetExport;
import pe.edu.hierropozo.cvmanager.util.JsonResponse;
import java.util.Map;

@WebServlet(urlPatterns={"/admin/companies","/admin/reports"})
public class LaborAdminServlet extends HttpServlet {
    private final LaborPortalService service=new LaborPortalService();
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
        try {
            boolean reports=req.getServletPath().endsWith("reports");
            if(reports) {
                String start=req.getParameter("start"),end=req.getParameter("end"),kind=req.getParameter("kind");
                if(start==null)start=LocalDate.now().withDayOfYear(1).toString();if(end==null)end=LocalDate.now().toString();if(kind==null)kind="jobs";
                var rows=service.report(kind,start,end);
                if("xlsx".equals(req.getParameter("format"))){res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");res.setHeader("Content-Disposition","attachment; filename=\"reporte-bolsa-laboral.xlsx\"");SpreadsheetExport.write(res.getOutputStream(),rows);return;}
                req.setAttribute("rows",rows);req.setAttribute("start",start);req.setAttribute("end",end);req.setAttribute("kind",kind);
            }else {req.setAttribute("companies",service.companies());req.setAttribute("reviews",service.reviews());}
            req.setAttribute("pageTitle",reports?"Reportes de bolsa laboral":"Empresas y solicitudes");
            req.getRequestDispatcher("/WEB-INF/views/admin/"+(reports?"reports":"companies")+".jsp").forward(req,res);
        }catch(Exception e){throw new ServletException(e);}
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
        if(!req.getServletPath().endsWith("companies")){res.sendError(405);return;}
        try {
            var review=service.review((Integer)req.getSession().getAttribute("userId"),Integer.parseInt(req.getParameter("id")),req.getParameter("decision"),req.getParameter("reason"));
            if(JsonResponse.requested(req)) {
                JsonResponse.send(res,200,Map.of("message","Decisión guardada correctamente.","companyName",review.companyName(),"statusLabel",review.statusLabel(),"reason",review.reason(),"reviewedAt",review.reviewedAt()));return;
            }
            res.sendRedirect(req.getContextPath()+"/admin/companies?ok=1");
        }catch(Exception e){
            if(JsonResponse.requested(req)) {
                boolean invalid=e instanceof IllegalArgumentException;
                if(!invalid)getServletContext().log("Falló la transacción de revisión de empresa",e);
                JsonResponse.send(res,invalid?400:500,Map.of("message",invalid?e.getMessage():"No se pudo completar la operación. Revise el historial antes de reintentar."));return;
            }
            throw new ServletException(e);
        }
    }
}
