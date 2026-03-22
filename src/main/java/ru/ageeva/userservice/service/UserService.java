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
        if (userRepository.existsByPassport(createDto.passport())) {
            throw new DuplicatePassportException(createDto.passport());
        }
        UserEntity saved = userRepository.save(userMapper.toEntity(createDto));
        return userMapper.toDto(saved);
    }

    @Cacheable(value = "users", key = "#id")
    public UserResponseDto getUserById(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toDto(entity);
    }

    @Cacheable(value = "users", key = "'allUsers'")
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Cacheable(value = "users", key = "'ids'")
    public List<Long> getAllUserIds() {
        return userRepository.findAll().stream()
                .map(UserEntity::getId)
                .toList();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "users", key = "'allUsers'"),
            @CacheEvict(value = "users", key = "'ids'")
    })
    public UserResponseDto updateUser(Long id, UserUpdateDto updateDto) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userMapper.updateEntityFromDto(updateDto, entity);
        return userMapper.toDto(userRepository.save(entity));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "users", key = "'allUsers'"),
            @CacheEvict(value = "users", key = "'ids'")
    })
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}