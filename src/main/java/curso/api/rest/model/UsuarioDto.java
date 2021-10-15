package curso.api.rest.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String login;
	private String nome;
	private String email;
	private String cpf;
	
	
	public UsuarioDto(Usuario usuario) {
		
		this.id = usuario.getId();
		this.login = usuario.getLogin();
		this.nome = usuario.getNome();
		this.email = usuario.getEmail();
		this.cpf = usuario.getCpf();
	}

}
