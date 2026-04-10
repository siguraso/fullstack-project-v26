package edu.ntnu.idi.idatt2105.backend.common.config;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUpMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void protectedRouteRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/deviations"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Authentication required"));
    }

    @Test
    void authEndpointIsPublic() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invitationValidationEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/api/invitations/validate")
                        .param("token", "invalid-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void swaggerEndpointsArePublic() throws Exception {
        // Swagger is disabled in tests, so these endpoints may not return 200.
        // What we care about here is that security does not block anonymous access.
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(result -> assertStatusIsNotAuthRelated(result.getResponse().getStatus()));

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(result -> assertStatusIsNotAuthRelated(result.getResponse().getStatus()));
    }

    private static void assertStatusIsNotAuthRelated(int status) {
        assertTrue(status != 401 && status != 403,
                () -> "Expected endpoint to be publicly accessible, but got auth-related status: " + status);
    }

    @Test
    void insufficientRoleReturnsForbiddenApiResponse() throws Exception {
        mockMvc.perform(patch("/api/deviations/1")
                        .with(user("staff@example.com").roles("STAFF"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"OPEN\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @Test
    void staffCannotUploadDocuments() throws Exception {
        mockMvc.perform(multipart("/api/documents")
                        .file("file", "content".getBytes())
                        .param("title", "Manual")
                        .param("area", "GENERAL")
                        .with(user("staff@example.com").roles("STAFF")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Access denied"));
    }
}
