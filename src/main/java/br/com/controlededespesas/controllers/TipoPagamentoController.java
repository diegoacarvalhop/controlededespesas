package br.com.controlededespesas.controllers;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.controlededespesas.models.TipoPagamento;
import br.com.controlededespesas.models.Usuario;
import br.com.controlededespesas.services.TipoPagamentoService;
import br.com.controlededespesas.services.UsuarioService;
import br.com.controlededespesas.utils.PaginacaoUtil;

@Controller
@RequestMapping("/tipopagamento")
public class TipoPagamentoController {

	@Autowired
	private TipoPagamentoService service;

	@Autowired
	private UsuarioService serviceUsuario;

	@GetMapping("/cadastrar")
	public String cadastrar(TipoPagamento tipoPagamento) {
		return "tipoPagamento/cadastro";
	}

	@GetMapping("/listar")
	public String listar(ModelMap model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("dir") Optional<String> dir) {
		int paginaAtual = page.orElse(1);
		String ordem = dir.orElse("asc");
		PaginacaoUtil<TipoPagamento> pageTipoPagamento = service.buscarPorPagina(paginaAtual, ordem);
		model.addAttribute("pageTipoPagamento", pageTipoPagamento);
		return "tipoPagamento/lista";
	}

	@PostMapping("/salvar")
	public String salvar(@Valid TipoPagamento tipoPagamento, BindingResult result, RedirectAttributes attr) {
		if (result.hasErrors()) {
			return "tipoPagamento/cadastro";
		}
		if (!(tipoPagamento.getDescricao().toUpperCase().equals("DÉBITO")
				|| tipoPagamento.getDescricao().toUpperCase().equals("DEBITO")
				|| tipoPagamento.getDescricao().toUpperCase().equals("CRÉDITO")
				|| tipoPagamento.getDescricao().toUpperCase().equals("CREDITO")
				|| tipoPagamento.getDescricao().toUpperCase().equals("DINHEIRO"))) {
			attr.addFlashAttribute("error", "Tipos de pagamento aceitos: Débito, Crédito e Dinheiro.");
			return "redirect:/tipopagamento/cadastrar";
		}
		if (service.buscarPorDescricao(tipoPagamento.getDescricao()) > 0) {
			attr.addFlashAttribute("error", "Já existe um tipo de pagamento com essa descrição.");
			return "redirect:/tipopagamento/cadastrar";
		}
		service.salvar(tipoPagamento);
		attr.addFlashAttribute("success", "Tipo de Pagamento cadastrado.");
		return "redirect:/tipopagamento/cadastrar";
	}

	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model, RedirectAttributes attr) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		if (service.buscarPorId(id).getUsuario() != usuario) {
			attr.addFlashAttribute("error", "O Tipo de Despesa não pertence ao seu usuário.");
			return "redirect:/";
		}
		model.addAttribute("tipoPagamento", service.buscarPorId(id));
		return "tipoPagamento/cadastro";
	}

	@PostMapping("/editar")
	public String editar(@Valid TipoPagamento tipoPagamento, BindingResult result, RedirectAttributes attr) {
		if (result.hasErrors()) {
			return "/tipoPagamento/cadastro";
		}
		if (service.tipoPagamentoTemFormaPagamento(tipoPagamento.getId())) {
			attr.addFlashAttribute("error", "tipo de Pagamento não atualizado. Tem despesa(s) vinculada(s).");
			return "redirect:/tipopagamento/editar/" + tipoPagamento.getId();
		}
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		tipoPagamento.setUsuario(usuario);
		service.editar(tipoPagamento);
		attr.addFlashAttribute("success", "tipo de Pagamento atualizado.");
		return "redirect:/tipopagamento/cadastrar";
	}

	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		if (service.buscarPorId(id).getUsuario() != usuario) {
			attr.addFlashAttribute("error", "O Tipo de Despesa não pertence ao seu usuário.");
			return "redirect:/";
		}
		if (service.tipoPagamentoTemFormaPagamento(id)) {
			attr.addFlashAttribute("error", "tipo de Pagamento não excluido. Tem despesa(s) vinculada(s).");
		} else {
			service.excluir(id);
			attr.addFlashAttribute("success", "tipo de Pagamento excluido.");
		}
		return "redirect:/tipopagamento/listar";
	}

}
