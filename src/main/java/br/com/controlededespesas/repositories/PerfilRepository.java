package br.com.controlededespesas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.controlededespesas.models.Perfil;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {

}