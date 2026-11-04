package com.nttdata.edutrack.infrastructure.adapter.in.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.edutrack.application.service.CursoService;
import com.nttdata.edutrack.infrastructure.adapter.in.web.dto.CursoResponse;

@RestController
@RequestMapping("/api/publico")
public class CursoPublicoController {

	private final CursoService cursos;

	public CursoPublicoController(CursoService cursos) {
		this.cursos = cursos;
	}

	@GetMapping("/cursos")
	public List<CursoResponse> listar() {
		return cursos.listar().stream().map(CursoResponse::from).toList();
	}
}
