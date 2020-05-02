package br.com.controlededespesas.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.controlededespesas.models.ControleDespesa;
import br.com.controlededespesas.models.Despesa;
import br.com.controlededespesas.utils.PaginacaoUtil;

@Repository
public class ControleDespesaRepositoryImpl extends AbstractRepository<ControleDespesa, Long>
		implements ControleDespesaRepository {

	public PaginacaoUtil<ControleDespesa> buscaPaginada(int pagina, String direcao) {
		int tamanho = 12;
		int inicio = (pagina - 1) * tamanho; // 0*5=0 1*5=5 2*5=10
		List<ControleDespesa> controleDespesas = getEntityManager()
				.createQuery("select cd from ControleDespesa cd order by cd.id " + direcao, ControleDespesa.class)
				.setFirstResult(inicio).setMaxResults(tamanho).getResultList();

		long totalRegistros = count();
		long totalDePaginas = (totalRegistros + (tamanho - 1)) / tamanho;

		List<Integer> numerosPaginas = new ArrayList<>();

		for (int x = 0; x < totalDePaginas; x++) {
			numerosPaginas.add(x + 1);
		}

		return new PaginacaoUtil<>(tamanho, pagina, totalDePaginas, direcao, controleDespesas, numerosPaginas);
	}

	public long count() {
		return getEntityManager().createQuery("select count(*) from ControleDespesa", Long.class).getSingleResult();
	}

	@Override
	public long findByDespesa(Despesa despesa) {
		return getEntityManager()
				.createQuery("select count(*) from ControleDespesa cd where cd.despesa = " + despesa.getId(),
						Long.class)
				.getSingleResult();
	}

	@Override
	public ControleDespesa getByDespesa(Despesa despesa) {
		return getEntityManager().createQuery("select cd from ControleDespesa cd where cd.despesa = " + despesa.getId(),
				ControleDespesa.class).getSingleResult();
	}

}
