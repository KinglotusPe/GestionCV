package com.cvmanager.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NvidiaAIService {

    private static final Logger LOGGER = Logger.getLogger(NvidiaAIService.class.getName());
    private static final String API_URL = "https://integrate.api.nvidia.com/v1/chat/completions";
    private static final String DEFAULT_MODEL = "openai/gpt-oss-20b";
    private static final String FALLBACK_API_KEY = "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";
    
    private final HttpClient httpClient;

    public NvidiaAIService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    private String getApiKey() {
        String key = System.getenv("NVIDIA_API_KEY");
        if (key == null || key.isBlank()) {
            key = System.getProperty("nvidia.api.key");
        }
        if (key == null || key.isBlank()) {
            try (var is = getClass().getClassLoader().getResourceAsStream("database.properties")) {
                if (is != null) {
                    Properties props = new Properties();
                    props.load(is);
                    key = props.getProperty("nvidia.api.key");
                }
            } catch (Exception ignored) {}
        }
        if (key == null || key.isBlank()) {
            key = FALLBACK_API_KEY;
        }
        return (key != null && !key.isBlank()) ? key.trim() : null;
    }

    private String getModel() {
        String model = System.getenv("NVIDIA_MODEL");
        if (model == null || model.isBlank()) {
            model = System.getProperty("nvidia.model");
        }
        if (model == null || model.isBlank()) {
            try (var is = getClass().getClassLoader().getResourceAsStream("database.properties")) {
                if (is != null) {
                    Properties props = new Properties();
                    props.load(is);
                    model = props.getProperty("nvidia.model");
                }
            } catch (Exception ignored) {}
        }
        return (model != null && !model.isBlank()) ? model.trim() : DEFAULT_MODEL;
    }

    public String generateGraduateProfile(String carrera, String habilidades, String intereses) {
        String prompt = "Actúa como un orientador laboral institucional del IESP Manuel Antonio Hierro Pozo. " +
                "Redacta un perfil profesional de alto impacto para un estudiante/egresado en 1 o 2 párrafos concisos y formales. " +
                "Datos proporcionados:\n" +
                "- Programa de estudios / Carrera: " + carrera + "\n" +
                "- Habilidades principales: " + habilidades + "\n" +
                "- Áreas de interés: " + intereses + "\n" +
                "Instrucciones: NO inventes empresas ficticias ni experiencia no especificada. Redacta en primera persona con enfoque en competencias y valor institucional. Devuelve ÚNICAMENTE el texto del perfil profesional sin introducciones ni comillas.";
        return callNvidiaOrFallback(prompt, generateFallbackProfile(carrera, habilidades));
    }

    public String improveExperience(String puesto, String empresa, String descripcionOriginal) {
        String prompt = "Mejora la redacción de la siguiente experiencia laboral para un currículum vitae, usando verbos de acción y logros concretos en español profesional: " +
                "Puesto: " + puesto + ", Empresa: " + empresa + ". Descripción original: " + descripcionOriginal + ". " +
                "Mantén la veracidad estricta, sin inventar cargos ni tecnologías no mencionadas. Devuelve solo las viñetas o texto redactado profesionalmente.";
        return callNvidiaOrFallback(prompt, generateFallbackExperience(puesto, empresa, descripcionOriginal));
    }

    public String improveSkills(String carrera, String habilidadesBorrador) {
        String prompt = "Organiza y optimiza la siguiente lista de habilidades técnicas y blandas para un CV en " + carrera + ": " + habilidadesBorrador + ". " +
                "Devuelve una lista limpia y profesional categorizada en Habilidades Técnicas y Competencias Profesionales.";
        return callNvidiaOrFallback(prompt, habilidadesBorrador != null ? habilidadesBorrador.trim() : "");
    }

    public String generateJobDescription(String tituloPuesto, String modalidad, String sector, String datosBasicos) {
        String prompt = "Actúa como un especialista en recursos humanos y atracción de talento corporativo. " +
                "Redacta la descripción atractiva y clara para una convocatoria laboral en Perú con los siguientes datos:\n" +
                "- Puesto: " + tituloPuesto + "\n" +
                "- Modalidad: " + modalidad + "\n" +
                "- Sector: " + sector + "\n" +
                "- Puntos clave: " + datosBasicos + "\n" +
                "Devuelve ÚNICAMENTE la descripción del puesto y objetivo del rol de forma concisa y profesional.";
        return callNvidiaOrFallback(prompt, generateFallbackJobDescription(tituloPuesto, modalidad, datosBasicos));
    }

    public String generateJobRequirements(String tituloPuesto, String carrera, String requisitosBorrador) {
        String prompt = "Redacta de manera clara y estructurada los requisitos y competencias indispensables para la vacante de '" + tituloPuesto +
                "' dirigida a egresados de " + carrera + ". Borrador inicial: " + requisitosBorrador + ". " +
                "Devuelve una lista con viñetas claras de Requisitos Académicos, Técnicos y Aptitudes.";
        return callNvidiaOrFallback(prompt, requisitosBorrador != null ? requisitosBorrador.trim() : "");
    }

    public String improveText(String tipo, String textoOriginal) {
        String prompt = "Corrige la ortografía, redacción y tono profesional del siguiente texto para un contexto de bolsa laboral (" + tipo + "):\n" +
                textoOriginal + "\nDevuelve únicamente el texto corregido.";
        return callNvidiaOrFallback(prompt, textoOriginal != null ? textoOriginal.trim() : "");
    }

    private String callNvidiaOrFallback(String prompt, String fallback) {
        String apiKey = getApiKey();
        if (apiKey == null) {
            LOGGER.info("NVIDIA_API_KEY no configurada. Utilizando redacción asistida institucional.");
            return fallback;
        }

        try {
            String escapedPrompt = escapeJson(prompt);
            String jsonPayload = "{"
                    + "\"model\": \"" + getModel() + "\","
                    + "\"messages\": ["
                    + "  {\"role\": \"system\", \"content\": \"Eres un asistente experto en redacción de CV y ofertas de empleo para la bolsa laboral del Instituto de Educación Superior Público Manuel Antonio Hierro Pozo. Responde siempre en español formal, directo y sin texto de relleno.\"},"
                    + "  {\"role\": \"user\", \"content\": \"" + escapedPrompt + "\"}"
                    + "],"
                    + "\"temperature\": 0.3,"
                    + "\"max_tokens\": 600"
                    + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .timeout(Duration.ofSeconds(15))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                String extracted = extractContentFromResponse(responseBody);
                if (extracted != null && !extracted.isBlank()) {
                    return extracted.trim();
                }
            } else {
                LOGGER.warning("NVIDIA API error HTTP " + response.statusCode() + ": " + response.body());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Excepción al conectar con NVIDIA AI. Aplicando plantilla institucional.", e);
        }

        return fallback;
    }

    private String extractContentFromResponse(String json) {
        if (json == null) return null;
        Pattern pattern = Pattern.compile("\"content\"\\s*:\\s*\"((?:\\\\\"|[^\"])*)\"", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            String raw = matcher.group(1);
            if (!raw.equals("null") && !raw.isBlank()) {
                return unescapeJson(raw);
            }
        }
        return null;
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

    private String unescapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    private String generateFallbackProfile(String carrera, String habilidades) {
        String baseCarrera = (carrera != null && !carrera.isBlank()) ? carrera : "mi especialidad técnica";
        String baseHab = (habilidades != null && !habilidades.isBlank()) ? " con sólidos conocimientos en " + habilidades : "";
        return "Profesional técnico formado en " + baseCarrera + baseHab + ", con capacidad analítica, alto sentido de responsabilidad y orientación a resultados. Comprometido con la mejora continua de procesos, el trabajo colaborativo interdisciplinario y la aplicación de estándares técnicos institucionales para generar valor en el entorno laboral.";
    }

    private String generateFallbackExperience(String puesto, String empresa, String desc) {
        String res = "• Gestión y ejecución técnica en el puesto de " + (puesto != null ? puesto : "Especialista") +
                (empresa != null && !empresa.isBlank() ? " en " + empresa : "") + ".\n";
        if (desc != null && !desc.isBlank()) {
            res += "• " + desc.replace("\n", "\n• ");
        } else {
            res += "• Aplicación de buenas prácticas técnicas, aseguramiento de la calidad y cumplimiento de objetivos asignados.";
        }
        return res;
    }

    private String generateFallbackJobDescription(String puesto, String modalidad, String datos) {
        String mod = (modalidad != null) ? " en modalidad " + modalidad : "";
        String res = "Buscamos un/a profesional para incorporarse como " + (puesto != null ? puesto : "Especialista") + mod + ".\n\n" +
                "El objetivo principal del puesto es liderar y ejecutar las actividades operativas y técnicas vinculadas al área, asegurando altos estándares de calidad y compromiso.";
        if (datos != null && !datos.isBlank()) {
            res += "\n\nAspectos clave de la posición:\n" + datos;
        }
        return res;
    }
}
