package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.UserDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.RoleNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.UserNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.UserRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.UserService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.UserBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserServiceImpl_Tests")
class UserServiceImplTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static UserBuilder userBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        userBuilder = new UserBuilder();
    }

    @BeforeEach
    private void setUp() {

        userService = new UserServiceImpl(userRepository, objectMapper, bCryptPasswordEncoder);
    }

    @Test
    void when_register_user_should_register_user() {

        String passwordEncoded = "encodedPassword";

        UserDTO userDTOExpected = (UserDTO) userBuilder.build(ObjectType.DTO);
        UserNode userExpectedAfterObjectMapping = (UserNode) userBuilder.withId(null).build(ObjectType.NODE);
        UserNode userExpectedAfterPasswordEncodingAndSetRoles = (UserNode) userBuilder.withId(null).withPassword(passwordEncoded)
                .withRoles(Set.of(new RoleNode("user"))).build(ObjectType.NODE);
        UserNode userExpected = (UserNode) userBuilder.withId(1L).withPassword(passwordEncoded)
                .withRoles(Set.of(new RoleNode("user"))).build(ObjectType.NODE);

        when(objectMapper.map(userDTOExpected, UserNode.class)).thenReturn(userExpectedAfterObjectMapping);
        when(bCryptPasswordEncoder.encode(userDTOExpected.getPassword())).thenReturn(passwordEncoded);
        when(userRepository.save(userExpectedAfterPasswordEncodingAndSetRoles)).thenReturn(userExpected);

        UserNode userActual = userService.register(userDTOExpected);

        assertAll(() -> assertEquals(userExpected.getId(), userActual.getId(),
                () -> "should return user with id: " + userExpected.getId() + ", but was" + userActual.getId()),
                () -> assertEquals(userExpected.getUserName(), userActual.getUserName(),
                        () -> "should return user with user name: " + userExpected.getUserName() + ", but was" + userActual.getUserName()),
                () -> assertEquals(userExpected.getPassword(), userActual.getPassword(),
                        () -> "should return user with user password: " + userExpected.getPassword() + ", but was" + userActual.getPassword()),
                () -> assertEquals(userExpected.getEmail(), userActual.getEmail(),
                        () -> "should return user with user email: " + userExpected.getEmail() + ", but was" + userActual.getEmail()),
                () -> assertEquals(userExpected.getRoles(), userActual.getRoles(),
                        () -> "should return user with user roles: " + userExpected.getRoles() + ", but was" + userActual.getRoles()),
                () -> verify(objectMapper, times(1)).map(userDTOExpected, UserNode.class),
                () -> verifyNoMoreInteractions(objectMapper),
                () -> verify(bCryptPasswordEncoder, times(1)).encode(userDTOExpected.getPassword()),
                () -> verifyNoMoreInteractions(bCryptPasswordEncoder),
                () -> verify(userRepository, times(1)).save(userExpectedAfterObjectMapping),
                () -> verifyNoMoreInteractions(userRepository));
    }

    @Test
    void when_user_exists_and_find_user_by_username_should_return_user() {

        String expectedUserName = "user123";

        Set<RoleNode> roles = Set.of(new RoleNode("user"));

        UserNode userExpected = (UserNode) userBuilder.withUserName(expectedUserName).withRoles(roles).build(ObjectType.NODE);

        when(userRepository.findByUserName(expectedUserName)).thenReturn(Optional.of(userExpected));

        Optional<UserNode> userActualOptional = userService.findByUserName(expectedUserName);

        assertTrue(userActualOptional.isPresent(), () -> "shouldn`t return empty optional");

        UserNode userActual = userActualOptional.get();

        assertAll(() -> assertEquals(userExpected.getId(), userActual.getId(),
                () -> "should return user with id: " + userExpected.getId() + ", but was" + userActual.getId()),
                () -> assertEquals(userExpected.getUserName(), userActual.getUserName(),
                        () -> "should return user with user name: " + userExpected.getUserName() + ", but was" + userActual.getUserName()),
                () -> assertEquals(userExpected.getPassword(), userActual.getPassword(),
                        () -> "should return user with user password: " + userExpected.getPassword() + ", but was" + userActual.getPassword()),
                () -> assertEquals(userExpected.getEmail(), userActual.getEmail(),
                        () -> "should return user with user email: " + userExpected.getEmail() + ", but was" + userActual.getEmail()),
                () -> assertEquals(userExpected.getRoles(), userActual.getRoles(),
                        () -> "should return user with user roles: " + userExpected.getRoles() + ", but was" + userActual.getRoles()),
                () -> verify(userRepository, times(1)).findByUserName(expectedUserName),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(bCryptPasswordEncoder));
    }

    @Test
    void when_user_not_exists_and_find_user_by_username_should_return_empty_optional() {

        String notExistingUserName = "user123";

        when(userRepository.findByUserName(notExistingUserName)).thenReturn(Optional.empty());

        Optional<UserNode> userActualOptional = userService.findByUserName(notExistingUserName);

        assertAll(() -> assertTrue(userActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(userRepository, times(1)).findByUserName(notExistingUserName),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(bCryptPasswordEncoder));
    }

    @Test
    void when_user_exists_and_find_user_by_email_should_return_user() {

        String expectedUserEmail = "user123@email.com";

        Set<RoleNode> roles = Set.of(new RoleNode("user"));

        UserNode userExpected = (UserNode) userBuilder.withEmail(expectedUserEmail).withRoles(roles).build(ObjectType.NODE);

        when(userRepository.findByEmail(expectedUserEmail)).thenReturn(Optional.of(userExpected));

        Optional<UserNode> userActualOptional = userService.findByEmail(expectedUserEmail);

        assertTrue(userActualOptional.isPresent(), () -> "shouldn`t return empty optional");

        UserNode userActual = userActualOptional.get();

        assertAll(() -> assertEquals(userExpected.getId(), userActual.getId(),
                () -> "should return user with id: " + userExpected.getId() + ", but was" + userActual.getId()),
                () -> assertEquals(userExpected.getUserName(), userActual.getUserName(),
                        () -> "should return user with user name: " + userExpected.getUserName() + ", but was" + userActual.getUserName()),
                () -> assertEquals(userExpected.getPassword(), userActual.getPassword(),
                        () -> "should return user with user password: " + userExpected.getPassword() + ", but was" + userActual.getPassword()),
                () -> assertEquals(userExpected.getEmail(), userActual.getEmail(),
                        () -> "should return user with user email: " + userExpected.getEmail() + ", but was" + userActual.getEmail()),
                () -> assertEquals(userExpected.getRoles(), userActual.getRoles(),
                        () -> "should return user with user roles: " + userExpected.getRoles() + ", but was" + userActual.getRoles()),
                () -> verify(userRepository, times(1)).findByEmail(expectedUserEmail),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(bCryptPasswordEncoder));
    }

    @Test
    void when_user_not_exists_and_find_user_by_email_should_return_empty_optional() {

        String notExistingUserEmail = "user123@email.com";

        when(userRepository.findByEmail(notExistingUserEmail)).thenReturn(Optional.empty());

        Optional<UserNode> userActualOptional = userService.findByEmail(notExistingUserEmail);

        assertAll(() -> assertTrue(userActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(userRepository, times(1)).findByEmail(notExistingUserEmail),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(bCryptPasswordEncoder));
    }

    @Test
    void when_user_exists_and_find_user_by_name_or_email_should_return_user() {

        String expectedUseName = "user123";
        String expectedUserEmail = "user123@email.com";

        Set<RoleNode> roles = Set.of(new RoleNode("user"));

        UserNode userExpected = (UserNode) userBuilder.withUserName(expectedUseName).withEmail(expectedUserEmail).withRoles(roles).build(ObjectType.NODE);

        when(userRepository.findByUserNameOrEmail(expectedUseName, expectedUserEmail)).thenReturn(Optional.of(userExpected));

        Optional<UserNode> userActualOptional = userService.findByUserNameOrEmail(expectedUseName, expectedUserEmail);

        assertTrue(userActualOptional.isPresent(), () -> "shouldn`t return empty optional");

        UserNode userActual = userActualOptional.get();

        assertAll(() -> assertEquals(userExpected.getId(), userActual.getId(),
                () -> "should return user with id: " + userExpected.getId() + ", but was" + userActual.getId()),
                () -> assertEquals(userExpected.getUserName(), userActual.getUserName(),
                        () -> "should return user with user name: " + userExpected.getUserName() + ", but was" + userActual.getUserName()),
                () -> assertEquals(userExpected.getPassword(), userActual.getPassword(),
                        () -> "should return user with user password: " + userExpected.getPassword() + ", but was" + userActual.getPassword()),
                () -> assertEquals(userExpected.getEmail(), userActual.getEmail(),
                        () -> "should return user with user email: " + userExpected.getEmail() + ", but was" + userActual.getEmail()),
                () -> assertEquals(userExpected.getRoles(), userActual.getRoles(),
                        () -> "should return user with user roles: " + userExpected.getRoles() + ", but was" + userActual.getRoles()),
                () -> verify(userRepository, times(1)).findByUserNameOrEmail(expectedUseName, expectedUserEmail),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(bCryptPasswordEncoder));
    }

    @Test
    void when_user_not_exists_and_find_user_by_name_or_email_should_return_empty_optional() {

        String notExistingUserName = "user123";
        String notExistingUserEmail = "user123";

        when(userRepository.findByUserNameOrEmail(notExistingUserName, notExistingUserEmail)).thenReturn(Optional.empty());

        Optional<UserNode> userActualOptional = userService.findByUserNameOrEmail(notExistingUserName, notExistingUserEmail);

        assertAll(() -> assertTrue(userActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(userRepository, times(1)).findByUserNameOrEmail(notExistingUserName, notExistingUserEmail),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(bCryptPasswordEncoder));
    }
}
