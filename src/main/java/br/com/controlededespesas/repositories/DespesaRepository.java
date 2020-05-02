package br.com.controlededespesas.repositories;

import java.util.List;

import br.com.controlededespesas.models.Despesa;
import br.com.controlededespesas.models.Usuario;
import br.com.controlededespesas.utils.PaginacaoUtil;

public interface DespesaRepository {

	void save(Despesa despesa);

	void update(Despesa despesa);

	void delete(Long id);

	Despesa findById(Long id);

	List<Despesa> findAll(Usuario usuario);

	PaginacaoUtil<Despesa> buscaPaginada(int pagina, String direcao);

	long findByDescricao(String descricao, Usuario usuario);

}
