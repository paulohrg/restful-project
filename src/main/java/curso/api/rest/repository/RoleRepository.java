package curso.api.rest.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import curso.api.rest.model.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
	
	@Query(value = "select r.* from \"role\" r where r.nome_role = 'ROLE_CLIENTE'", nativeQuery = true)
	Role buscaRolePadrao();
	
	@Transactional
	@Modifying
	@Query(value = "insert into usuario_role values (?1, ?2)", nativeQuery = true)
	void salvarPermissao(Long idUsuario, Long idRole);
}
