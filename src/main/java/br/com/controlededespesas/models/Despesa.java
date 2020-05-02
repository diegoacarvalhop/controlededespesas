package br.com.controlededespesas.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
@Entity
@Table(name = "DESPESA")
public class Despesa extends AbstractModel<Long> {

	@NotNull
	@ManyToOne
	@JoinColumn(name = "id_tipo_despesa_fk")
	private TipoDespesa tipoDespesa;

	@NotBlank
	@Size(min = 3, max = 60)
	private String descricao;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "id_forma_pagamento_fk")
	private FormaPagamento formaPagamento;

	@NotNull
	@NumberFormat(style = Style.CURRENCY, pattern = "#,##0.00")
	@Column(columnDefinition = "DECIMAL(8,2) DEFAULT 0.00")
	private BigDecimal valor;

	@NotNull
	@Column(name = "quantidade_parcelas")
	private Integer quantidadeParcelas;

	@NotNull
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "data_entrada", columnDefinition = "DATE")
	private LocalDate dataDespesa;

	private String status;

	@ManyToOne
	@JoinColumn(name = "id_usuario_fk")
	private Usuario usuario;

	@OneToMany(mappedBy = "despesa", cascade = CascadeType.REMOVE)
	private List<ControleDespesa> controleDespesas;
	
}
