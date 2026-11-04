package com.nttdata.edutrack.infrastructure.adapter.in.web;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.edutrack.application.service.NotaService;
import com.nttdata.edutrack.infrastructure.adapter.in.web.dto.NotaResponse;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

	private final NotaService notas;

	public ReporteController(NotaService notas) {
		this.notas = notas;
	}

	@GetMapping("/notas")
	@PreAuthorize("hasRole('REPORTES')")
	public List<NotaResponse> todas() {
		return notas.todas().stream().map(NotaResponse::from).toList();
	}
}
