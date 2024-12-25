package tech.intellibio.augi4.document;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.user.User;
import tech.intellibio.augi4.user.UserRepository;
import tech.intellibio.augi4.util.NotFoundException;


@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    public DocumentService(final DocumentRepository documentRepository,
            final UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
    }

    public Page<DocumentDTO> findAll(final String filter, final Pageable pageable) {
        Page<Document> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = documentRepository.findAllById(integerFilter, pageable);
        } else {
            page = documentRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(document -> mapToDTO(document, new DocumentDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public DocumentDTO get(final Integer id) {
        return documentRepository.findById(id)
                .map(document -> mapToDTO(document, new DocumentDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final DocumentDTO documentDTO) {
        final Document document = new Document();
        mapToEntity(documentDTO, document);
        return documentRepository.save(document).getId();
    }

    public void update(final Integer id, final DocumentDTO documentDTO) {
        final Document document = documentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(documentDTO, document);
        documentRepository.save(document);
    }

    public void delete(final Integer id) {
        documentRepository.deleteById(id);
    }

    private DocumentDTO mapToDTO(final Document document, final DocumentDTO documentDTO) {
        documentDTO.setId(document.getId());
        documentDTO.setContent(document.getContent());
        documentDTO.setEmbedding(document.getEmbedding());
        documentDTO.setFilename(document.getFilename());
        documentDTO.setSessionId(document.getSessionId());
        documentDTO.setUser(document.getUser() == null ? null : document.getUser().getId());
        return documentDTO;
    }

    private Document mapToEntity(final DocumentDTO documentDTO, final Document document) {
        document.setContent(documentDTO.getContent());
        document.setEmbedding(documentDTO.getEmbedding());
        document.setFilename(documentDTO.getFilename());
        document.setSessionId(documentDTO.getSessionId());
        final User user = documentDTO.getUser() == null ? null : userRepository.findById(documentDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
        document.setUser(user);
        return document;
    }

}
