package chasky.ai_gatherer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import chasky.ai_gatherer.feature.AiConfig;
import chasky.ai_gatherer.feature.AisClient;
import chasky.ai_gatherer.feature.ResposeDTO.ConceptDTO;
import chasky.ai_gatherer.feature.ResposeDTO.ConceptResponseDTO;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = "openai.api.key=test_key_12345")
class AiGathererControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AiConfig aiConfig;

	@MockitoBean
	private AisClient aisClient;

	private String uri = "/api/v1/ai";

	private MediaType mt = MediaType.APPLICATION_JSON;

	@BeforeEach
	private void setUp() throws IOException, InterruptedException {

		Mockito.when(aisClient.promptAi(anyString())).thenReturn(
				new ConceptResponseDTO(
						List.of(new ConceptDTO("dockerfile",
								"A Dockerfile is a text document that contains all the commands to assemble an image. It automates the process of creating Docker images, which are used to run applications in isolated environments called containers."),
								new ConceptDTO("Docker Image",
										"A Docker image is a lightweight, standalone, and executable software package that includes everything needed to run a piece of software, including the code, runtime, libraries, and environment variables. Dockerfiles are used to build these images."))));

	}

	@Test
	void shouldReturnOkStatus() throws Exception {
		String query = "dockerfile";
		performPost(query).andExpect(status().isOk());
	}

	@Test
	void shouldReturnValidResponseDTO() throws Exception {
		String query = "dockerfile";
		ResultActions result = performPost(query);
		System.out.println("---------------------");
		System.out.println(getResultString(result));
		assertTrue(getResultString(result).contains("d"));
	}

	private ResultActions performPost(String query) throws Exception {
		return mockMvc.perform(post(uri)
				.contentType(mt)
				.content(query));
	}

	private String getResultString(ResultActions resultActions) throws Exception {
		return resultActions
				.andReturn()
				.getResponse()
				.getContentAsString();
	}
}
