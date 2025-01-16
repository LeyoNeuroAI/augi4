package tech.intellibio.augi4.contact;


import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.util.NotFoundException;


@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(final ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public List<ContactDTO> findAll() {
        final List<Contact> contacts = contactRepository.findAll(Sort.by("id"));
        return contacts.stream()
                .map(contact -> mapToDTO(contact, new ContactDTO()))
                .toList();
    }

    public ContactDTO get(final Long id) {
        return contactRepository.findById(id)
                .map(contact -> mapToDTO(contact, new ContactDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ContactDTO contactDTO) {
        final Contact contact = new Contact();
        mapToEntity(contactDTO, contact);
        return contactRepository.save(contact).getId();
    }

    public void update(final Long id, final ContactDTO contactDTO) {
        final Contact contact = contactRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(contactDTO, contact);
        contactRepository.save(contact);
    }

    public void delete(final Long id) {
        contactRepository.deleteById(id);
    }

    private ContactDTO mapToDTO(final Contact contact, final ContactDTO contactDTO) {
        contactDTO.setId(contact.getId());
        contactDTO.setEmail(contact.getEmail());
        contactDTO.setSubject(contact.getSubject());
        contactDTO.setSummary(contact.getSummary());
        return contactDTO;
    }

    private Contact mapToEntity(final ContactDTO contactDTO, final Contact contact) {
        contact.setEmail(contactDTO.getEmail());
        contact.setSubject(contactDTO.getSubject());
        contact.setSummary(contactDTO.getSummary());
        return contact;
    }

}
