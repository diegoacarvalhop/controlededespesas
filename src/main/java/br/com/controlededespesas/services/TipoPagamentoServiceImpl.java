package br.com.controlededespesas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.controlededespesas.models.TipoPagamento;
import br.com.controlededespesas.models.Usuario;
import br.com.controlededespesas.repositories.TipoPagamentoRepository;
import br.com.controlededespesas.utils.PaginacaoUtil;

@Service
@Transactional(readOnly = false)
public class TipoPagamentoServiceImpl implements TipoPagamentoService {

	@Autowired
	private TipoPagamentoRepository repository;

	@Autowired
	private UsuarioService serviceUsuario;

	@Override
	public void salvar(TipoPagamento tipoPagamento) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		tipoPagamento.setUsuario(usuario);
		tipoPagamento.setDescricao(tipoPagamento.getDescricao().toUpperCase());
		repository.save(tipoPagamento);
	}

	@Override
	public void editar(TipoPagamento tipoPagamento) {
		tipoPagamento.setDescricao(tipoPagamento.getDescricao().toUpperCase());
		repository.update(tipoPagamento);
	}

	@Override
	public void excluir(Long id) {
		repository.delete(id);
	}

	@Override
	public TipoPagamento buscarPorId(Long id) {
		return repository.findById(id);
	}

	@Override
	public List<TipoPagamento> buscarTodos() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		return repository.findAll(usuario);
	}

	@Override
	public boolean tipoPagamentoTemFormaPagamento(Long id) {
		if (buscarPorId(id).getFormaPagamentos().isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public PaginacaoUtil<TipoPagamento> buscarPorPagina(int pagina, String direcao) {
		return repository.buscaPaginada(pagina, direcao);
	}

	@Override
	public long buscarPorDescricao(String descricao) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		return repository.findByDescricao(descricao, usuario);
	}

}
