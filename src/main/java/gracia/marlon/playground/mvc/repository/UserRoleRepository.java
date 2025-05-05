package gracia.marlon.playground.mvc.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gracia.marlon.playground.mvc.model.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

	@Query("SELECT UR FROM UserRole UR WHERE UR.user.isRetired=false")
	Page<UserRole> findAllNotRetired(Pageable pageable);

	@Query("SELECT UR FROM UserRole UR WHERE UR.user.id = ?1 AND UR.user.isRetired=false")
	Page<UserRole> findByUserId(Long userId, Pageable pageable);

	@Query("SELECT UR FROM UserRole UR WHERE UR.role.id = ?1 AND UR.user.isRetired=false")
	Page<UserRole> findByRoleId(Long roleId, Pageable pageable);

	@Query("SELECT UR FROM UserRole UR WHERE UR.user.id = ?1 AND UR.role.id = ?2 AND UR.user.isRetired=false")
	List<UserRole> findByUserIdAndRoleId(Long userId, Long roleId);

	@Query("SELECT UR FROM UserRole UR WHERE UR.user.id = ?1 AND UR.user.isRetired=false")
	List<UserRole> findAllByUserId(Long userId);

	@Modifying
	@Query("DELETE FROM UserRole UR WHERE UR.user.id = ?1 AND UR.role.id = ?2")
	int deleteByUserIDAndRoleId(Long userId, Long roleId);

}
