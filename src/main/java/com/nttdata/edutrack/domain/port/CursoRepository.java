package com.nttdata.edutrack.domain.port;

import java.util.List;
import java.util.Optional;

import com.nttdata.edutrack.domain.model.Curso;

public interface CursoRepository {

	List<Curso> findAll();

	Optional<Curso> findById(String id);
}
