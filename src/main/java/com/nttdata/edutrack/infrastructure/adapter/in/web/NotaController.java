package com.nttdata.edutrack.infrastructure.adapter.in.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.edutrack.application.service.NotaService;
import com.nttdata.edutrack.infrastructure.adapter.in.web.dto.NotaResponse;
import com.nttdata.edutrack.infrastructure.adapter.in.web.dto.RegistrarNotaRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notas")
public class NotaController {

	private final NotaService notas;

	public NotaController(NotaService notas) {
		this.notas = notas;
	}

	@GetMapping
	@PreAuthorize("hasRole('ESTUDIANTE')")
	public List<NotaResponse> mias(@AuthenticationPrincipal Jwt jwt) {
		return notas.deEstudiante(jwt.getSubject()).stream().map(NotaResponse::from).toList();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('DOCENTE')")
	public NotaResponse registrar(@Valid @RequestBody RegistrarNotaRequest request) {
		return NotaResponse.from(notas.registrar(request.cursoId(), request.estudianteId(), request.valor()));
	}
}
