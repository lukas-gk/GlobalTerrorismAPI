package com.NowakArtur97.GlobalTerrorismAPI.controller.target;

import com.NowakArtur97.GlobalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.TargetModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.controller.TargetController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.patch.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.violation.ViolationHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("TargetController_Tests")
class TargetControllerDeleteMethodTest {

    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";
    private final String LINK_WITH_PARAMETER = TARGET_BASE_PATH + "/" + "{id}";

    private MockMvc mockMvc;

    private GenericRestController<TargetModel, TargetDTO> targetController;

    @Mock
    private GenericService<TargetNode, TargetDTO> targetService;

    @Mock
    private TargetModelAssembler targetModelAssembler;

    @Mock
    private PagedResourcesAssembler<TargetNode> pagedResourcesAssembler;

    @Mock
    private PatchHelper patchHelper;

    @Mock
    private ViolationHelper<TargetNode, TargetDTO> violationHelper;

    private static TargetBuilder targetBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        targetBuilder = new TargetBuilder();
    }

    @BeforeEach
    private void setUp() {

        targetController = new TargetController(targetService, targetModelAssembler, pagedResourcesAssembler,
                patchHelper, violationHelper);

        mockMvc = MockMvcBuilders.standaloneSetup(targetController).setControllerAdvice(new GenericRestControllerAdvice())
                .build();
    }

    @Test
    void when_delete_existing_target_should_not_return_content() {

        Long targetId = 1L;

        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).build(ObjectType.NODE);

        when(targetService.delete(targetId)).thenReturn(Optional.of(targetNode));

        assertAll(() -> mockMvc.perform(delete(LINK_WITH_PARAMETER, targetId))
                        .andExpect(status().isNoContent())
                        .andExpect(jsonPath("$").doesNotExist()),
                () -> verify(targetService, times(1)).delete(targetId),
                () -> verifyNoMoreInteractions(targetService),
                () -> verifyNoInteractions(targetModelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_delete_target_but_target_does_not_exist_should_return_error_response() {

        Long targetId = 1L;

        when(targetService.delete(targetId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(delete(LINK_WITH_PARAMETER, targetId))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find TargetModel with id: " + targetId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(targetService, times(1)).delete(targetId),
                () -> verifyNoMoreInteractions(targetService),
                () -> verifyNoInteractions(targetModelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }
}