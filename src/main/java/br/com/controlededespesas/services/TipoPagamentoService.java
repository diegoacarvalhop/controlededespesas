package br.com.controlededespesas.services;

import java.util.List;

import br.com.controlededespesas.models.TipoPagamento;
import br.com.controlededespesas.utils.PaginacaoUtil;

public interface TipoPagamentoService {

	void salvar(TipoPagamento tipoPagamento);

	void editar(TipoPagamento tipoPagamento);

	void excluir(Long id);

	TipoPagamento buscarPorId(Long id);

	List<TipoPagamento> buscarTodos();

	boolean tipoPagamentoTemFormaPagamento(Long id);

	public PaginacaoUtil<TipoPagamento> buscarPorPagina(int pagina, String direcao);

	long buscarPorDescricao(String descricao);

}