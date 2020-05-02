package br.com.controlededespesas.services;

import java.util.List;

import br.com.controlededespesas.models.Despesa;
import br.com.controlededespesas.utils.PaginacaoUtil;

public interface DespesaService {

	void salvar(Despesa despesa);

	void editar(Despesa despesa);

	void excluir(Long id);

	Despesa buscarPorId(Long id);

	public PaginacaoUtil<Despesa> buscarPorPagina(int pagina, String direcao);

	List<Despesa> buscarTodos();

	long buscarPorDescricao(String descricao);

	boolean temParcelaQuitada(Long id);

}
