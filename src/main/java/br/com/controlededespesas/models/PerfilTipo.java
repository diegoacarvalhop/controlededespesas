package br.com.controlededespesas.models;

import lombok.Getter;

@Getter
public enum PerfilTipo {

	ADMIN(1, "ADMIN");

	private long codigo;
	private String descricao;

	private PerfilTipo(long codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

}
