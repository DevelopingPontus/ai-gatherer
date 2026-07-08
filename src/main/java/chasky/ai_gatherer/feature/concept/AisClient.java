package chasky.ai_gatherer.feature.concept;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

import chasky.ai_gatherer.feature.concept.ResposeDTO.ConceptResponseDTO;
import jakarta.annotation.PostConstruct;

@Component
public class AisClient {

    private final AiConfig aiConfig;

    private final Set<Integer> RETRYABLE_STATUS = Set.of(429, 500, 502, 503, 504);

    public AisClient(AiConfig aiConfig) {
        this.aiConfig = aiConfig;
    }

    @PostConstruct
    private void checkKey() {
        if (System.getenv("OPENAI_API_KEY") == null || System.getenv("OPENAI_API_KEY") == "") {
            throw new IllegalStateException("API key was not loaded before construction");
        }

        System.out.println("API Key loaded successfully (first 5 chars: " + System.getenv("OPENAI_API_KEY").substring(0, 5) + "...)");
    }

    /**
     * Makes a request to the OpenAI API using the provided key.
     * 
     * @param prompt The text prompt to send to the model.
     * @return The response text from the API.
     * @throws IOException          If there is a network issue.
     * @throws InterruptedException If the request is interrupted.
     */
    public ConceptResponseDTO promptAi(String prompt) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .build();

        String requestBody = aiConfig.contructRequestBody(prompt);

        HttpRequest request = aiConfig.constructRequest(requestBody);

        HttpResponse<String> response = sendRequest(client, request);

        if (response.statusCode() != 200) {
            throw new IOException("Failed to get response from OpenAI");
        }

        return aiConfig.parseResponse(response.body());
    }

    private HttpResponse<String> sendRequest(HttpClient client, HttpRequest request)
            throws IOException, InterruptedException {

        int maxRetries = 5;
        long baseBackoffMillis = 500;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (!RETRYABLE_STATUS.contains(response.statusCode())) {
                return response;
            }

            if (attempt == maxRetries) {
                throw new IOException("Failed after retries, last status: " + response.statusCode());
            }

            long jitter = ThreadLocalRandom.current().nextLong(0, 250);
            long sleepMillis = (baseBackoffMillis * (1L << (attempt - 1))) + jitter;

            Thread.sleep(sleepMillis);
        }

        throw new IOException("Unreachable");
    }

}