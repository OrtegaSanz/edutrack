package com.nttdata.edutrack.infrastructure.adapter.in.web.dto;

import com.nttdata.edutrack.domain.model.Nota;

public record NotaResponse(String id, String cursoId, String estudianteId, int valor) {

	public static NotaResponse from(Nota nota) {
		return new NotaResponse(nota.id(), nota.cursoId(), nota.estudianteId(), nota.valor());
	}
}
