package br.com.controlededespesas.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.controlededespesas.models.FormaPagamento;
import br.com.controlededespesas.models.TipoPagamento;
import br.com.controlededespesas.models.Usuario;
import br.com.controlededespesas.services.FormaPagamentoService;
import br.com.controlededespesas.services.TipoPagamentoService;
import br.com.controlededespesas.services.UsuarioService;
import br.com.controlededespesas.utils.PaginacaoUtil;

@Controller
@RequestMapping("/formapagamento")
public class FormaPagamentoController {

	@Autowired
	private FormaPagamentoService service;

	@Autowired
	private TipoPagamentoService serviceTipoPagamento;

	@Autowired
	private UsuarioService serviceUsuario;

	@GetMapping("/cadastrar")
	public String cadastrar(FormaPagamento cartao) {
		return "formaPagamento/cadastro";
	}

	@GetMapping("/listar")
	public String listar(ModelMap model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("dir") Optional<String> dir) {
		int paginaAtual = page.orElse(1);
		String ordem = dir.orElse("asc");
		PaginacaoUtil<FormaPagamento> pageFormaPagamento = service.buscarPorPagina(paginaAtual, ordem);
		model.addAttribute("pageFormaPagamento", pageFormaPagamento);
		return "formaPagamento/lista";
	}

	@PostMapping("/salvar")
	public String salvar(@Valid FormaPagamento formaPagamento, BindingResult result, RedirectAttributes attr) {
		if (result.hasErrors()) {
			return "formaPagamento/cadastro";
		}
		if ((formaPagamento.getTipoPagamento().getDescricao().equals("DINHEIRO")
				|| formaPagamento.getTipoPagamento().getDescricao().equals("DÉBITO")
				|| formaPagamento.getTipoPagamento().getDescricao().equals("DEBITO"))
				&& formaPagamento.getDiaMelhorCompra() == 0 || formaPagamento.getDiaVencimentoFatura() == 0) {
			attr.addFlashAttribute("error",
					"Para forma de pagamento em dinheiro ou débito, deve colocar 1 (um) para melhor dia de compra e dia de vencimento.");
			return "redirect:/formapagamento/cadastrar";
		}
		if (service.buscarPorDescricaoCartao(formaPagamento.getDescricaoCartao()) > 0) {
			attr.addFlashAttribute("error", "Já existe uma forma de pagamento com essa descrição.");
			return "redirect:/formapagamento/cadastrar";
		}
		service.salvar(formaPagamento);
		attr.addFlashAttribute("success", "Forma de pagamento cadastrada.");
		return "redirect:/formapagamento/cadastrar";
	}

	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model, RedirectAttributes attr) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		if (service.buscarPorId(id).getUsuario() != usuario) {
			attr.addFlashAttribute("error", "O Tipo de Despesa não pertence ao seu usuário.");
			return "redirect:/";
		}
		model.addAttribute("formaPagamento", service.buscarPorId(id));
		return "/formaPagamento/cadastro";
	}

	@PostMapping("/editar")
	public String editar(@Valid FormaPagamento formaPagamento, BindingResult result, RedirectAttributes attr) {
		if (result.hasErrors()) {
			return "/formaPagamento/cadastro";
		}
		if ((formaPagamento.getTipoPagamento().getDescricao().equals("DINHEIRO")
				|| formaPagamento.getTipoPagamento().getDescricao().equals("DÉBITO")
				|| formaPagamento.getTipoPagamento().getDescricao().equals("DEBITO"))
				&& formaPagamento.getDiaMelhorCompra() == 0 || formaPagamento.getDiaVencimentoFatura() == 0) {
			attr.addFlashAttribute("error",
					"Para forma de pagamento em dinheiro ou débito, deve colocar 1 (um) para melhor dia de compra e dia de vencimento.");
			return "redirect:/formapagamento/cadastrar";
		}
		if (service.formaPagamentoTemDespesa(formaPagamento.getId())) {
			attr.addFlashAttribute("error", "Forma de Pagamento não atualizada. Tem despesa(s) vinculada(s).");
			return "redirect:/formapagamento/editar/" + formaPagamento.getId();
		}
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		formaPagamento.setUsuario(usuario);
		service.editar(formaPagamento);
		attr.addFlashAttribute("success", "Forma de Pagamento atualizada.");
		return "redirect:/formapagamento/cadastrar";
	}

	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		if (service.buscarPorId(id).getUsuario() != usuario) {
			attr.addFlashAttribute("error", "O Tipo de Despesa não pertence ao seu usuário.");
			return "redirect:/";
		}
		if (service.formaPagamentoTemDespesa(id)) {
			attr.addFlashAttribute("error", "Forma de Pagamento não excluida. Tem despesa(s) vinculada(s).");
		} else {
			service.excluir(id);
			attr.addFlashAttribute("success", "Forma de Pagamento excluida.");
		}
		return "redirect:/formapagamento/listar";
	}

	@ModelAttribute("tiposPagamentos")
	public List<TipoPagamento> listaTipoPagamento() {
		return serviceTipoPagamento.buscarTodos();
	}

}
