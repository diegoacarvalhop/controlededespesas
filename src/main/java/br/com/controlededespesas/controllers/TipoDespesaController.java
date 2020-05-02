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

import br.com.controlededespesas.models.TipoDespesa;
import br.com.controlededespesas.models.Usuario;
import br.com.controlededespesas.services.TipoDespesaService;
import br.com.controlededespesas.services.UsuarioService;
import br.com.controlededespesas.utils.PaginacaoUtil;

@Controller
@RequestMapping("/tipodespesa")
public class TipoDespesaController {

	@Autowired
	private TipoDespesaService service;

	@Autowired
	private UsuarioService serviceUsuario;

	@GetMapping("/cadastrar")
	public String cadastrar(TipoDespesa tipoDespesa) {
		return "tipoDespesa/cadastro";
	}

	@GetMapping("/listar")
	public String listar(ModelMap model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("dir") Optional<String> dir) {
		int paginaAtual = page.orElse(1);
		String ordem = dir.orElse("asc");
		PaginacaoUtil<TipoDespesa> pageTipoDespesa = service.buscarPorPagina(paginaAtual, ordem);
		model.addAttribute("pageTipoDespesa", pageTipoDespesa);
		return "tipoDespesa/lista";
	}

	@PostMapping("/salvar")
	public String salvar(@Valid TipoDespesa tipoDespesa, BindingResult result, RedirectAttributes attr) {
		if (result.hasErrors()) {
			return "tipoDespesa/cadastro";
		}
		if (service.buscarPorDescricao(tipoDespesa.getDescricao()) > 0) {
			attr.addFlashAttribute("error", "Já existe um tipo de despesa com essa descrição.");
			return "redirect:/tipodespesa/cadastrar";
		}
		service.salvar(tipoDespesa);
		attr.addFlashAttribute("success", "Tipo de Despesa cadastrada.");
		return "redirect:/tipodespesa/cadastrar";
	}

	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model, RedirectAttributes attr) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		if (service.buscarPorId(id).getUsuario() != usuario) {
			attr.addFlashAttribute("error", "O Tipo de Despesa não pertence ao seu usuário.");
			return "redirect:/";
		}
		model.addAttribute("tipoDespesa", service.buscarPorId(id));
		return "tipoDespesa/cadastro";
	}

	@PostMapping("/editar")
	public String editar(@Valid TipoDespesa tipoDespesa, BindingResult result, RedirectAttributes attr) {
		if (result.hasErrors()) {
			return "/tipoDespesa/cadastro";
		}
		if (service.tipoDespesaTemDespesa(tipoDespesa.getId())) {
			attr.addFlashAttribute("error", "Tipo de Despesa não atualizada. Tem despesa(s) vinculada(s).");
			return "redirect:/tipodespesa/editar/" + tipoDespesa.getId();
		}
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		tipoDespesa.setUsuario(usuario);
		service.editar(tipoDespesa);
		attr.addFlashAttribute("success", "Tipo de Despesa atualizada.");
		return "redirect:/tipodespesa/cadastrar";
	}

	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		if (service.buscarPorId(id).getUsuario() != usuario) {
			attr.addFlashAttribute("error", "O Tipo de Despesa não pertence ao seu usuário.");
			return "redirect:/";
		}
		if (service.tipoDespesaTemDespesa(id)) {
			attr.addFlashAttribute("error", "Tipo de Despesa não excluida. Tem despesa(s) vinculada(s).");
		} else {
			service.excluir(id);
			attr.addFlashAttribute("success", "Tipo de Despesa excluida.");
		}
		return "redirect:/tipodespesa/listar";
	}

}
