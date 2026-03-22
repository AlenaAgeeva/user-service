package ru.ageeva.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.ageeva.userservice.dto.UserCreateDto;
import ru.ageeva.userservice.dto.UserResponseDto;
import ru.ageeva.userservice.dto.UserUpdateDto;
import ru.ageeva.userservice.exception.DuplicatePassportException;
import ru.ageeva.userservice.exception.UserNotFoundException;
import ru.ageeva.userservice.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldSaveAndReturnUser() {
        UserCreateDto dto = new UserCreateDto("Ivan", "Ivanov", "1wr3v5y5br56500");
        UserResponseDto created = userService.createUser(dto);
        assertThat(created.id()).isNotNull();
        assertThat(created.firstName()).isEqualTo("Ivan");
        assertThat(created.lastName()).isEqualTo("Ivanov");
        assertThat(created.passport()).isEqualTo("1wr3v5y5br56500");
        assertThat(userRepository.existsByPassport("1wr3v5y5br56500")).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenDuplicateUserName() {
        userService.createUser(new UserCreateDto("Ivan", "Ivanov", "1wr3v5y5br56500"));
        assertThatThrownBy(() -> userService.createUser(new UserCreateDto("Stepan", "Stepanov",
                "1wr3v5y5br56500")))
                .isInstanceOf(DuplicatePassportException.class);
    }

    @Test
    void shouldReturnUserById() {
        UserResponseDto created = userService.createUser(new UserCreateDto("Ivan", "Ivanov",
                "1wr3v5y5br56500"));
        UserResponseDto found = userService.getUserById(created.id());
        assertThat(found).isEqualTo(created);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldReturnListOfUsers() {
        userService.createUser(new UserCreateDto("Ivan", "Ivanov", "1wr3v5y5br56500"));
        userService.createUser(new UserCreateDto("Fedor", "Fedorov", "8hr0g735br89jf4"));
        List<UserResponseDto> all = userService.getAllUsers();
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void shouldUpdateUserFields() {
        UserResponseDto created = userService.createUser(new UserCreateDto("Ivan", "Ivanov",
                "1wr3v5y5br56500"));
        UserUpdateDto updateDto = new UserUpdateDto("Fedor", "Fedorov");
        UserResponseDto updated = userService.updateUser(created.id(), updateDto);
        assertThat(updated.firstName()).isEqualTo("Fedor");
        assertThat(updated.lastName()).isEqualTo("Fedorov");
        assertThat(updated.passport()).isEqualTo("1wr3v5y5br56500");
    }

    @Test
    void shouldDeleteUser() {
        UserResponseDto created = userService.createUser(new UserCreateDto("Ivan", "Ivanov",
                "1wr3v5y5br56500"));
        userService.deleteUser(created.id());
        assertThat(userRepository.findById(created.id())).isEmpty();
    }
}