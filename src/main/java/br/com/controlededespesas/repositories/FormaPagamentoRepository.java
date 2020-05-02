package br.com.controlededespesas.repositories;

import java.util.List;

import br.com.controlededespesas.models.FormaPagamento;
import br.com.controlededespesas.models.Usuario;
import br.com.controlededespesas.utils.PaginacaoUtil;

public interface FormaPagamentoRepository {

	void save(FormaPagamento formaPagamento);

	void update(FormaPagamento formaPagamento);

	void delete(Long id);

	FormaPagamento findById(Long id);

	List<FormaPagamento> findAll(Usuario usuario);

	PaginacaoUtil<FormaPagamento> buscaPaginada(int pagina, String direcao);

	long findByDescricaoCartao(String descricao, Usuario usuario);

}
