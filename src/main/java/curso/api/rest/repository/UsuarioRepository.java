package curso.api.rest.repository;


import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import curso.api.rest.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	
	@Query("select u from Usuario u where u.login = ?1")
	Usuario buscarUsuarioPorLogin(String login);
	
	default Page<Usuario> buscarUsuarioPorNome(String nome, PageRequest pageRequest){
		Usuario usuario = new Usuario();
		usuario.setNome(nome);
		
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny().withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		
		Example<Usuario> example = Example.of(usuario, exampleMatcher);
		
		Page<Usuario> usuariosPage = findAll(example, pageRequest);
		
		return usuariosPage;
	};
}
