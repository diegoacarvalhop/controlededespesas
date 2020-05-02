package br.com.controlededespesas.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.controlededespesas.services.DespesaService;

@Controller
public class HomeController {

	@Autowired
	private DespesaService serviceDespesa;

	@GetMapping("/")
	public String home(ModelMap model) {
		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
			return "redirect:/login";
		}
		model.addAttribute("despesas", serviceDespesa.buscarTodos());
		return "home";
	}

	@GetMapping("/login")
	public String login(ModelMap model) {
		return "login";
	}

	@GetMapping({ "/login-error" })
	public String loginError(ModelMap model) {
		model.addAttribute("alerta", "erro");
		model.addAttribute("titulo", "Crendenciais inválidas!");
		model.addAttribute("texto", "Login ou senha incorretos, tente novamente.");
		model.addAttribute("subtexto", "Acesso permitido apenas para cadastros já ativados.");
		return "login";
	}

}
