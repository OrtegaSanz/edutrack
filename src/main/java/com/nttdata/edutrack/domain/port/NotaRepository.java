package com.nttdata.edutrack.domain.port;

import java.util.List;

import com.nttdata.edutrack.domain.model.Nota;

public interface NotaRepository {

	List<Nota> findAll();

	List<Nota> findByEstudianteId(String estudianteId);

	Nota save(Nota nota);
}
