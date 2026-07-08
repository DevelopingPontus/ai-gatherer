package chasky.ai_gatherer.feature.concept.ResposeDTO;

import jakarta.validation.constraints.NotBlank;

public class ConceptDTO {
    @NotBlank
    public String concept;
    @NotBlank
    public String explanation;

    public ConceptDTO() {
    }

    public ConceptDTO(String concept, String explanation) {
        this.concept = concept;
        this.explanation = explanation;
    }

}
