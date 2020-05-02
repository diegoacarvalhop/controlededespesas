package br.com.controlededespesas.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import br.com.controlededespesas.models.TipoPagamento;
import br.com.controlededespesas.models.Usuario;
import br.com.controlededespesas.services.UsuarioService;
import br.com.controlededespesas.utils.PaginacaoUtil;

@Repository
public class TipoPagamentoRepositoryImpl extends AbstractRepository<TipoPagamento, Long>
		implements TipoPagamentoRepository {

	@Autowired
	private UsuarioService serviceUsuario;

	public PaginacaoUtil<TipoPagamento> buscaPaginada(int pagina, String direcao) {
		int tamanho = 10;
		int inicio = (pagina - 1) * tamanho; // 0*5=0 1*5=5 2*5=10
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		List<TipoPagamento> tipoPagametos = getEntityManager()
				.createQuery("select tp from TipoPagamento tp where tp.usuario = " + usuario.getId()
						+ " order by tp.descricao " + direcao, TipoPagamento.class)
				.setFirstResult(inicio).setMaxResults(tamanho).getResultList();

		long totalRegistros = count(usuario);
		long totalDePaginas = (totalRegistros + (tamanho - 1)) / tamanho;

		List<Integer> numerosPaginas = new ArrayList<>();

		for (int x = 0; x < totalDePaginas; x++) {
			numerosPaginas.add(x + 1);
		}

		return new PaginacaoUtil<>(tamanho, pagina, totalDePaginas, direcao, tipoPagametos, numerosPaginas);
	}

	public long count(Usuario usuario) {
		return getEntityManager()
				.createQuery("select count(*) from TipoPagamento where usuario = " + usuario.getId(), Long.class)
				.getSingleResult();
	}

	@Override
	public long findByDescricao(String descricao, Usuario usuario) {
		return getEntityManager().createQuery("select count(*) from TipoPagamento tp where tp.descricao = '" + descricao
				+ "' and tp.usuario = " + usuario.getId(), Long.class).getSingleResult();
	}

	@Override
	public List<TipoPagamento> findAll(Usuario usuario) {
		return getEntityManager().createQuery("select tp from TipoPagamento tp where tp.usuario = " + usuario.getId(),
				TipoPagamento.class).getResultList();
	}
}
