package com.NowakArtur97.GlobalTerrorismAPI.controller.bulk;

import com.NowakArtur97.GlobalTerrorismAPI.testUtil.mapper.ObjectTestMapper;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.github.wnameless.spring.bulkapi.BulkOperation;
import com.github.wnameless.spring.bulkapi.BulkRequest;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("BulkController_Tests")
class BulkControllerTest {

    private final String BULK_BASE_PATH = "http://localhost:8080/api/v1/bulk";
    private final String GROUP_BASE_PATH = "/api/v1/groups";
    private final String EVENT_BASE_PATH = "/api/v1/events";
    private final String TARGET_BASE_PATH = "/api/v1/targets";

    @Value("${spring.bulk.api.limit}")
    private int bulkOperationsLimit;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void when_valid_bulk_request_should_return_array_of_responses() {

        BulkOperation bulkGetGroupOperation = new BulkOperation();
        bulkGetGroupOperation.setMethod("GET");
        bulkGetGroupOperation.setUrl(GROUP_BASE_PATH);

        BulkOperation bulkGetEventsOperation = new BulkOperation();
        bulkGetEventsOperation.setMethod("GET");
        bulkGetEventsOperation.setUrl(EVENT_BASE_PATH);

        BulkOperation bulkGetTargetsOperation = new BulkOperation();
        bulkGetTargetsOperation.setMethod("GET");
        bulkGetTargetsOperation.setUrl(TARGET_BASE_PATH);

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.setOperations(List.of(bulkGetGroupOperation, bulkGetEventsOperation, bulkGetTargetsOperation));

        assertAll(
                () -> mockMvc.perform(post(BULK_BASE_PATH).content(ObjectTestMapper.asJsonString(bulkRequest))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("results").isArray())
                        .andExpect(jsonPath("results", hasSize(3)))
                        .andExpect(jsonPath("results[0].status", is(200)))
                        .andExpect(jsonPath("results[0].body").exists())
                        .andExpect(jsonPath("results[0].headers").exists())
                        .andExpect(jsonPath("results[1].status", is(200)))
                        .andExpect(jsonPath("results[1].body").exists())
                        .andExpect(jsonPath("results[1].headers").exists())
                        .andExpect(jsonPath("results[2].status", is(200)))
                        .andExpect(jsonPath("results[2].body").exists())
                        .andExpect(jsonPath("results[2].headers").exists()));
    }

    @Test
    void when_invalid_bulk_request_with_exceeded_number_of_operations_should_return_error_response() {

        BulkOperation bulkGetGroupOperation = new BulkOperation();
        bulkGetGroupOperation.setMethod("GET");
        bulkGetGroupOperation.setUrl(GROUP_BASE_PATH);

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.setOperations(Collections.nCopies(bulkOperationsLimit + 1, bulkGetGroupOperation));

        assertAll(
                () -> mockMvc.perform(post(BULK_BASE_PATH).content(ObjectTestMapper.asJsonString(bulkRequest))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isPayloadTooLarge())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(content().string("Bulk operations exceed the limitation(" + bulkOperationsLimit + ")"))
        );
    }

    @Test
    void when_invalid_bulk_request_with_invalid_url_should_return_error_response() {

        String invalidUrl = "some invalid url";

        BulkOperation invalidOperation = new BulkOperation();
        invalidOperation.setMethod("GET");
        invalidOperation.setUrl(invalidUrl);

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.setOperations(List.of(invalidOperation));

        assertAll(
                () -> mockMvc.perform(post(BULK_BASE_PATH).content(ObjectTestMapper.asJsonString(bulkRequest))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnprocessableEntity())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(content().string("Invalid URL(/" + invalidUrl + ") exists in this bulk request"))
        );
    }
}