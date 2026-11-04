package com.nttdata.edutrack.infrastructure.adapter.in.web.dto;

import com.nttdata.edutrack.domain.model.Curso;

public record CursoResponse(String id, String nombre) {

	public static CursoResponse from(Curso curso) {
		return new CursoResponse(curso.id(), curso.nombre());
	}
}
