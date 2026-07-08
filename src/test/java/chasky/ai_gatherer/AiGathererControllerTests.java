package chasky.ai_gatherer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.io.IOException;
import java.util.List;

import chasky.ai_gatherer.feature.concept.ResposeDTO.ConceptDTO;
import chasky.ai_gatherer.feature.concept.ResposeDTO.ConceptResponseDTO;
import chasky.ai_gatherer.feature.concept.AisClient;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = "openai.api.key=test_key_12345")
class AiGathererControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AisClient aisClient;

	private String uri = "/api/v1/ai";
	private MediaType mt = MediaType.APPLICATION_JSON;

	@BeforeEach
	void setUp() throws IOException, InterruptedException {
		Mockito.when(aisClient.promptAi(anyString())).thenReturn(
				new ConceptResponseDTO(
						List.of(new ConceptDTO("dockerfile",
								"A Dockerfile is a text document that contains all the commands to assemble an image. It automates the process of creating Docker images, which are used to run applications in isolated environments called containers."),
								new ConceptDTO("Docker Image",
										"A Docker image is a lightweight, standalone, and executable software package that includes everything needed to run a piece of software, including the code, runtime, libraries, and environment variables. Dockerfiles are used to build these images."))));
	}

	// ✅ HAPPY PATH TESTS
	@Test
	@WithMockUser(roles = "USER")
	void shouldReturnOkStatus() throws Exception {
		performPost("dockerfile").andExpect(status().isOk());
	}

	@Test
	@WithMockUser(roles = "USER")
	void shouldReturnValidResponseDTO() throws Exception {
		String result = getResultString(performPost("dockerfile"));
		assertTrue(result.contains("dockerfile"));
	}

	// 401 Unauthorized - No authentication
	@Test
	void shouldReturn401WithoutAuthentication() throws Exception {
		mockMvc.perform(post(uri)
				.param("prompt", "test")
				.contentType(mt))
				.andExpect(status().isUnauthorized());
	}

	// 403 Forbidden - Insufficient roles
	@Test
	@WithMockUser(roles = "GUEST")
	void shouldReturn403ForInsufficientRole() throws Exception {
		mockMvc.perform(post(uri)
				.param("prompt", "test")
				.contentType(mt))
				.andExpect(status().isForbidden());
	}

	// 405 Method Not Allowed
	@Test
	@WithMockUser(roles = "USER")
	void shouldReturn405ForWrongHttpMethod() throws Exception {
		mockMvc.perform(get(uri)
				.param("prompt", "test")
				.contentType(mt))
				.andExpect(status().isMethodNotAllowed());
	}

	// 500 Internal Server Error - Generic Exception
	@Test
	@WithMockUser(roles = "USER")
	void shouldReturn500ForGenericException() throws Exception {
		Mockito.when(aisClient.promptAi(anyString()))
				.thenThrow(new RuntimeException("Unexpected error"));

		mockMvc.perform(post(uri)
				.param("prompt", "test")
				.contentType(mt))
				.andExpect(status().isInternalServerError());
	}

	// HELPER METHODS
	private ResultActions performPost(String query) throws Exception {
		return mockMvc.perform(post(uri)
				.param("prompt", query)
				.contentType(mt));
	}

	private String getResultString(ResultActions resultActions) throws Exception {
		return resultActions
				.andReturn()
				.getResponse()
				.getContentAsString();
	}
}
