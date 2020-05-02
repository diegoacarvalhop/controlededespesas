package br.com.controlededespesas.services;

import java.util.List;

import br.com.controlededespesas.models.FormaPagamento;
import br.com.controlededespesas.utils.PaginacaoUtil;

public interface FormaPagamentoService {

	void salvar(FormaPagamento formaPagamento);

	void editar(FormaPagamento formaPagamento);

	void excluir(Long id);

	FormaPagamento buscarPorId(Long id);

	List<FormaPagamento> buscarTodos();

	boolean formaPagamentoTemDespesa(Long id);

	public PaginacaoUtil<FormaPagamento> buscarPorPagina(int pagina, String direcao);

	long buscarPorDescricaoCartao(String descricaoCartao);

}