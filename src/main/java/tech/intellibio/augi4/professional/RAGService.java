/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tech.intellibio.augi4.professional;

/**
 *
 * @author leonard
 */
import dev.langchain4j.data.embedding.Embedding;

//import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
//import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import tech.intellibio.augi4.document.Document;
import tech.intellibio.augi4.document.DocumentRepository;
import tech.intellibio.augi4.user.User;

import org.springframework.stereotype.Service;

@Service
public class RAGService {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPass;

    private final EmbeddingModel embeddingModel;

    private final DocumentRepository documentRepository;

    // Directory where files will be saved
    private final String FILE_DIRECTORY = "files";

    public RAGService(final EmbeddingModel embeddingModel, tech.intellibio.augi4.document.DocumentRepository documentRepository) {

        this.embeddingModel = embeddingModel;

        this.documentRepository = documentRepository;
    }

    private void saveFile(MultipartFile file, String newFilename) throws IOException {
        // Create directory if it doesn't exist
        File directory = new File(FILE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir(); // Create the directory
        }

        // Define the path where the file will be saved
        Path filePath = Paths.get(directory.getAbsolutePath(), newFilename);

        // Save the file
        Files.copy(file.getInputStream(), filePath);
        //System.out.println("File saved to: " + filePath.toString());
    }

    private String extractPdfText(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractDocxText(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            return extractor.getText();
        }
    }

    private String extractTextWithTika(InputStream inputStream) throws IOException, TikaException {
        Tika tika = new Tika();
        return tika.parseToString(inputStream);
    }

    public String createDocuments(
            MultipartFile[] files, User user
    ) throws SAXException, IOException {

        // Generate a random UUID (or use a random number if preferred)
        String sessionID = UUID.randomUUID().toString();

        for (MultipartFile file : files) {

            String extractedText = null;

            try (InputStream inputStream = file.getInputStream()) {
                String contentType = file.getContentType();

                if ("application/pdf".equals(contentType)) {
                    extractedText = extractPdfText(inputStream);
                } else if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType)) {
                    extractedText = extractDocxText(inputStream);
                } else {
                    // Fallback to Tika for other file types
                    extractedText = extractTextWithTika(inputStream);
                }

                Response<Embedding> response = embeddingModel.embed(extractedText);

// Get the Embedding object from the response
                Embedding embedding = response.content();
                double[] pgvectorEmbedding = embedding.vectorAsList().stream()
                        .mapToDouble(Float::doubleValue)
                        .toArray();

                Document document = new Document();
                document.setEmbedding(pgvectorEmbedding);

                // Create a new filename with the random UUID and original extension
                String newFilename = sessionID + file.getOriginalFilename();

                document.setContent(extractedText);
                document.setEmbedding(pgvectorEmbedding);
                document.setFilename(newFilename);
                document.setSessionId(sessionID);

                document.setUser(user);

                documentRepository.save(document);

                // Save the file to the directory
                saveFile(file, newFilename);

            } catch (TikaException e) {
                System.err.println("Tika-specific parsing error: " + e.getMessage());
                e.printStackTrace();

            } catch (FileAlreadyExistsException e) {

                System.err.println("FileAlreadyExistsException " + e.getMessage());
//            } catch (Exception e) {
//                System.err.println("General error: " + e.getMessage());
//                e.printStackTrace();
//                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (Exception e) {
                // Log the error (optional)
                System.err.println("Error occurred: " + e.getMessage());

                // Check if the error message indicates the model is loading
                if (e.getMessage().contains("Model sentence-transformers/all-MiniLM-L6-v2 is currently loading")) {

                    System.out.println("\"Model sentence-transformers/all-MiniLM-L6-v2 is currently loading\"");

                }

            }

        }
        return sessionID;
    }

    public String rag(String prompt, String sessionId) throws SQLException {
        Response<Embedding> promptResponse = embeddingModel.embed(prompt);

// Get the Embedding object from the response
        Embedding embedding1 = promptResponse.content();
        double[] vec = embedding1.vectorAsList().stream()
                .mapToDouble(Float::doubleValue)
                .toArray();

        String query = "SELECT * FROM public.documents WHERE session_id =? ORDER BY embedding <-> ?::vector limit 2";

// Convert ArrayList to a PostgreSQL-compatible vector string
        StringBuilder vectorString = new StringBuilder();
        vectorString.append('[');
        for (int i = 0; i < vec.length; i++) { // Use vec.length instead of vec.size()
            vectorString.append(vec[i]); // Use vec[i] instead of vec.get(i)
            if (i < vec.length - 1) { // Use vec.length instead of vec.size()
                vectorString.append(',');
            }
        }
        vectorString.append(']');
////        
//
        //System.out.println(vectorString.toString());

        List<String> documentsContent = new ArrayList();

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, sessionId);
            pstmt.setString(2, vectorString.toString());

            // Execute the query
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Assuming you want to get some columns from the result
                int id = rs.getInt("id"); // Replace with your actual column name
                String document = rs.getString("content"); // Replace with your actual column name
                // Add more fields as necessary
                documentsContent.add(document);
                //System.out.println("ID: " + id + ", Document: " + document);
            }
        } catch (SQLException e) {

        }

        String message;

        if (!documentsContent.isEmpty()) {
            // Use messageContent in your API call or other processing

            message = "Here are some relevant documents:\n\n" + documentsContent + "\n\nBased on these documents, " + prompt;

        } else {
            // Handle the case where all documents were empty
            message = prompt;
        }

        return message;

    }
}
