package gracia.marlon.playground.mvc.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import gracia.marlon.playground.mvc.model.Role;
import gracia.marlon.playground.mvc.model.Users;
import gracia.marlon.playground.mvc.repository.RoleRepository;
import gracia.marlon.playground.mvc.repository.UserRoleRepository;
import gracia.marlon.playground.mvc.repository.UsersRepository;

public class DataLoaderTest {

	private final UsersRepository userRepository;

	private final RoleRepository roleRepository;

	private final UserRoleRepository userRoleRepository;

	private final PasswordEncoder passwordEncoder;

	private final RedissonClient redissonClient;

	private final Environment env;

	private final DataLoader dataLoader;

	public DataLoaderTest() {
		this.userRepository = Mockito.mock(UsersRepository.class);
		this.roleRepository = Mockito.mock(RoleRepository.class);
		this.userRoleRepository = Mockito.mock(UserRoleRepository.class);
		this.passwordEncoder = Mockito.mock(PasswordEncoder.class);
		this.redissonClient = Mockito.mock(RedissonClient.class);
		this.env = Mockito.mock(Environment.class);
		Mockito.when(this.env.getProperty(Mockito.eq("admin.default.user"), Mockito.anyString())).thenReturn("admin");
		Mockito.when(this.env.getProperty(Mockito.eq("admin.default.password"), Mockito.anyString()))
				.thenReturn("admin123");
		this.dataLoader = new DataLoader(this.userRepository, this.roleRepository, this.userRoleRepository,
				this.passwordEncoder, this.redissonClient, this.env);
	}

	@Test
	public void runSuccessful() throws Exception {
		RLock lock = Mockito.mock(RLock.class);

		Role role = new Role();
		role.setId(1L);

		List<Role> roleList = new ArrayList<Role>();
		roleList.add(role);

		Users user = new Users();
		user.setId(1L);

		List<Users> userList = new ArrayList<Users>();
		userList.add(user);

		Mockito.when(this.redissonClient.getLock(Mockito.anyString())).thenReturn(lock);
		Mockito.when(lock.tryLock(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(TimeUnit.class))).thenReturn(true);
		Mockito.when(this.roleRepository.findByRole(Mockito.anyString())).thenReturn(roleList);
		Mockito.when(this.userRepository.findByUsername(Mockito.anyString())).thenReturn(userList);
		Mockito.when(lock.isHeldByCurrentThread()).thenReturn(true);
		Mockito.doNothing().when(lock).unlock();

		this.dataLoader.run("");

		Mockito.verify(this.userRoleRepository, Mockito.never()).save(Mockito.any());
	}

	@Test
	public void runNoLocked() throws Exception {
		RLock lock = Mockito.mock(RLock.class);

		Mockito.when(this.redissonClient.getLock(Mockito.anyString())).thenReturn(lock);
		Mockito.when(lock.tryLock(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(TimeUnit.class))).thenReturn(false);
		Mockito.when(lock.isHeldByCurrentThread()).thenReturn(false);

		this.dataLoader.run("");

		Mockito.verify(this.userRoleRepository, Mockito.never()).save(Mockito.any());
	}

	@Test
	public void runLockException() throws Exception {
		RLock lock = Mockito.mock(RLock.class);

		Mockito.when(this.redissonClient.getLock(Mockito.anyString())).thenReturn(lock);
		Mockito.when(lock.tryLock(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(TimeUnit.class)))
				.thenThrow(new RuntimeException());

		Mockito.when(lock.isHeldByCurrentThread()).thenReturn(false);

		this.dataLoader.run("");

		Mockito.verify(this.userRoleRepository, Mockito.never()).save(Mockito.any());
	}

}
