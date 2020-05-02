package br.com.controlededespesas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.controlededespesas.models.ControleDespesa;
import br.com.controlededespesas.models.Despesa;
import br.com.controlededespesas.repositories.ControleDespesaRepository;
import br.com.controlededespesas.utils.PaginacaoUtil;

@Service
@Transactional(readOnly = false)
public class ControleDespesaServiceImpl implements ControleDespesaService {

	@Autowired
	private ControleDespesaRepository repository;

	@Autowired
	private DespesaService serviceDespesa;

	@Override
	public void salvar(ControleDespesa controleDespesa) {
		repository.save(controleDespesa);
	}

	@Override
	public void editar(ControleDespesa controleDespesa, boolean quitar) {
		if(quitar) {
			controleDespesa.setStatus("QUITADO");
			if (controleDespesa.getDespesa().getQuantidadeParcelas() == controleDespesa.getParcela()) {
				Despesa despesa = controleDespesa.getDespesa();
				despesa.setStatus("QUITADO");
				serviceDespesa.editar(despesa);
			}
		}
		repository.update(controleDespesa);
	}

	@Override
	public void excluir(Long id) {
		repository.delete(id);
	}

	@Override
	public ControleDespesa buscarPorId(Long id) {
		return repository.findById(id);
	}

	@Override
	public List<ControleDespesa> buscarTodos() {
		return repository.findAll();
	}

	@Override
	public PaginacaoUtil<ControleDespesa> buscarPorPagina(int pagina, String direcao) {
		return repository.buscaPaginada(pagina, direcao);
	}

	@Override
	public long buscarPorDespesa(Despesa despesa) {
		return repository.findByDespesa(despesa);
	}

	@Override
	public ControleDespesa getByDespesa(Despesa despesa) {
		return repository.getByDespesa(despesa);
	}

}
