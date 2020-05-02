package br.com.controlededespesas.utils;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.controlededespesas.models.Perfil;
import br.com.controlededespesas.repositories.PerfilRepository;

@Component
public class DummyData {

	@Autowired
	PerfilRepository repositoryPerfil;

	@PostConstruct
	private void init() {
		Perfil perfil = null;
		if (repositoryPerfil.findAll().size() == 0) {
			perfil = new Perfil();
			perfil.setDescricao("ADMIN");
			repositoryPerfil.save(perfil);
		}
	}

}
