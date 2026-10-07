package pe.edu.hierropozo.cvmanager.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;
import pe.edu.hierropozo.cvmanager.config.DBConnection;
import pe.edu.hierropozo.cvmanager.dao.CurriculumDAO;
import pe.edu.hierropozo.cvmanager.dao.CurriculumDAOImpl;
import pe.edu.hierropozo.cvmanager.dao.GraduateDAO;
import pe.edu.hierropozo.cvmanager.dao.GraduateDAOImpl;
import pe.edu.hierropozo.cvmanager.model.Curriculum;
import pe.edu.hierropozo.cvmanager.model.Graduate;

/**
 * Controlador para la subida de archivos (fotografía de perfil o CV en PDF)
 * mediante formularios multipart/form-data.
 */
@WebServlet("/graduate/upload")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB en memoria
    maxFileSize = 5 * 1024 * 1024,        // 5 MB máximo por archivo
    maxRequestSize = 6 * 1024 * 1024      // 6 MB máximo por petición
)
public class UploadServlet extends HttpServlet {

    private final GraduateDAO graduateDAO = new GraduateDAOImpl();
    private final CurriculumDAO curriculumDAO = new CurriculumDAOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String uploadType = request.getParameter("type");
            Part filePart = request.getPart("file");

            if (filePart == null || filePart.getSize() == 0) {
                throw new IllegalArgumentException("El archivo seleccionado está vacío.");
            }

            String contentType = filePart.getContentType();
            boolean isPhoto = "photo".equals(uploadType);

            // Validación estricta de MIME types
            if (isPhoto && !("image/jpeg".equals(contentType) || "image/png".equals(contentType))) {
                throw new IllegalArgumentException("Solo se admiten imágenes JPG o PNG para la foto de perfil.");
            }

            if (!isPhoto && !"application/pdf".equals(contentType)) {
                throw new IllegalArgumentException("Solo se admiten documentos en formato PDF para el currículum.");
            }

            String extension = isPhoto ? ("image/png".equals(contentType) ? ".png" : ".jpg") : ".pdf";
            String uniqueFileName = UUID.randomUUID().toString() + extension;
            String subFolder = isPhoto ? "photos" : "cvs";

            // Guardar en la carpeta del usuario
            Path uploadDir = Path.of(System.getProperty("user.home"), "cvmanager_uploads", subFolder);
            Files.createDirectories(uploadDir);

            Path destinationPath = uploadDir.resolve(uniqueFileName);
            try (InputStream inputStream = filePart.getInputStream()) {
                Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            }

            int userId = (Integer) request.getSession().getAttribute("userId");

            if (isPhoto) {
                String sql = "UPDATE egresados SET photo_url = ? WHERE user_id = ?";
                try (Connection connection = DBConnection.getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, uniqueFileName);
                    statement.setInt(2, userId);
                    statement.executeUpdate();
                }
            } else {
                Graduate graduate = graduateDAO.findByUserId(userId);
                Curriculum curriculum = curriculumDAO.findByGraduate(graduate.getGraduateId());

                String sql = "UPDATE cv SET cv_pdf_url = ? WHERE cv_id = ?";
                try (Connection connection = DBConnection.getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, uniqueFileName);
                    statement.setInt(2, curriculum.getCvId());
                    statement.executeUpdate();
                }
            }

            String redirectTarget = isPhoto ? "profile" : "cv";
            response.sendRedirect(request.getContextPath() + "/graduate/" + redirectTarget + "?uploaded=1");

        } catch (Exception e) {
            throw new ServletException("Error al cargar y procesar el archivo adjunto", e);
        }
    }
}
