package com.NowakArtur97.GlobalTerrorismAPI.controller.event;

import com.NowakArtur97.GlobalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.EventController;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
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
@Tag("EventController_Tests")
class EventControllerDeleteMethodTest {

    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";
    private final String LINK_WITH_PARAMETER = EVENT_BASE_PATH + "/" + "{id}";

    private MockMvc mockMvc;

    private GenericRestController<EventModel, EventDTO> eventController;

    @Mock
    private GenericService<EventNode, EventDTO> eventService;

    @Mock
    private EventModelAssembler modelAssembler;

    @Mock
    private PagedResourcesAssembler<EventNode> pagedResourcesAssembler;

    @Mock
    private PatchHelper patchHelper;

    @Mock
    private ViolationHelper<EventNode, EventDTO> violationHelper;

    private static TargetBuilder targetBuilder;
    private static EventBuilder eventBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        targetBuilder = new TargetBuilder();
        eventBuilder = new EventBuilder();
    }

    @BeforeEach
    private void setUp() {

        eventController = new EventController(eventService, modelAssembler, pagedResourcesAssembler, patchHelper,
                violationHelper);

        mockMvc = MockMvcBuilders.standaloneSetup(eventController).setControllerAdvice(new GenericRestControllerAdvice())
                .build();
    }

    @Test
    void when_delete_existing_event_should_not_return_content() {

        Long eventId = 1L;

        EventNode eventNode = (EventNode) eventBuilder.withTarget(null).build(ObjectType.NODE);

        when(eventService.delete(eventId)).thenReturn(Optional.of(eventNode));

        assertAll(
                () -> mockMvc.perform(delete(LINK_WITH_PARAMETER, eventId))
                        .andExpect(status().isNoContent())
                        .andExpect(jsonPath("$").doesNotExist()),
                () -> verify(eventService, times(1)).delete(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_delete_existing_event_with_target_should_not_return_content() {

        Long eventId = 1L;

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

        when(eventService.delete(eventId)).thenReturn(Optional.of(eventNode));

        assertAll(
                () -> mockMvc.perform(delete(LINK_WITH_PARAMETER, eventId))
                        .andExpect(status().isNoContent())
                        .andExpect(jsonPath("$").doesNotExist()),
                () -> verify(eventService, times(1)).delete(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_delete_event_but_event_does_not_exist_should_return_error_response() {

        Long eventId = 1L;

        when(eventService.delete(eventId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(delete(LINK_WITH_PARAMETER, eventId)).andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find EventModel with id: " + eventId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(eventService, times(1)).delete(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }
}
