package chasky.ai_gatherer.feature.concept;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import chasky.ai_gatherer.feature.concept.ResposeDTO.ConceptResponseDTO;
import jakarta.validation.constraints.Size;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {
    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping
    public ConceptResponseDTO postMethodName(@Size(min = 1, max = 500) String prompt) throws IOException, InterruptedException {
        return aiService.promptAi(prompt);
    }

}
