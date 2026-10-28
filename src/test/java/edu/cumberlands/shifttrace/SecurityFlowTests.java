package edu.cumberlands.shifttrace;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityFlowTests {
    @Autowired MockMvc mvc;

    @Test
    void anonymousUserCannotReadNotes() throws Exception {
        mvc.perform(get("/api/notes")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "analyst", roles = "ANALYST")
    void exactSearchReturnsOneRecord() throws Exception {
        mvc.perform(get("/api/notes").param("assetTag", "LAB-RACK-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assetTag").value("LAB-RACK-01"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(username = "analyst", roles = "ANALYST")
    void sqlInjectionInputIsJustData() throws Exception {
        mvc.perform(get("/api/notes").param("assetTag", "' OR '1'='1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }


    @Test
    @WithMockUser(username = "analyst", roles = "ANALYST")
    void searchAliasReturnsOneRecord() throws Exception {
        mvc.perform(get("/api/notes/search").param("assetTag", "LAB-RACK-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assetTag").value("LAB-RACK-01"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(username = "analyst", roles = "ANALYST")
    void injectionShapedInputWithValidPrefixReturnsNothing() throws Exception {
        mvc.perform(get("/api/notes/search")
                .param("assetTag", "LAB-RACK-01' OR '1'='1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void anonymousUserCannotSearchNotes() throws Exception {
        mvc.perform(get("/api/notes/search").param("assetTag", "LAB-RACK-01"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "analyst", roles = "ANALYST")
    void unauthorisedWriteMethodIsBlocked() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/api/notes"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "analyst", roles = "ANALYST")
    void analystCannotOpenAdminSummary() throws Exception {
        mvc.perform(get("/api/admin/summary"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void administratorCanSeeSummary() throws Exception {
        mvc.perform(get("/api/admin/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storedNoteCount").value(3));
    }
}
