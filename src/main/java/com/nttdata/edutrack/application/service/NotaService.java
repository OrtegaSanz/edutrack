package com.nttdata.edutrack.application.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.nttdata.edutrack.domain.exception.NotaInvalidaException;
import com.nttdata.edutrack.domain.model.Nota;
import com.nttdata.edutrack.domain.port.CursoRepository;
import com.nttdata.edutrack.domain.port.NotaRepository;

@Service
public class NotaService {

	private final NotaRepository notas;
	private final CursoRepository cursos;
	private final AtomicInteger secuencia = new AtomicInteger(2);

	public NotaService(NotaRepository notas, CursoRepository cursos) {
		this.notas = notas;
		this.cursos = cursos;
	}

	public List<Nota> deEstudiante(String subject) {
		return notas.findByEstudianteId(subject);
	}

	public List<Nota> todas() {
		return notas.findAll();
	}

	public Nota registrar(String cursoId, String estudianteId, int valor) {
		cursos.findById(cursoId)
				.orElseThrow(() -> new NotaInvalidaException("El curso no existe: " + cursoId));
		String id = "N-" + String.format("%03d", secuencia.incrementAndGet());
		return notas.save(new Nota(id, cursoId, estudianteId, valor));
	}
}
