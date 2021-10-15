package curso.api.rest.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import curso.api.rest.model.Role;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.RoleRepository;
import curso.api.rest.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario usuario = usuarioRepository.buscarUsuarioPorLogin(username);
		
		if(usuario == null) {
			throw new UsernameNotFoundException("Usuario não encontrado!");
		}
		
		return new User(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());
	}

	public void inserePermissaoDeUsuario(Long idUsuario, List<Role> roles) {
		List<Role> permissoes = new ArrayList<Role>();
		
		if (roles == null ||  roles.size() == 0) {
			Role role = roleRepository.buscaRolePadrao();
			permissoes.add(role);
		}else {
			permissoes = roles;
		}
		
		for (Role role : permissoes) {
			roleRepository.salvarPermissao(idUsuario, role.getId());
		}
	}

}
