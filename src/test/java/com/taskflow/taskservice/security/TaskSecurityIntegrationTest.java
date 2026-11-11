package com.taskflow.taskservice.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties={
        "spring.datasource.url=jdbc:h2:mem:taskflow-security;DB_CLOSE_DELAY=-1;MODE=MySQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "taskflow.jwt.secret-base64=VGhpcy1pcy1hLXRlc3Qta2V5LWZvci1qd3QtMzItYnl0ZXM="
})
class TaskSecurityIntegrationTest {
    @Autowired MockMvc mvc;
    private final SecretKey key=new SecretKeySpec("This-is-a-test-key-for-jwt-32-bytes".getBytes(StandardCharsets.UTF_8),"HmacSHA256");
    private final JwtEncoder encoder=new NimbusJwtEncoder(new ImmutableSecret<>(key));

    private String token(String subject,Instant expiry) {
        JwtClaimsSet claims=JwtClaimsSet.builder().issuer("taskflow-user-service").subject(subject)
                .issuedAt(expiry.minusSeconds(300)).expiresAt(expiry).claim("roles",java.util.List.of("USER")).build();
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),claims)).getTokenValue();
    }

    @Test void rejectsMissingInvalidAndExpiredTokens() throws Exception {
        mvc.perform(get("/api/tasks")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/tasks").header("Authorization","Bearer not-a-jwt")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/tasks").header("Authorization","Bearer "+token("7",Instant.now().minusSeconds(300))))
                .andExpect(status().isUnauthorized());
    }

    @Test void taskOwnershipAlwaysComesFromTheValidatedJwt() throws Exception {
        String ownerToken=token("7",Instant.now().plusSeconds(300));
        String created=mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization","Bearer "+ownerToken)
                        .content("{\"title\":\"Private task\",\"userId\":999}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.userId").value(7)).andReturn().getResponse().getContentAsString();
        long taskId=new com.fasterxml.jackson.databind.ObjectMapper().readTree(created).get("id").asLong();
        mvc.perform(get("/api/tasks/"+taskId).header("Authorization","Bearer "+token("8",Instant.now().plusSeconds(300))))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/tasks").header("Authorization","Bearer "+token("8",Instant.now().plusSeconds(300))))
                .andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
    }
}
