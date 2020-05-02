package br.com.controlededespesas.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import br.com.controlededespesas.models.TipoDespesa;
import br.com.controlededespesas.models.Usuario;
import br.com.controlededespesas.services.UsuarioService;
import br.com.controlededespesas.utils.PaginacaoUtil;

@Repository
public class TipoDespesaRepositoryImpl extends AbstractRepository<TipoDespesa, Long> implements TipoDespesaRepository {

	@Autowired
	private UsuarioService serviceUsuario;

	public PaginacaoUtil<TipoDespesa> buscaPaginada(int pagina, String direcao) {
		int tamanho = 10;
		int inicio = (pagina - 1) * tamanho; // 0*5=0 1*5=5 2*5=10
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		List<TipoDespesa> tipoDespesas = getEntityManager()
				.createQuery("select td from TipoDespesa td where td.usuario = " + usuario.getId()
						+ " order by td.descricao " + direcao, TipoDespesa.class)
				.setFirstResult(inicio).setMaxResults(tamanho).getResultList();

		long totalRegistros = count(usuario);
		long totalDePaginas = (totalRegistros + (tamanho - 1)) / tamanho;

		List<Integer> numerosPaginas = new ArrayList<>();

		for (int x = 0; x < totalDePaginas; x++) {
			numerosPaginas.add(x + 1);
		}

		return new PaginacaoUtil<>(tamanho, pagina, totalDePaginas, direcao, tipoDespesas, numerosPaginas);
	}

	public long count(Usuario usuario) {
		return getEntityManager()
				.createQuery("select count(*) from TipoDespesa where usuario = " + usuario.getId(), Long.class)
				.getSingleResult();
	}

	@Override
	public long findByDescricao(String descricao, Usuario usuario) {
		return getEntityManager().createQuery("select count(*) from TipoDespesa td where td.descricao = '" + descricao
				+ "' and usuario = " + usuario.getId(), Long.class).getSingleResult();
	}

	@Override
	public List<TipoDespesa> findAll(Usuario usuario) {
		return getEntityManager()
				.createQuery("select td from TipoDespesa td where td.usuario = " + usuario.getId(), TipoDespesa.class)
				.getResultList();
	}
}
