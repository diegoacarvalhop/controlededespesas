package br.com.controlededespesas.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
@Entity
@Table(name = "FORMA_PAGAMENTO")
public class FormaPagamento extends AbstractModel<Long> {

	@NotBlank
	@Size(min = 3, max = 30)
	@Column(name = "descricao_cartao")
	private String descricaoCartao;

	@NotBlank
	@Size(min = 4, max = 4)
	@Column(name = "ultimos_digitos")
	private String ultimosDigitos;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "id_tipo_pagamento_fk")
	private TipoPagamento tipoPagamento;

	@NotNull
	@Column(name = "dia_vencimento_fatura")
	private Integer diaVencimentoFatura;

	@NotNull
	@Column(name = "dia_melhor_compra")
	private Integer diaMelhorCompra;

	@ManyToOne
	@JoinColumn(name = "id_usuario_fk")
	private Usuario usuario;

	@OneToMany(mappedBy = "formaPagamento")
	private List<Despesa> despesas;

}