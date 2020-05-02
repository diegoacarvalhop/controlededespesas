package br.com.controlededespesas.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@Entity
@Table(name = "CONTROLE_DESPESA")
public class ControleDespesa extends AbstractModel<Long> {

	@NotBlank
	private String status;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "id_despesa_fk")
	private Despesa despesa;

	@NotNull
	private Integer parcela;

	@NotNull
	@NumberFormat(style = Style.CURRENCY, pattern = "#,##0.00")
	@Column(columnDefinition = "DECIMAL(8,2) DEFAULT 0.00")
	private BigDecimal valor;

	@NotNull
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "data_parcela", columnDefinition = "DATE")
	private LocalDate dataParcela;

}
