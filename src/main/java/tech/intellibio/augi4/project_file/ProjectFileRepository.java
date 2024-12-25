package tech.intellibio.augi4.project_file;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tech.intellibio.augi4.project.Project;


public interface ProjectFileRepository extends JpaRepository<ProjectFile, Long> {

    Page<ProjectFile> findAllById(Long id, Pageable pageable);

    ProjectFile findFirstByFile(Project project);

}
