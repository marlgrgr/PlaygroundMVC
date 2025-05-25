package gracia.marlon.playground.mvc.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.dtos.UserWithPasswordDTO;
import gracia.marlon.playground.mvc.services.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserGraphQLController {

    private final UserService userService;

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PagedResponse<UserDTO> getUsers(
            @Argument Integer page,
            @Argument Integer pageSize) {
        return userService.getUsers(page, pageSize);
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO getUserById(@Argument Long userId) {
        return userService.getUserById(userId);
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO getUserByUsername(@Argument String username) {
        return userService.getUserByUsername(username);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean createUser(@Argument("user") UserWithPasswordDTO userDto) {
        userService.createUser(userDto);
        return true;
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteUser(@Argument Long userId) {
        userService.deleteUser(userId);
        return true;
    }
}