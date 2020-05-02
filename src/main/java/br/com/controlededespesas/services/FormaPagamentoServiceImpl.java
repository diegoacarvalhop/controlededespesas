package br.com.controlededespesas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.controlededespesas.models.FormaPagamento;
import br.com.controlededespesas.models.Usuario;
import br.com.controlededespesas.repositories.FormaPagamentoRepository;
import br.com.controlededespesas.utils.PaginacaoUtil;

@Service
@Transactional(readOnly = false)
public class FormaPagamentoServiceImpl implements FormaPagamentoService {

	@Autowired
	private FormaPagamentoRepository repository;

	@Autowired
	private UsuarioService serviceUsuario;

	@Override
	public void salvar(FormaPagamento formaPagamento) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		formaPagamento.setUsuario(usuario);
		formaPagamento.setDescricaoCartao(formaPagamento.getDescricaoCartao().toUpperCase());
		repository.save(formaPagamento);
	}

	@Override
	public void editar(FormaPagamento formaPagamento) {
		formaPagamento.setDescricaoCartao(formaPagamento.getDescricaoCartao().toUpperCase());
		repository.update(formaPagamento);
	}

	@Override
	public void excluir(Long id) {
		repository.delete(id);
	}

	@Override
	public FormaPagamento buscarPorId(Long id) {
		return repository.findById(id);
	}

	@Override
	public List<FormaPagamento> buscarTodos() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		return repository.findAll(usuario);
	}

	@Override
	public boolean formaPagamentoTemDespesa(Long id) {
		if (buscarPorId(id).getDespesas().isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public PaginacaoUtil<FormaPagamento> buscarPorPagina(int pagina, String direcao) {
		return repository.buscaPaginada(pagina, direcao);
	}

	@Override
	public long buscarPorDescricaoCartao(String descricaoCartao) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		return repository.findByDescricaoCartao(descricaoCartao, usuario);
	}

}
