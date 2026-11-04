package com.nttdata.edutrack.domain.model;

import com.nttdata.edutrack.domain.exception.NotaInvalidaException;

public record Nota(String id, String cursoId, String estudianteId, int valor) {

	public Nota {
		if (valor < 0 || valor > 20) {
			throw new NotaInvalidaException("La nota debe estar entre 0 y 20");
		}
	}
}
