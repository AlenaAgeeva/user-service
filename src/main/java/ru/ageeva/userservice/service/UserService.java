package ru.ageeva.userservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import ru.ageeva.userservice.dto.UserCreateDto;
import ru.ageeva.userservice.dto.UserResponseDto;
import ru.ageeva.userservice.dto.UserUpdateDto;
import ru.ageeva.userservice.entity.UserEntity;
import ru.ageeva.userservice.exception.DuplicatePassportException;
import ru.ageeva.userservice.exception.UserNotFoundException;
import ru.ageeva.userservice.mapper.UserMapper;
import ru.ageeva.userservice.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", key = "'allUsers'"),
            @CacheEvict(value = "users", key = "'ids'")
    })
    public UserResponseDto createUser(UserCreateDto createDto) {
        try {
            if (userRepository.existsByPassport(createDto.passport())) {
                log.warn("Attempt to create user with duplicate passport: {}", createDto.passport());
                throw new DuplicatePassportException(createDto.passport());
            }
            UserEntity saved = userRepository.save(userMapper.toEntity(createDto));
            return userMapper.toDto(saved);

        } catch (DuplicatePassportException e) {
            log.error("Duplicate passport error while creating user: {}", createDto.passport(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while creating user with passport: {}", createDto.passport(), e);
            throw e;
        }
    }

    @Cacheable(value = "users", key = "#id")
    public UserResponseDto getUserById(Long id) {
        try {
            UserEntity entity = userRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("User not found with id: {}", id);
                        return new UserNotFoundException(id);
                    });
            return userMapper.toDto(entity);
        } catch (UserNotFoundException e) {
            log.error("User not found error for id: {}", id, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while fetching user by id: {}", id, e);
            throw e;
        }
    }

    @Cacheable(value = "users", key = "'allUsers'")
    public List<UserResponseDto> getAllUsers() {
        try {
            List<UserResponseDto> users = userRepository.findAll().stream()
                    .map(userMapper::toDto)
                    .toList();
            return users;
        } catch (Exception e) {
            log.error("Unexpected error while fetching all users", e);
            throw e;
        }
    }

    @Cacheable(value = "users", key = "'ids'")
    public List<Long> getAllUserIds() {
        try {
            List<Long> ids = userRepository.findAll().stream()
                    .map(UserEntity::getId)
                    .toList();
            return ids;
        } catch (Exception e) {
            log.error("Unexpected error while fetching all user IDs", e);
            throw e;
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "users", key = "'allUsers'"),
            @CacheEvict(value = "users", key = "'ids'")
    })
    public UserResponseDto updateUser(Long id, UserUpdateDto updateDto) {
        try {
            UserEntity entity = userRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("User not found for update with id: {}", id);
                        return new UserNotFoundException(id);
                    });
            userMapper.updateEntityFromDto(updateDto, entity);
            UserEntity updated = userRepository.save(entity);
            return userMapper.toDto(updated);
        } catch (UserNotFoundException e) {
            log.error("User not found error while updating user id: {}", id, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating user with id: {}", id, e);
            throw e;
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "users", key = "'allUsers'"),
            @CacheEvict(value = "users", key = "'ids'")
    })
    public void deleteUser(Long id) {
        try {
            if (!userRepository.existsById(id)) {
                log.warn("User not found for deletion with id: {}", id);
                throw new UserNotFoundException(id);
            }
            userRepository.deleteById(id);
        } catch (UserNotFoundException e) {
            log.error("User not found error while deleting user id: {}", id, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting user with id: {}", id, e);
            throw e;
        }
    }
}