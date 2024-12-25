/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tech.intellibio.augi4.professional;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.product.Product;
import tech.intellibio.augi4.product.ProductRepository;
import tech.intellibio.augi4.prompt.Prompt;
import tech.intellibio.augi4.prompt.PromptRepository;

/**
 *
 * @author leonard
 */


@Service
public class GeniusService {
    



    final private ProductRepository productRepository;

   
    final private PromptRepository promptRepository;

    public GeniusService(final ProductRepository productRepository, final PromptRepository promptRepository) {
        this.productRepository = productRepository;
        this.promptRepository = promptRepository;
    }
    
    
    

    public List<Prompt> getPromptsByProductName(String productName) {
        Product product = productRepository.findByName(productName)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return promptRepository.findByPromptProducts(product);
    }
    

}