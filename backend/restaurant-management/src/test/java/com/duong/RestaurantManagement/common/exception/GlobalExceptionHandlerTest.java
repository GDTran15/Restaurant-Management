package com.duong.RestaurantManagement.common.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new TestController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @Test
    void rendersProblemDetailsWithStatusAndRequestPath() throws Exception {
        mvc.perform(get("/test/missing"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Food not found"))
                .andExpect(jsonPath("$.instance").value("/test/missing"))
                .andExpect(jsonPath("$.message").doesNotExist());
    }

    @Test
    void preservesDuplicateFieldErrorsAsAnExtension() throws Exception {
        mvc.perform(get("/test/duplicate"))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.errors.name").value("Already exists"));
    }

    @Test
    void preservesValidationFieldErrorsAsAnExtension() throws Exception {
        mvc.perform(post("/test/validate").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.errors.name").value("Name is required"));
    }

    @RestController
    static class TestController {
        @GetMapping("/test/missing")
        void missing() {
            throw new ResourceNotFoundException("Food not found");
        }

        @GetMapping("/test/duplicate")
        void duplicate() {
            throw new DuplicateResourceException(Map.of("name", "Already exists"));
        }

        @PostMapping("/test/validate")
        void validate(@Valid @RequestBody Input input) {
        }
    }

    record Input(@NotBlank(message = "Name is required") String name) {
    }
}
