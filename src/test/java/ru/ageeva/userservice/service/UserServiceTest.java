package ru.ageeva.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ageeva.userservice.dto.UserCreateDto;
import ru.ageeva.userservice.dto.UserResponseDto;
import ru.ageeva.userservice.dto.UserUpdateDto;
import ru.ageeva.userservice.entity.UserEntity;
import ru.ageeva.userservice.exception.DuplicatePassportException;
import ru.ageeva.userservice.exception.UserNotFoundException;
import ru.ageeva.userservice.mapper.UserMapper;
import ru.ageeva.userservice.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private UserEntity userEntity;
    private UserCreateDto createDto;
    private UserResponseDto responseDto;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity(userId, "Ivan", "Ivanov", "1wr3v5y5br56500");
        createDto = new UserCreateDto("Ivan", "Ivanov", "1wr3v5y5br56500");
        responseDto = new UserResponseDto(userId, "Ivan", "Ivanov", "1wr3v5y5br56500");
    }

    @Test
    void shouldReturnSavedUser() {
        when(userRepository.existsByPassport(createDto.passport())).thenReturn(false);
        when(userMapper.toEntity(createDto)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(responseDto);

        UserResponseDto result = userService.createUser(createDto);

        assertThat(result).isEqualTo(responseDto);
        verify(userRepository).save(userEntity);
    }

    @Test
    void shouldThrowDuplicateUserNameException() {
        when(userRepository.existsByPassport(createDto.passport())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(createDto))
                .isInstanceOf(DuplicatePassportException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldReturnUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(responseDto);

        UserResponseDto result = userService.getUserById(userId);

        assertThat(result).isEqualTo(responseDto);
    }

    @Test
    void shouldThrowUserNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldReturnListOfUsers() {
        List<UserEntity> entities = List.of(userEntity);
        when(userRepository.findAll()).thenReturn(entities);
        when(userMapper.toDto(userEntity)).thenReturn(responseDto);

        List<UserResponseDto> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(responseDto);
    }

    @Test
    void shouldReturnListOfUserIds() {
        List<UserEntity> entities = List.of(userEntity);
        when(userRepository.findAll()).thenReturn(entities);

        List<Long> result = userService.getAllUserIds();

        assertThat(result).containsExactly(userId);
    }

    @Test
    void shouldReturnUpdatedUser() {
        UserUpdateDto updateDto = new UserUpdateDto("Stepan", "Stepanov");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(responseDto);

        UserResponseDto result = userService.updateUser(userId, updateDto);

        assertThat(result).isEqualTo(responseDto);
        verify(userMapper).updateEntityFromDto(updateDto, userEntity);
        verify(userRepository).save(userEntity);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUpdating() {
        UserUpdateDto updateDto = new UserUpdateDto("Stepan", "Stepanov");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, updateDto))
                .isInstanceOf(UserNotFoundException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldDeleteUser() {
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenDeleting() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(UserNotFoundException.class);
        verify(userRepository, never()).deleteById(any());
    }
}