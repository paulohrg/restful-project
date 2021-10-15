package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {
	
	/*tempo de validade do token em milesegundos*/
	private static final long EXPIRATION_TIME = 172800000;
	
	private static final String SECRET = "SenhaExtremamenteSecreta";
	
	private static final String TOKEN_PREFIX = "Bearer";
	
	private static final String HEADER_STRING = "Authorization";
	
	/*Gerar token de autenticação e adicionar ao cabeçalho e resposta http*/
	public void addAutenticacao(HttpServletResponse response, String usuario) throws IOException{
		
		/*montagem do token*/
		String JWT =  Jwts.builder() /*chama o gerador de token*/
						.setSubject(usuario) /*adiciona o usuario*/
						.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) /*define tempo de inspirção do token*/
						.signWith(SignatureAlgorithm.HS512, SECRET).compact(); /*compacta o algoritimo*/
		
		/*Junta token com prefixo*/
		String token = TOKEN_PREFIX + " " + JWT;

		/*Adiciona ao cabeçalho http*/
		response.addHeader(HEADER_STRING, token);
		
		/*Liberar resposta para portas diferentes que usam a api ou clientes web*/
		liberacaoCors(response);
		
		/*escreve token como resposta no corpo do http*/
		response.getWriter().write("{\""+HEADER_STRING+"\": \""+ token +"\"}");
	}
	
	/*retorna o usuario validado com token*/
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {
		
		/*Pega token enviado no cabeçalho http*/
		String token = request.getHeader(HEADER_STRING);
		
		try {
			if(token != null) {
				
				String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();
				
				/*Faz a validação do token*/
				String user = Jwts.parser().setSigningKey(SECRET)
								.parseClaimsJws(tokenLimpo)
								.getBody().getSubject(); /*pega o usuario informado no token*/
				
				if(user != null) {
					
					Usuario usuario = ApplicationContextLoad.getApplicationContext()
										.getBean(UsuarioRepository.class).buscarUsuarioPorLogin(user);
					
					if(usuario != null){
						return new UsernamePasswordAuthenticationToken(
								usuario.getLogin(), usuario.getSenha(), usuario.getRoles());
					}
				}
			}
		}catch (ExpiredJwtException e) {
			try {
				
				response.getOutputStream().println("Token expirado! Faça login ou informe o novo token para autenticação.");
			} catch (IOException e1) {}
		}
		
		liberacaoCors(response);
		
		return null; /*Não autorizado*/
	}

	private void liberacaoCors(HttpServletResponse response) {
		
		if(response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		if(response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		if(response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		if(response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
	}
}
