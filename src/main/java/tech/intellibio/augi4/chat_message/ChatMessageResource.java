package tech.intellibio.augi4.chat_message;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/chatMessages", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatMessageResource {

    private final ChatMessageService chatMessageService;

    public ChatMessageResource(final ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @GetMapping
    public ResponseEntity<List<ChatMessageDTO>> getAllChatMessages() {
        return ResponseEntity.ok(chatMessageService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatMessageDTO> getChatMessage(
            @PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(chatMessageService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createChatMessage(
            @RequestBody @Valid final ChatMessageDTO chatMessageDTO) {
        final Integer createdId = chatMessageService.create(chatMessageDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateChatMessage(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final ChatMessageDTO chatMessageDTO) {
        chatMessageService.update(id, chatMessageDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteChatMessage(@PathVariable(name = "id") final Integer id) {
        chatMessageService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
