package curso.api.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.model.Anuncio;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.AnuncioRepository;

@RestController
@RequestMapping(value="/anuncios")
public class AnuncioController {
	@Autowired
	private AnuncioRepository anuncioRepository;
	
	@GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Anuncio>> listar(){
		
		List<Anuncio> listaDeAnuncios = new ArrayList<>(); 
		
		for (Anuncio anuncio : anuncioRepository.findAll()) {
			listaDeAnuncios.add(anuncio);
		};
		
		return new ResponseEntity<List<Anuncio>>(listaDeAnuncios, HttpStatus.OK);
	}
	
	@GetMapping(value = "/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Anuncio>> listarPorUsuario(@PathVariable(name = "idUsuario") Long idUsuario ){
		
		List<Anuncio> listaDeAnuncios = anuncioRepository.buscaAnunciosPorUsuario(idUsuario);
		
		return new ResponseEntity<List<Anuncio>>(listaDeAnuncios, HttpStatus.CREATED);
	}
	
	@PostMapping(value = "/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Anuncio> salvar(@PathVariable Long idUsuario,
										  @RequestBody Anuncio anuncio){
		
		Usuario usuario = new Usuario();
		usuario.setId(idUsuario);
		anuncio.setUsuario(usuario);
		
		Anuncio anuncioSalvo = anuncioRepository.save(anuncio);
		
		return new ResponseEntity<Anuncio>(anuncioSalvo, HttpStatus.CREATED);
	}
	
	@PutMapping(value = "/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Anuncio> atualizar(@PathVariable Long idUsuario,
										  @RequestBody Anuncio anuncio){
		
		Usuario usuario = new Usuario();
		usuario.setId(idUsuario);
		anuncio.setUsuario(usuario);
		
		Anuncio anuncioSalvo = anuncioRepository.save(anuncio);
		
		return new ResponseEntity<Anuncio>(anuncioSalvo, HttpStatus.OK);
	}
}
