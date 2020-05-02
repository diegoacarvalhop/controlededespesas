package br.com.controlededespesas.repositories;

import java.util.List;

import br.com.controlededespesas.models.ControleDespesa;
import br.com.controlededespesas.models.Despesa;
import br.com.controlededespesas.utils.PaginacaoUtil;

public interface ControleDespesaRepository {

	void save(ControleDespesa controleDespesa);

	void update(ControleDespesa controleDespesa);

	void delete(Long id);

	ControleDespesa findById(Long id);

	List<ControleDespesa> findAll();

	PaginacaoUtil<ControleDespesa> buscaPaginada(int pagina, String direcao);

	long findByDespesa(Despesa despesa);

	ControleDespesa getByDespesa(Despesa despesa);

}
