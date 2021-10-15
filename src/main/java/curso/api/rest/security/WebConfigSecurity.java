package curso.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import curso.api.rest.service.UserDetailsServiceImpl;

/*mapeia urls, endereços, autoriza ou bloqueia acesso a url*/

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;
	
	/*configura as solicitações de acesso por http*/
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/*ativando a proteção contra usuarios que não estão autenticados por token*/
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		
		/*ativando a permissao para acesso a pagina inicial do sistema*/
		.disable().authorizeRequests().antMatchers("/").permitAll()
		.antMatchers("/index.html").permitAll()
		
		/*Permitindo acesso externo aos methodos da api*/
		.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
		
		/*url de logout - redireciona após o usuário deslogar do sistema*/
		.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index.html")
		
		/*mapeia url de logout e invalida usuario após logout*/
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		
		/*Filtra as requisições de login para autenticação*/
		.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), 
									UsernamePasswordAuthenticationFilter.class)
		
		/*filtra demais requisições para verificar presença do token JWT no HEADER HTTP*/
		.addFilterBefore(new JWTApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		/*Consultar o usuario no banco de dados*/
		auth.userDetailsService(userDetailsServiceImpl)
	
		/*padrão de codificação de senha*/
		.passwordEncoder(new BCryptPasswordEncoder());
		
	}
}
