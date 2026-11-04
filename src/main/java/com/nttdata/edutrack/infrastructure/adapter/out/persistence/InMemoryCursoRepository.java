package com.nttdata.edutrack.infrastructure.adapter.out.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.nttdata.edutrack.domain.model.Curso;
import com.nttdata.edutrack.domain.port.CursoRepository;

@Repository
public class InMemoryCursoRepository implements CursoRepository {

	private final Map<String, Curso> cursos = new ConcurrentHashMap<>();

	public InMemoryCursoRepository() {
		guardar(new Curso("C-101", "Matemáticas"));
		guardar(new Curso("C-102", "Historia"));
		guardar(new Curso("C-103", "Física"));
	}

	@Override
	public List<Curso> findAll() {
		return List.copyOf(cursos.values());
	}

	@Override
	public Optional<Curso> findById(String id) {
		return Optional.ofNullable(cursos.get(id));
	}

	private void guardar(Curso curso) {
		cursos.put(curso.id(), curso);
	}
}
