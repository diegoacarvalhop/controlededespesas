package br.com.controlededespesas.repositories;

import java.util.List;

import br.com.controlededespesas.models.TipoDespesa;
import br.com.controlededespesas.models.Usuario;
import br.com.controlededespesas.utils.PaginacaoUtil;

public interface TipoDespesaRepository {

	void save(TipoDespesa tipoDespesa);

	void update(TipoDespesa tipoDespesa);

	void delete(Long id);

	TipoDespesa findById(Long id);

	List<TipoDespesa> findAll(Usuario usuario);

	PaginacaoUtil<TipoDespesa> buscaPaginada(int pagina, String direcao);

	long findByDescricao(String descricao, Usuario usuario);

}
