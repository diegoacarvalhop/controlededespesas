package br.com.controlededespesas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.controlededespesas.models.TipoDespesa;
import br.com.controlededespesas.models.Usuario;
import br.com.controlededespesas.repositories.TipoDespesaRepository;
import br.com.controlededespesas.utils.PaginacaoUtil;

@Service
@Transactional(readOnly = false)
public class TipoDespesaServiceImpl implements TipoDespesaService {

	@Autowired
	private TipoDespesaRepository repository;

	@Autowired
	private UsuarioService serviceUsuario;

	@Override
	public void salvar(TipoDespesa tipoDespesa) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		tipoDespesa.setUsuario(usuario);
		tipoDespesa.setDescricao(tipoDespesa.getDescricao().toUpperCase());
		repository.save(tipoDespesa);
	}

	@Override
	public void editar(TipoDespesa tipoDespesa) {
		tipoDespesa.setDescricao(tipoDespesa.getDescricao().toUpperCase());
		repository.update(tipoDespesa);
	}

	@Override
	public void excluir(Long id) {
		repository.delete(id);
	}

	@Override
	public TipoDespesa buscarPorId(Long id) {
		return repository.findById(id);
	}

	@Override
	public List<TipoDespesa> buscarTodos() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		return repository.findAll(usuario);
	}

	@Override
	public boolean tipoDespesaTemDespesa(Long id) {
		if (buscarPorId(id).getDespesas().isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public PaginacaoUtil<TipoDespesa> buscarPorPagina(int pagina, String direcao) {
		return repository.buscaPaginada(pagina, direcao);
	}

	@Override
	public long buscarPorDescricao(String descricao) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		return repository.findByDescricao(descricao, usuario);
	}

}
