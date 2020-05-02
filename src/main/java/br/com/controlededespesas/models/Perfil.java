package br.com.controlededespesas.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
@Entity
@Table(name = "PERFIL")
public class Perfil extends AbstractModel<Long> {

	@NotEmpty
	@Column(unique = true)
	private String descricao;

	public Perfil() {
		super();
	}

	public Perfil(Long id) {
		super.setId(id);
	}

}
