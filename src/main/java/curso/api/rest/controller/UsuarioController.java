package curso.api.rest.controller;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.model.Anuncio;
import curso.api.rest.model.Usuario;
import curso.api.rest.model.UsuarioDto;
import curso.api.rest.repository.AnuncioRepository;
import curso.api.rest.repository.UsuarioRepository;
import curso.api.rest.service.UserDetailsServiceImpl;

@RestController
@RequestMapping(value = "/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@Autowired
	private AnuncioRepository anuncioRepository;

	@GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	@CacheEvict(value = "cacheUsuarios", allEntries = true)
	@CachePut("cacheUsuarios")
	public ResponseEntity<Page<Usuario>> buscarTodos() throws Exception {
		
		PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));

		Page<Usuario> lista = usuarioRepository.findAll(page);
		
		return new ResponseEntity<Page<Usuario>>(lista, HttpStatus.OK);
	}
	
	@GetMapping(value = "/page/{pagina}", produces = MediaType.APPLICATION_JSON_VALUE)
	@CachePut("cacheUsuarios")
	public ResponseEntity<Page<Usuario>> buscarTodosPorPagina(@PathVariable("pagina") int pagina) throws Exception {
		
		PageRequest page = PageRequest.of(pagina, 5, Sort.by("nome"));

		Page<Usuario> lista = usuarioRepository.findAll(page);
		
		return new ResponseEntity<Page<Usuario>>(lista, HttpStatus.OK);
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Usuario> buscaUsuarioPorId(@PathVariable(value = "id") Long id) {

		Usuario usuarioSalvo = usuarioRepository.findById(id).get();
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	@GetMapping(value = "/buscaUsuarioPorNome/{nome}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<Usuario>> buscarUsuariosPorNome(@PathVariable("nome") String nome) throws Exception {

		PageRequest page = PageRequest.of(0, 5, Sort.by("Nome")); 
		
		Page<Usuario> lista = usuarioRepository.buscarUsuarioPorNome(nome.toUpperCase().trim(), page);

		return new ResponseEntity<Page<Usuario>>(lista, HttpStatus.OK);
	}
	@GetMapping(value = "/buscaUsuarioPorNome/{nome}/page/{pagina}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<Usuario>> buscarUsuariosPorNomePorPagina(@PathVariable("nome") String nome, @PathVariable("pagina") int pagina) throws Exception {

		PageRequest page = PageRequest.of(pagina, 5, Sort.by("Nome")); 
		
		Page<Usuario> lista = usuarioRepository.buscarUsuarioPorNome(nome.toUpperCase().trim(), page);

		return new ResponseEntity<Page<Usuario>>(lista, HttpStatus.OK);
	}
	
	@GetMapping(value = "/buscaUsuarioPorLogin/{login}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Usuario> buscarUsuarioPorLogin(@PathVariable("login") String login) throws Exception {

		Usuario usuario = usuarioRepository.buscarUsuarioPorLogin(login);

		return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
	}

	@PostMapping(value = "/", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> salvar(@RequestBody Usuario usuario) {

		Usuario usuarioExistente = usuarioRepository.buscarUsuarioPorLogin(usuario.getLogin());

		if (usuarioExistente != null) {
			return new ResponseEntity<String>("Login Já cadastrado!", HttpStatus.OK);
		}

		/* criptografar senha */
		usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));

		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		userDetailsServiceImpl.inserePermissaoDeUsuario(usuarioSalvo.getId(), usuario.getRoles());

		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	@PutMapping(value = "/", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {

		Usuario usuarioAtual = usuarioRepository.findById(usuario.getId()).get();

		BeanUtils.copyProperties(usuarioAtual, usuario, getNullPropertyNames(usuarioAtual));

		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}", produces = "application/text")
	@ResponseBody
	public String deletar(@PathVariable(name = "id") Long id) {

		List<Anuncio> anuncios = anuncioRepository.buscaAnunciosPorUsuario(id);

		if (anuncios != null && anuncios.size() > 0) {
			return "Existem anuncios vinculados ao usuário, Exclusão não permitida.";
		}

		usuarioRepository.deleteById(id);

		return "Usuário excluido com sucesso!";
	}

	public static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<String>();
		for (java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null)
				emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}

}
