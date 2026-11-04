package com.nttdata.edutrack.infrastructure.adapter.in.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiSecurityTest {

	private static final String VALERIA = "8f14e45f-ea9d-4e1a-9c0a-000000000001";

	@Autowired
	MockMvc mockMvc;

	@MockBean
	JwtDecoder jwtDecoder;

	@Test
	void catalogoPublicoSinToken() throws Exception {
		mockMvc.perform(get("/api/publico/cursos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").exists());
	}

	@Test
	void notasSinTokenEs401() throws Exception {
		mockMvc.perform(get("/api/notas"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void estudianteSoloVeSusNotas() throws Exception {
		mockMvc.perform(get("/api/notas").with(jwt()
						.jwt(token -> token.subject(VALERIA).claim("realm_access", Map.of("roles", List.of("ESTUDIANTE"))))
						.authorities(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].estudianteId").value(VALERIA));
	}

	@Test
	void docenteNoLeeNotasDeEstudiantes() throws Exception {
		mockMvc.perform(get("/api/notas").with(jwt()
						.jwt(token -> token.claim("realm_access", Map.of("roles", List.of("DOCENTE"))))
						.authorities(new SimpleGrantedAuthority("ROLE_DOCENTE"))))
				.andExpect(status().isForbidden());
	}

	@Test
	void docenteRegistraNota() throws Exception {
		mockMvc.perform(post("/api/notas")
						.with(jwt()
								.jwt(token -> token.claim("realm_access", Map.of("roles", List.of("DOCENTE"))))
								.authorities(new SimpleGrantedAuthority("ROLE_DOCENTE")))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"cursoId":"C-102","estudianteId":"%s","valor":18}
								""".formatted(VALERIA)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.valor").value(18));
	}

	@Test
	void reportesLeeTodasLasNotas() throws Exception {
		mockMvc.perform(get("/api/reportes/notas").with(jwt()
						.jwt(token -> token.claim("realm_access", Map.of("roles", List.of("REPORTES"))))
						.authorities(new SimpleGrantedAuthority("ROLE_REPORTES"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").exists());
	}

	@Test
	void estudianteNoAccedeAReportes() throws Exception {
		mockMvc.perform(get("/api/reportes/notas").with(jwt()
						.jwt(token -> token.claim("realm_access", Map.of("roles", List.of("ESTUDIANTE"))))
						.authorities(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))))
				.andExpect(status().isForbidden());
	}
}
