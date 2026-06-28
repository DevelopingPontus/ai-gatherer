# AI Gatherer

`ai-gatherer` is a Spring Boot application that forwards user prompts to the OpenAI Responses API and returns a JSON-structured explanation of concepts.

## Features

- Spring Boot 4.x application with Java 25
- Uses OpenAI Responses API (`gpt-4o`) for concept explanation
- Enforces a JSON schema response shape for predictable output
- Includes retry handling for transient OpenAI HTTP errors

## Requirements

- Java 25
- Maven or the included Maven wrapper (`./mvnw`)
- `OPENAI_API_KEY` environment variable set with a valid OpenAI API key

## Setup

1. Clone the repository.
1. Set the OpenAI API key in your shell:

```bash
export OPENAI_API_KEY="your-openai-api-key"
```

1. Run the application:

```bash
./mvnw spring-boot:run
```

or with Maven if installed:

```bash
mvn spring-boot:run
```

## API

- Endpoint: `POST /api/v1/ai`
- Expected input: prompt text describing the concept or topic to explain
- Output: JSON object containing a list of concept entries with `concept` and `explanation`

Example response shape:

```json
{
  "concepts": [
    {
      "concept": "Example Concept",
      "explanation": "A scientific explanation of that concept."
    }
  ]
}
```

## Output Reliability

This project is designed with several reliability safeguards for the OpenAI response pipeline:

- Uses OpenAI Responses API with a strict `json_schema` format to reduce output variance.
- The request includes `strict: true` and a JSON schema that requires `concepts`, each with `concept` and `explanation`.
- The client retries transient API failures for status codes `429`, `500`, `502`, `503`, and `504` using exponential backoff with jitter.
- Responses are parsed as JSON and mapped to `ConceptResponseDTO`, which enforces that each returned item contains the expected fields.

> Note: Even with schema enforcement and retries, AI-generated responses can still vary over time. For production use, add request validation, fallback handling, and monitoring for malformed or unexpected responses.

## Notes

- The application loads `OPENAI_API_KEY` from the environment and fails startup if the key is missing.
- The OpenAI request is built in `AiConfig` and parsed in `AisClient`.
- The service is intentionally simple and can be extended with request validation, richer input models, or additional endpoint behavior.
