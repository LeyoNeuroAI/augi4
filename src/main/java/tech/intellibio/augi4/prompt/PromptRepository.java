package tech.intellibio.augi4.prompt;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tech.intellibio.augi4.product.Product;
import tech.intellibio.augi4.program.Program;



public interface PromptRepository extends JpaRepository<Prompt, Long> {

    Page<Prompt> findAllById(Long id, Pageable pageable);

  Prompt findFirstByProgram(Program program);

    Prompt findFirstByPromptProducts(Product product);
    
    List<Prompt> findByPromptProducts(Product product);
    
   Optional  <Prompt> findByChapterNoAndProgram(Integer chapterNo, Program program);
    

}
