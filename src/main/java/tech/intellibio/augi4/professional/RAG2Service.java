/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tech.intellibio.augi4.professional;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.time.Duration.ofSeconds;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author leonard
 */


//Non streaming

@Service
public class RAG2Service {
    
    
    
    

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Value("${anthropic.api.url}")
    private String apiUrl;

    @Value("${anthropic.api.version}")
    private String apiVersion;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPass;


//

    private final RestTemplate restTemplate = new RestTemplate();

//Injected frrom config
   @Autowired
    private EmbeddingModel embeddingModel;

    public String getResponse(String prompt, String systemPrompt) throws SQLException {

        Response<Embedding> promptResponse = embeddingModel.embed(prompt);

// Get the Embedding object from the response
        Embedding embedding = promptResponse.content();
        List<Float> vec = embedding.vectorAsList();

        String query = "SELECT * FROM public.document ORDER BY embedding <-> ?::vector";

// Convert ArrayList to a PostgreSQL-compatible vector string
        StringBuilder vectorString = new StringBuilder();
        vectorString.append('[');
        for (int i = 0; i < vec.size(); i++) {
            vectorString.append(vec.get(i));
            if (i < vec.size() - 1) {
                vectorString.append(',');
            }
        }
        vectorString.append(']');
////        
//
//        System.out.println(vectorString);

        List<String> documentsContent = new ArrayList();

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, vectorString.toString());

            // Execute the query
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Assuming you want to get some columns from the result
                int id = rs.getInt("id"); // Replace with your actual column name
                String document = rs.getString("content"); // Replace with your actual column name
                // Add more fields as necessary
                documentsContent.add(document);
//                System.out.println("ID: " + id + ", Document: " + document);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // In production, handle this more gracefully
        }
//
//// Prepare the message content
        String messageContent = "Here are some relevant documents:\n\n" + documentsContent + "\n\nBased on these documents, " + prompt;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", apiVersion);

        Map<String, Object> requestBody = Map.of(
                "model", "claude-3-5-sonnet-20240620",
                "max_tokens", 2000,
                "temperature", 0.7,
                "system", systemPrompt,
                "messages", List.of(Map.of("role", "user", "content", messageContent))
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);
            // Log full response
            
            
            

            if (response.getBody() != null && response.getBody().containsKey("content")) {
                List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");
                if (!content.isEmpty() && content.get(0).containsKey("text")) {
                    String extractedText = (String) (String) content.get(0).get("text");
                    //System.out.println("Extracted text: " + extractedText);  // Print the extracted text
                    return extractedText;

                }
            }

            //throw new RuntimeException("Unexpected response structure from Anthropic API");
        } catch (HttpClientErrorException e) {
            System.err.println("API Error Response: " + e.getResponseBodyAsString());  // Log error response
            throw new RuntimeException("Error calling Anthropic API: " + e.getResponseBodyAsString(), e);
        }
        return "check";

    }
}

    

