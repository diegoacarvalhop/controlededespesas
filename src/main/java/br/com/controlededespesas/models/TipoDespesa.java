package br.com.controlededespesas.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
@Entity
@Table(name = "TIPO_DESPESA")
public class TipoDespesa extends AbstractModel<Long> {

	@NotBlank
	@Size(min = 3, max = 60)
	private String descricao;

	@ManyToOne
	@JoinColumn(name = "id_usuario_fk")
	private Usuario usuario;

	@OneToMany(mappedBy = "tipoDespesa")
	private List<Despesa> despesas;

}
