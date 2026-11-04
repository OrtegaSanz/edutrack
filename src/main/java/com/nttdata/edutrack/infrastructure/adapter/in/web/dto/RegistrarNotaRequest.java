package com.nttdata.edutrack.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RegistrarNotaRequest(
		@NotBlank String cursoId,
		@NotBlank String estudianteId,
		@Min(0) @Max(20) int valor) {
}
