package br.com.controlededespesas.services;

import java.util.List;

import br.com.controlededespesas.models.ControleDespesa;
import br.com.controlededespesas.models.Despesa;
import br.com.controlededespesas.utils.PaginacaoUtil;

public interface ControleDespesaService {

	void salvar(ControleDespesa controleDespesa);

	void editar(ControleDespesa controleDespesa, boolean quitar);

	void excluir(Long id);

	ControleDespesa buscarPorId(Long id);

	List<ControleDespesa> buscarTodos();

	public PaginacaoUtil<ControleDespesa> buscarPorPagina(int pagina, String direcao);

	long buscarPorDespesa(Despesa despesa);

	ControleDespesa getByDespesa(Despesa despesa);

}