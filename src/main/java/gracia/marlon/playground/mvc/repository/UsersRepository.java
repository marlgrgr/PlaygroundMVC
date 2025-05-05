package gracia.marlon.playground.mvc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gracia.marlon.playground.mvc.model.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

	@Query("SELECT U FROM Users U WHERE U.id = ?1 AND U.isRetired = false")
	Optional<Users> findById(Long id);

	@Query("SELECT U FROM Users U WHERE U.username = ?1 AND U.isRetired = false")
	List<Users> findByUsername(String username);

	@Query("SELECT U FROM Users U WHERE U.isRetired = false")
	Page<Users> findNotRetiredUsers(Pageable pageable);

	@Modifying
	@Query("UPDATE Users U SET U.isRetired = true WHERE U.id = ?1")
	int retireById(Long id);
}
