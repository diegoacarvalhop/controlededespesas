package br.com.controlededespesas.services;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.controlededespesas.models.Perfil;
import br.com.controlededespesas.models.Usuario;
import br.com.controlededespesas.repositories.PerfilRepository;
import br.com.controlededespesas.repositories.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService {

	@Autowired
	private UsuarioRepository repository;

	@Autowired
	private PerfilRepository repositoryPerfil;

	@Transactional(readOnly = true)
	public Usuario buscarPorEmail(String email) {
		return repository.findByEmail(email);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = buscarPorEmail(username);
		return new User(usuario.getEmail(), usuario.getSenha(),
				AuthorityUtils.createAuthorityList(getAtuthorities(usuario.getPerfis())));
	}

	private String[] getAtuthorities(List<Perfil> perfis) {
		String[] authorities = new String[perfis.size()];
		for (int i = 0; i < perfis.size(); i++) {
			authorities[i] = perfis.get(i).getDescricao();
		}
		return authorities;
	}

	@Transactional(readOnly = false)
	public void salvarUsuario(Usuario usuario) {
		List<Perfil> perfis = new ArrayList<>();
		Perfil perfil = repositoryPerfil.findById(1L).get();
		perfis.add(perfil);
		usuario.setPerfis(perfis);
		usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
		repository.save(usuario);
	}

	public Usuario buscarPorId(Long id) {
		return repository.findById(id).get();
	}

	public void editar(@Valid Usuario usuario) {
		repository.save(usuario);
	}

}
