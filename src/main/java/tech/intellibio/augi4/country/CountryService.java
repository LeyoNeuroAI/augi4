package tech.intellibio.augi4.country;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.intellibio.augi4.program.Program;
import tech.intellibio.augi4.program.ProgramRepository;
import tech.intellibio.augi4.project.Project;
import tech.intellibio.augi4.project.ProjectRepository;
import tech.intellibio.augi4.user.User;
import tech.intellibio.augi4.user.UserRepository;
import tech.intellibio.augi4.util.NotFoundException;
import tech.intellibio.augi4.util.ReferencedWarning;


@Service
public class CountryService {

    private final CountryRepository countryRepository;
    private final ProgramRepository programRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public CountryService(final CountryRepository countryRepository,
            final ProgramRepository programRepository, final UserRepository userRepository,
            final ProjectRepository projectRepository) {
        this.countryRepository = countryRepository;
        this.programRepository = programRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }
    
     public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    public Page<CountryDTO> findAll(final String filter, final Pageable pageable) {
        Page<Country> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = countryRepository.findAllById(longFilter, pageable);
        } else {
            page = countryRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(country -> mapToDTO(country, new CountryDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public CountryDTO get(final Long id) {
        return countryRepository.findById(id)
                .map(country -> mapToDTO(country, new CountryDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CountryDTO countryDTO) {
        final Country country = new Country();
        mapToEntity(countryDTO, country);
        return countryRepository.save(country).getId();
    }

    public void update(final Long id, final CountryDTO countryDTO) {
        final Country country = countryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(countryDTO, country);
        countryRepository.save(country);
    }

    public void delete(final Long id) {
        countryRepository.deleteById(id);
    }

    private CountryDTO mapToDTO(final Country country, final CountryDTO countryDTO) {
        countryDTO.setId(country.getId());
        countryDTO.setCode(country.getCode());
        countryDTO.setName(country.getName());
        countryDTO.setProvince(country.getProvince());
        countryDTO.setStatus(country.getStatus());
        return countryDTO;
    }

    private Country mapToEntity(final CountryDTO countryDTO, final Country country) {
        country.setCode(countryDTO.getCode());
        country.setName(countryDTO.getName());
        country.setProvince(countryDTO.getProvince());
        country.setStatus(countryDTO.getStatus());
        return country;
    }

    public boolean codeExists(final String code) {
        return countryRepository.existsByCodeIgnoreCase(code);
    }

    public boolean nameExists(final String name) {
        return countryRepository.existsByNameIgnoreCase(name);
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Country country = countryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Program countryProgram = programRepository.findFirstByCountry(country);
        if (countryProgram != null) {
            referencedWarning.setKey("country.program.country.referenced");
            referencedWarning.addParam(countryProgram.getId());
            return referencedWarning;
        }
        final User countryUser = userRepository.findFirstByCountry(country);
        if (countryUser != null) {
            referencedWarning.setKey("country.user.country.referenced");
            referencedWarning.addParam(countryUser.getId());
            return referencedWarning;
        }
        final Project project1Project = projectRepository.findFirstByProject1(country);
        if (project1Project != null) {
            referencedWarning.setKey("country.project.project1.referenced");
            referencedWarning.addParam(project1Project.getId());
            return referencedWarning;
        }
        return null;
    }

}
