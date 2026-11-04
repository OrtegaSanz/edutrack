package com.nttdata.edutrack.infrastructure.adapter.out.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.nttdata.edutrack.domain.model.Nota;
import com.nttdata.edutrack.domain.port.NotaRepository;

@Repository
public class InMemoryNotaRepository implements NotaRepository {

	static final String VALERIA = "8f14e45f-ea9d-4e1a-9c0a-000000000001";
	static final String BRUNO = "8f14e45f-ea9d-4e1a-9c0a-000000000002";

	private final Map<String, Nota> notas = new ConcurrentHashMap<>();

	public InMemoryNotaRepository() {
		guardar(new Nota("N-001", "C-101", VALERIA, 17));
		guardar(new Nota("N-002", "C-101", BRUNO, 14));
	}

	@Override
	public List<Nota> findAll() {
		return List.copyOf(notas.values());
	}

	@Override
	public List<Nota> findByEstudianteId(String estudianteId) {
		List<Nota> resultado = new ArrayList<>();
		for (Nota nota : notas.values()) {
			if (nota.estudianteId().equals(estudianteId)) {
				resultado.add(nota);
			}
		}
		return List.copyOf(resultado);
	}

	@Override
	public Nota save(Nota nota) {
		guardar(nota);
		return nota;
	}

	private void guardar(Nota nota) {
		notas.put(nota.id(), nota);
	}
}
