package chasky.ai_gatherer.feature.concept;

import java.io.IOException;

import org.springframework.stereotype.Service;

import chasky.ai_gatherer.feature.concept.ResposeDTO.ConceptResponseDTO;

@Service
public class AiService {
    private final AisClient aisClient;

    public AiService(AisClient aisClient) {
        this.aisClient = aisClient;
    }
    
    public ConceptResponseDTO promptAi(String prompt) throws IOException, InterruptedException  {
        return aisClient.promptAi(prompt);
    }
    
}
