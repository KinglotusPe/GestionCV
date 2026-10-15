package com.cvmanager.controllers;

import com.cvmanager.services.NvidiaAIService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/api/ai-assistant")
public class AIAssistantServlet extends HttpServlet {

    private final NvidiaAIService aiService = new NvidiaAIService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json;charset=UTF-8");
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"success\":false,\"error\":\"Sesión expirada o no autenticada.\"}");
            return;
        }

        String action = req.getParameter("action");
        if (action == null || action.isBlank()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"success\":false,\"error\":\"Acción de IA no especificada.\"}");
            return;
        }

        try {
            String resultText = "";
            switch (action) {
                case "generateGraduateProfile":
                    String carrera = req.getParameter("carrera");
                    String habilidades = req.getParameter("habilidades");
                    String intereses = req.getParameter("intereses");
                    resultText = aiService.generateGraduateProfile(carrera, habilidades, intereses);
                    break;

                case "improveExperience":
                    String puestoExp = req.getParameter("puesto");
                    String empresaExp = req.getParameter("empresa");
                    String descExp = req.getParameter("descripcion");
                    resultText = aiService.improveExperience(puestoExp, empresaExp, descExp);
                    break;

                case "improveSkills":
                    String carreraSkills = req.getParameter("carrera");
                    String rawSkills = req.getParameter("habilidades");
                    resultText = aiService.improveSkills(carreraSkills, rawSkills);
                    break;

                case "generateJobDescription":
                    String tituloPuesto = req.getParameter("puesto");
                    String modalidad = req.getParameter("modalidad");
                    String sector = req.getParameter("sector");
                    String datos = req.getParameter("datos");
                    resultText = aiService.generateJobDescription(tituloPuesto, modalidad, sector, datos);
                    break;

                case "generateJobRequirements":
                    String reqPuesto = req.getParameter("puesto");
                    String reqCarrera = req.getParameter("carrera");
                    String reqBorrador = req.getParameter("requisitos");
                    resultText = aiService.generateJobRequirements(reqPuesto, reqCarrera, reqBorrador);
                    break;

                case "improveText":
                    String tipo = req.getParameter("tipo");
                    String texto = req.getParameter("texto");
                    resultText = aiService.improveText(tipo, texto);
                    break;

                default:
                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    res.getWriter().write("{\"success\":false,\"error\":\"Acción no soportada.\"}");
                    return;
            }

            String escaped = escapeJson(resultText);
            res.getWriter().write("{\"success\":true,\"result\":\"" + escaped + "\"}");

        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"success\":false,\"error\":\"Error al procesar con IA: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
