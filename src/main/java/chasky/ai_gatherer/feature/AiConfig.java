package chasky.ai_gatherer.feature;

import java.net.URI;
import java.net.http.HttpRequest;

import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import chasky.ai_gatherer.feature.ResposeDTO.ConceptResponseDTO;
import jakarta.annotation.PostConstruct;

@Configuration
public class AiConfig {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String API_KEY = System.getenv("OPENAI_API_KEY");
    private final String API_URL = "https://api.openai.com/v1/responses";
    private final String system = "You are a university teacher who scientificly explains concepts and how they depend on adjacent concepts.";
    private final String model = "gpt-4o";
    private final Float temperature = 0f;
    private final String format = """
            {
                            "format": {
                              "type": "json_schema",
                              "name": "concept_response",
                              "schema": {
                                "type": "object",
                                "properties": {
                                  "concepts": {
                                    "type": "array",
                                    "items": {
                                      "type": "object",
                                      "properties": {
                                        "concept": { "type": "string" },
                                        "explanation": { "type": "string" }
                                      },
                                      "required": ["concept", "explanation"],
                                      "additionalProperties": false
                                    }
                                  }
                                },
                                "required": ["concepts"],
                                "additionalProperties": false
                              }
                    """;

    @PostConstruct
    private void checkKey() {
        if (API_KEY == null || API_KEY == "") {
            throw new IllegalStateException("API key was not loaded before construction");
        }

        System.out.println("API Key loaded successfully (first 5 chars: " + API_KEY.substring(0, 5) + "...)");
    }

    public String contructRequestBody(String prompt) {
        return String.format(
                """
                        {
                          "model": "%s",
                          "input": [
                            {
                              "role": "system",
                              "content": "%s"
                            },
                            {
                              "role": "user",
                              "content": "%s"
                            }
                          ],
                          "temperature": %s,
                          "text": %s,
                              "strict": true
                            }
                          }
                        }
                        """,
                model, system, prompt, temperature, format);
    }

    public HttpRequest constructRequest(String requestBody) {
        return HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(java.time.Duration.ofSeconds(30))
                .build();
    }

    public ConceptResponseDTO parseResponse(String responseBody) throws JsonMappingException, JsonProcessingException {
        JsonNode root = objectMapper.readTree(responseBody);

        String text = root.path("output")
                .get(0)
                .path("content")
                .get(0)
                .path("text")
                .asText();

        return objectMapper.readValue(text, ConceptResponseDTO.class);
    }
}
