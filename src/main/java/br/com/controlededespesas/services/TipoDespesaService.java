package br.com.controlededespesas.services;

import java.util.List;

import br.com.controlededespesas.models.TipoDespesa;
import br.com.controlededespesas.utils.PaginacaoUtil;

public interface TipoDespesaService {

	void salvar(TipoDespesa tipoDespesa);

	void editar(TipoDespesa tipoDespesa);

	void excluir(Long id);

	TipoDespesa buscarPorId(Long id);

	List<TipoDespesa> buscarTodos();

	boolean tipoDespesaTemDespesa(Long id);

	public PaginacaoUtil<TipoDespesa> buscarPorPagina(int pagina, String direcao);

	long buscarPorDescricao(String descricao);

}