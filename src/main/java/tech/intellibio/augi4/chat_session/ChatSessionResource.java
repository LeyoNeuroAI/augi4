package tech.intellibio.augi4.chat_session;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.intellibio.augi4.util.ReferencedException;
import tech.intellibio.augi4.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/chatSessions", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatSessionResource {

    private final ChatSessionService chatSessionService;

    public ChatSessionResource(final ChatSessionService chatSessionService) {
        this.chatSessionService = chatSessionService;
    }

    @Operation(
            parameters = {
                    @Parameter(
                            name = "page",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = Integer.class)
                    ),
                    @Parameter(
                            name = "size",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = Integer.class)
                    ),
                    @Parameter(
                            name = "sort",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = String.class)
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Page<ChatSessionDTO>> getAllChatSessions(
            @RequestParam(name = "filter", required = false) final String filter,
            @Parameter(hidden = true) @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable) {
        return ResponseEntity.ok(chatSessionService.findAll(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatSessionDTO> getChatSession(
            @PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(chatSessionService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createChatSession(
            @RequestBody @Valid final ChatSessionDTO chatSessionDTO) {
        final Integer createdId = chatSessionService.create(chatSessionDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateChatSession(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final ChatSessionDTO chatSessionDTO) {
        chatSessionService.update(id, chatSessionDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteChatSession(@PathVariable(name = "id") final Integer id) {
        final ReferencedWarning referencedWarning = chatSessionService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        chatSessionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
