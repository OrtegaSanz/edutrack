package com.nttdata.edutrack.infrastructure.adapter.in.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nttdata.edutrack.domain.exception.NotaInvalidaException;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(NotaInvalidaException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> notaInvalida(NotaInvalidaException ex) {
		return Map.of("error", ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> validacion(MethodArgumentNotValidException ex) {
		return Map.of("error", "La nota debe estar entre 0 y 20 y el curso/estudiante son obligatorios");
	}
}
