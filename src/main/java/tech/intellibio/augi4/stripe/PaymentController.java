/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tech.intellibio.augi4.stripe;

import com.checkout.payments.request.PaymentRequest;
import com.checkout.payments.response.PaymentResponse;
import groovy.util.logging.Slf4j;
import static java.lang.Math.log;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
   import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author leonard
 */
@Controller
@RequestMapping("/payment")
@Slf4j
public class PaymentController {

   private static final String API_SECRET_KEY = "sk_sbox_xytis7wykpe7pclpd3afe22h54s";
    private static final String API_ENDPOINT = "https://api.sandbox.checkout.com/payment-sessions";
  
    
      public static void makePaymentSessionRequest() {
        try {
            // Create the request payload
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("amount", 1000);
            requestBody.put("currency", "GBP");
            requestBody.put("reference", "ORD-123A");

            // Billing details
            Map<String, Object> billing = new HashMap<>();
            Map<String, String> address = new HashMap<>();
            address.put("country", "GB");
            billing.put("address", address);
            requestBody.put("billing", billing);

            // Customer details
            Map<String, String> customer = new HashMap<>();
            customer.put("name", "Jia Tsang");
            customer.put("email", "jia.tsang@example.com");
            requestBody.put("customer", customer);

            // Success and failure URLs
            requestBody.put("success_url", "https://example.com/payments/success");
            requestBody.put("failure_url", "https://example.com/payments/failure");

            // Convert payload to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(requestBody);

            // Create HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Create HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_ENDPOINT))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_SECRET_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

            // Send request and get response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse and print the response
            int statusCode = response.statusCode();
            String responseBody = response.body();
            
            System.out.println("Status Code: " + statusCode);
            System.out.println("Response Body: " + responseBody);

            // Optional: Parse response to a Map if you want to extract specific fields
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            String paymentSessionId = (String) responseMap.get("id");
            String paymentSessionToken = (String) responseMap.get("payment_session_token");

            System.out.println("Payment Session ID: " + paymentSessionId);
            System.out.println("Payment Session Token: " + paymentSessionToken);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
  
    
    
    @GetMapping("/checkout")
    public String showCheckoutForm(Model model) {
             record PaymentDetails(
        long amountInPennies,
        String currency,
        String reference,
        String billingCountry,
        String customerName,
        String customerEmail,
        String processingChannelId
    ) {}

    // Instantiate the payment details with sample data
    PaymentDetails paymentDetails = new PaymentDetails(
        10000,       // Amount in pennies (e.g., $100.00)
        "USD",       // Currency
        "INV-2024-001", // Reference
        "United States", // Billing Country
        "John Doe",  // Customer Name
        "john.doe@example.com", // Customer Email
        "pc_q4dbxom5jbgudnjzjpz7j2z6uq" // Processing Channel ID for Checkout.com
    );

    // Add to model
    model.addAttribute("payment", paymentDetails);
        
        
         makePaymentSessionRequest();
        return "checkout";
    }
    



 
}




