package br.com.controlededespesas.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.controlededespesas.models.Usuario;
import br.com.controlededespesas.services.UsuarioService;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioService service;

	@GetMapping("/cadastrar")
	public String cadastrar(Usuario usuario) {
		return "usuario/cadastro";
	}

	@GetMapping("/listar")
	public String listar(ModelMap model) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		model.addAttribute("usuario", service.buscarPorEmail(user.getUsername()));
		return "usuario/lista";
	}

	@PostMapping("/salvar")
	public String salvarUsuarios(Usuario usuario, BindingResult result, RedirectAttributes attr) {
		if (usuario.getEmail().isEmpty()) {
			result.addError(new ObjectError("email", "Campo obrigatório."));
		}
		if (usuario.getSenha().isEmpty()) {
			result.addError(new ObjectError("senha", "Campo obrigatório."));
		}
		if (result.hasErrors()) {
			return "usuario/cadastro";
		}
		if (service.buscarPorEmail(usuario.getEmail()) != null) {
			attr.addFlashAttribute("error", "E-mail já cadastrado.");
			return "redirect:/usuario/cadastrar";
		}
		service.salvarUsuario(usuario);
		attr.addFlashAttribute("success", "Usuário cadastrado.");
		return "redirect:/usuario/cadastrar";
	}

	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model, RedirectAttributes attr) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = service.buscarPorEmail(user.getUsername());
		if (usuario.getId() != id) {
			attr.addFlashAttribute("error", "Erro ao tentar editar o perfil.");
			return "redirect:/usuario/editar/" + usuario.getId();
		}
		model.addAttribute("usuario", usuario);
		return "usuario/cadastro";
	}

	@PostMapping("/editar")
	public String editar(Usuario usuario, RedirectAttributes attr) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuarioEditar = service.buscarPorEmail(user.getUsername());
		if (usuario.getEmail().isEmpty()) {
			attr.addFlashAttribute("error", "Campo de e-mail é obrigatório.");
			return "redirect:/usuario/editar/" + usuario.getId();
		}
		if (usuario.getSenha().isEmpty()) {
			usuario.setSenha(usuarioEditar.getSenha());
		} else {
			usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
		}
		if (usuarioEditar.getId() != usuario.getId()) {
			attr.addFlashAttribute("error", "Erro ao tentar editar o perfil.");
			return "redirect:/usuario/editar/" + usuario.getId();
		}
		if (service.buscarPorEmail(usuario.getEmail()) != null) {
			attr.addFlashAttribute("error", "E-mail já cadastrado.");
			return "redirect:/usuario/editar/" + usuario.getId();
		}
		usuario.setPerfis(usuarioEditar.getPerfis());
		service.editar(usuario);
		return "redirect:/login";
	}

}
