package com.NowakArtur97.GlobalTerrorismAPI.controller.target;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.NowakArtur97.GlobalTerrorismAPI.advice.TargetControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.TargetModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.TargetController;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationHelper;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("TargetController_Tests")
class TargetControllerDeleteMethodTest {

	private final String BASE_PATH = "http://localhost:8080/api/targets";

	private MockMvc mockMvc;

	private TargetController targetController;

	@Mock
	private TargetService targetService;

	@Mock
	private TargetModelAssembler targetModelAssembler;

	@Mock
	private PagedResourcesAssembler<TargetNode> pagedResourcesAssembler;

	@Mock
	private PatchHelper patchHelper;

	@Mock
	private ViolationHelper violationHelper;

	@BeforeEach
	private void setUp() {

		targetController = new TargetController(targetService, targetModelAssembler, pagedResourcesAssembler,
				patchHelper, violationHelper);

		mockMvc = MockMvcBuilders.standaloneSetup(targetController).setControllerAdvice(new TargetControllerAdvice())
				.build();
	}

	@Test
	void when_delete_existing_target_should_return_target() {

		Long targetId = 1L;
		String targetName = "target";
		TargetNode targetNode = new TargetNode(targetId, targetName);
		TargetModel targetModel = new TargetModel(targetId, targetName);

		String pathToLink = BASE_PATH + "/" + targetId.intValue();
		Link link = new Link(pathToLink);
		targetModel.add(link);

		String linkWithParameter = BASE_PATH + "/" + "{id}";

		when(targetService.delete(targetId)).thenReturn(Optional.of(targetNode));
		when(targetModelAssembler.toModel(targetNode)).thenReturn(targetModel);

		assertAll(() -> mockMvc.perform(delete(linkWithParameter, targetId)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("links[0].href", is(pathToLink))).andExpect(jsonPath("id", is(targetId.intValue())))
				.andExpect(jsonPath("target", is(targetName))), () -> verify(targetService, times(1)).delete(targetId),
				() -> verifyNoMoreInteractions(targetService),
				() -> verify(targetModelAssembler, times(1)).toModel(targetNode),
				() -> verifyNoMoreInteractions(targetModelAssembler),
				() -> verifyNoInteractions(pagedResourcesAssembler), () -> verifyNoInteractions(patchHelper),
				() -> verifyNoInteractions(violationHelper));
	}

	@Test
	void when_delete_target_but_target_not_exists_should_return_error_response() {

		Long targetId = 1L;

		String linkWithParameter = BASE_PATH + "/" + "{id}";

		when(targetService.delete(targetId)).thenReturn(Optional.empty());

		assertAll(
				() -> mockMvc.perform(delete(linkWithParameter, targetId)).andExpect(status().isNotFound())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("timestamp").isNotEmpty()).andExpect(content().json("{'status': 404}"))
						.andExpect(jsonPath("errors[0]", is("Could not find target with id: " + targetId))),
				() -> verify(targetService, times(1)).delete(targetId), () -> verifyNoMoreInteractions(targetService),
				() -> verifyNoInteractions(targetModelAssembler), () -> verifyNoInteractions(pagedResourcesAssembler),
				() -> verifyNoInteractions(patchHelper), () -> verifyNoInteractions(violationHelper));
	}
}