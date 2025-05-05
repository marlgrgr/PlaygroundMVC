package gracia.marlon.playground.mvc.configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import gracia.marlon.playground.mvc.model.Role;
import gracia.marlon.playground.mvc.model.UserRole;
import gracia.marlon.playground.mvc.model.Users;
import gracia.marlon.playground.mvc.repository.RoleRepository;
import gracia.marlon.playground.mvc.repository.UserRoleRepository;
import gracia.marlon.playground.mvc.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataLoader implements CommandLineRunner {

	private final UsersRepository userRepository;

	private final RoleRepository roleRepository;

	private final UserRoleRepository userRoleRepository;

	private final PasswordEncoder passwordEncoder;

	private final RedissonClient redissonClient;

	private final String defaultAdminUser;

	private final String defaultAdminPassword;

	private final int MAX_WAIT_FOR_LOCK_TIME = 10;

	private final int MAX_TIME_WITH_LOCK = 60;
	
	private final String LOAD_DATA_LOCK = "load-data-lock";

	public DataLoader(UsersRepository userRepository, RoleRepository roleRepository,
			UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder, RedissonClient redissonClient,
			Environment env) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.userRoleRepository = userRoleRepository;
		this.passwordEncoder = passwordEncoder;
		this.redissonClient = redissonClient;
		this.defaultAdminUser = env.getProperty("admin.default.user", "admin");
		this.defaultAdminPassword = env.getProperty("admin.default.password", "admin123");
	}

	@Override
	public void run(String... args) throws Exception {

		RLock lock = this.redissonClient.getLock(LOAD_DATA_LOCK);
		try {
			boolean isLocked = lock.tryLock(MAX_WAIT_FOR_LOCK_TIME, MAX_TIME_WITH_LOCK, TimeUnit.SECONDS);

			if (!isLocked) {
				return;
			}

			final List<Role> adminRoleList = this.roleRepository.findByRole("ROLE_ADMIN");
			final List<Role> userRoleList = this.roleRepository.findByRole("ROLE_USER");

			Role adminRole = null;
			Role userRole = null;

			if (adminRoleList.isEmpty()) {
				adminRole = new Role(null, "ROLE_ADMIN");
				this.roleRepository.save(adminRole);
			} else {
				adminRole = adminRoleList.getFirst();
			}

			if (userRoleList.isEmpty()) {
				userRole = new Role(null, "ROLE_USER");
				this.roleRepository.save(userRole);
			} else {
				userRole = userRoleList.getFirst();
			}

			List<Users> userList =this.userRepository.findByUsername(this.defaultAdminUser); 
			if (userList.isEmpty()) {

				Users admin = new Users(null, this.defaultAdminUser,
						this.passwordEncoder.encode(this.defaultAdminPassword), this.defaultAdminUser, true, false);
				this.userRepository.save(admin);
				final UserRole adminRoleAdmin = new UserRole(null, admin, adminRole);
				final UserRole adminRoleUser = new UserRole(null, admin, userRole);

				this.userRoleRepository.save(adminRoleAdmin);
				this.userRoleRepository.save(adminRoleUser);
			}
		} catch (Exception e) {
			log.error("An error occurred while creating the starting data", e);
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}
}