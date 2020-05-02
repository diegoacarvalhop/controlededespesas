package br.com.controlededespesas.repositories;

import java.util.List;

import br.com.controlededespesas.models.TipoPagamento;
import br.com.controlededespesas.models.Usuario;
import br.com.controlededespesas.utils.PaginacaoUtil;

public interface TipoPagamentoRepository {

	void save(TipoPagamento tipoPagamento);

	void update(TipoPagamento tipoPagamento);

	void delete(Long id);

	TipoPagamento findById(Long id);

	List<TipoPagamento> findAll(Usuario usuario);

	PaginacaoUtil<TipoPagamento> buscaPaginada(int pagina, String direcao);

	long findByDescricao(String descricao, Usuario usuario);

}
