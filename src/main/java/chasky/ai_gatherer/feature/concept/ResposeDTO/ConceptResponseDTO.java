package chasky.ai_gatherer.feature.concept.ResposeDTO;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public class ConceptResponseDTO {
    @NotEmpty
    public List<ConceptDTO> concepts;

    public ConceptResponseDTO() {
    }

    public ConceptResponseDTO(List<ConceptDTO> concepts) {
        this.concepts = concepts;
    }

}
