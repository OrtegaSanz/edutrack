package com.nttdata.edutrack.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nttdata.edutrack.domain.model.Curso;
import com.nttdata.edutrack.domain.port.CursoRepository;

@Service
public class CursoService {

	private final CursoRepository cursos;

	public CursoService(CursoRepository cursos) {
		this.cursos = cursos;
	}

	public List<Curso> listar() {
		return cursos.findAll();
	}
}
