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

import br.com.controlededespesas.models.ControleDespesa;
import br.com.controlededespesas.models.Despesa;
import br.com.controlededespesas.models.FormaPagamento;
import br.com.controlededespesas.models.TipoDespesa;
import br.com.controlededespesas.models.Usuario;
import br.com.controlededespesas.services.ControleDespesaService;
import br.com.controlededespesas.services.DespesaService;
import br.com.controlededespesas.services.FormaPagamentoService;
import br.com.controlededespesas.services.TipoDespesaService;
import br.com.controlededespesas.services.UsuarioService;
import br.com.controlededespesas.utils.PaginacaoUtil;

@Controller
@RequestMapping("/despesa")
public class DespesaController {

	@Autowired
	private DespesaService service;

	@Autowired
	private FormaPagamentoService serviceFormaPagamento;

	@Autowired
	private TipoDespesaService serviceTipoDespesa;

	@Autowired
	private ControleDespesaService serviceControleDespesa;

	@Autowired
	private UsuarioService serviceUsuario;

	@GetMapping("/cadastrar")
	public String cadastrar(Despesa despesa) {
		return "despesa/cadastro";
	}

	@GetMapping("/listar")
	public String listar(ModelMap model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("dir") Optional<String> dir) {
		int paginaAtual = page.orElse(1);
		String ordem = dir.orElse("asc");
		PaginacaoUtil<Despesa> pageDespesa = service.buscarPorPagina(paginaAtual, ordem);
		model.addAttribute("pageDespesa", pageDespesa);
		return "despesa/lista";
	}

	@PostMapping("/salvar")
	public String salvar(@Valid Despesa despesa, BindingResult result, RedirectAttributes attr) {
		if (result.hasErrors()) {
			return "despesa/cadastro";
		}
		if (service.buscarPorDescricao(despesa.getDescricao()) > 0) {
			attr.addFlashAttribute("error", "Já existe uma despesa com essa descrição.");
			return "redirect:/despesa/cadastrar";
		}
		if ((despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("DINHEIRO")
				|| despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("DÉBITO")
				|| despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("DEBITO"))
				&& despesa.getQuantidadeParcelas() > 0) {
			attr.addFlashAttribute("error",
					"Para forma de pagamento em dinheiro ou débito, deve colocar 0 (zero) para quantidade de parcelas.");
			return "redirect:/despesa/cadastrar";
		} else if ((despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("CRÉDITO")
				|| despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("CREDITO"))
				&& despesa.getQuantidadeParcelas() == 0) {
			attr.addFlashAttribute("error",
					"Para forma de pagamento em crédito, deve colocar ao menos 1 (um) para quantidade de parcelas.");
			return "redirect:/despesa/cadastrar";
		}
		service.salvar(despesa);
		attr.addFlashAttribute("success", "Despesa cadastrada.");
		return "redirect:/despesa/cadastrar";
	}

	@ModelAttribute("formasPagamentos")
	public List<FormaPagamento> listaFormaPagamento(ModelMap model) {
		return serviceFormaPagamento.buscarTodos();
	}

	@ModelAttribute("tiposDespesas")
	public List<TipoDespesa> listaTipoDespesa(ModelMap model) {
		return serviceTipoDespesa.buscarTodos();
	}

	@GetMapping("editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model, RedirectAttributes attr) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		if (service.buscarPorId(id).getUsuario() != usuario) {
			attr.addFlashAttribute("error", "O Tipo de Despesa não pertence ao seu usuário.");
			return "redirect:/";
		}
		model.addAttribute("despesa", service.buscarPorId(id));
		model.addAttribute("parcelaQuidata", service.temParcelaQuitada(id));
		return "despesa/cadastro";
	}

	@PostMapping("/editar")
	public String editar(@Valid Despesa despesa, BindingResult result, RedirectAttributes attr) {
		if (result.hasErrors()) {
			return "despesa/cadastro";
		}
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		despesa.setUsuario(usuario);
		if ((despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("DINHEIRO")
				|| despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("DÉBITO")
				|| despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("DEBITO"))
				&& despesa.getQuantidadeParcelas() > 0) {
			attr.addFlashAttribute("error",
					"Para forma de pagamento em dinheiro ou débito, deve colocar 0 (zero) para quantidade de parcelas.");
			return "redirect:/despesa/editar/" + despesa.getId();
		} else if ((despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("CRÉDITO")
				|| despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("CREDITO"))
				&& despesa.getQuantidadeParcelas() == 0) {
			attr.addFlashAttribute("error",
					"Para forma de pagamento em crédito, deve colocar ao menos 1 (um) para quantidade de parcelas.");
			return "redirect:/despesa/editar/" + despesa.getId();
		}
		service.editar(despesa);
		attr.addFlashAttribute("success", "Despesa editada.");
		return "redirect:/despesa/editar/" + despesa.getId();
	}

	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		if (service.buscarPorId(id).getUsuario() != usuario) {
			attr.addFlashAttribute("error", "O Tipo de Despesa não pertence ao seu usuário.");
			return "redirect:/";
		}
		service.excluir(id);
		attr.addFlashAttribute("success", "Despesa exluída.");
		return "redirect:/despesa/listar";
	}

	@GetMapping("quitarcontroledespesa/{id}/{quitar}")
	public String quitarControleDespesa(@PathVariable("id") Long id, @PathVariable("quitar") boolean quitar,
			RedirectAttributes attributes) {
		ControleDespesa controleDespesa = serviceControleDespesa.buscarPorId(id);
		serviceControleDespesa.editar(controleDespesa, quitar);
		attributes.addFlashAttribute("success", "Parcela " + controleDespesa.getParcela() + " quitada.");
		return "redirect:/despesa/editar/" + controleDespesa.getDespesa().getId();
	}

}
