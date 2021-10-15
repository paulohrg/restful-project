package curso.api.rest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import curso.api.rest.model.Anuncio;

@Repository
public interface AnuncioRepository extends CrudRepository<Anuncio, Long>{
	
	@Query(value = "select a.* from Anuncio a where a.id_usuario = :idUsuario", nativeQuery = true)
	List<Anuncio> buscaAnunciosPorUsuario(Long idUsuario);
}
