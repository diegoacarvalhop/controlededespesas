package br.com.controlededespesas.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import br.com.controlededespesas.models.FormaPagamento;
import br.com.controlededespesas.models.Usuario;
import br.com.controlededespesas.services.UsuarioService;
import br.com.controlededespesas.utils.PaginacaoUtil;

@Repository
public class FormaPagamentoRepositoryImpl extends AbstractRepository<FormaPagamento, Long>
		implements FormaPagamentoRepository {

	@Autowired
	private UsuarioService serviceUsuario;

	public PaginacaoUtil<FormaPagamento> buscaPaginada(int pagina, String direcao) {
		int tamanho = 10;
		int inicio = (pagina - 1) * tamanho; // 0*5=0 1*5=5 2*5=10
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		List<FormaPagamento> formaPagamentos = getEntityManager()
				.createQuery("select fp from FormaPagamento fp where fp.usuario = " + usuario.getId()
						+ " order by fp.descricaoCartao " + direcao, FormaPagamento.class)
				.setFirstResult(inicio).setMaxResults(tamanho).getResultList();

		long totalRegistros = count();
		long totalDePaginas = (totalRegistros + (tamanho - 1)) / tamanho;

		List<Integer> numerosPaginas = new ArrayList<>();

		for (int x = 0; x < totalDePaginas; x++) {
			numerosPaginas.add(x + 1);
		}

		return new PaginacaoUtil<>(tamanho, pagina, totalDePaginas, direcao, formaPagamentos, numerosPaginas);
	}

	public long count() {
		return getEntityManager().createQuery("select count(*) from FormaPagamento", Long.class).getSingleResult();
	}

	@Override
	public long findByDescricaoCartao(String descricaoCartao, Usuario usuario) {
		return getEntityManager().createQuery("select count(*) from FormaPagamento fp where fp.descricaoCartao = '"
				+ descricaoCartao + "' and fp.usuario = " + usuario.getId(), Long.class).getSingleResult();
	}

	@Override
	public List<FormaPagamento> findAll(Usuario usuario) {
		return getEntityManager().createQuery("select fp from FormaPagamento fp where fp.usuario = " + usuario.getId(),
				FormaPagamento.class).getResultList();
	}

}
