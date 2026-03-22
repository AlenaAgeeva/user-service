package ru.ageeva.userservice.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.ageeva.userservice.dto.UserCreateDto;
import ru.ageeva.userservice.dto.UserResponseDto;
import ru.ageeva.userservice.dto.UserUpdateDto;
import ru.ageeva.userservice.entity.UserEntity;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldMapEntityToDto() {
        UserEntity entity = new UserEntity(1L, "Ivan", "Ivanov", "1wr3v5y5br56500");
        UserResponseDto dto = mapper.toDto(entity);
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.firstName()).isEqualTo("Ivan");
        assertThat(dto.lastName()).isEqualTo("Ivanov");
        assertThat(dto.passport()).isEqualTo("1wr3v5y5br56500");
    }

    @Test
    void shouldMapCreateDtoToEntity() {
        UserCreateDto createDto = new UserCreateDto("Ivan", "Ivanov", "1wr3v5y5br56500");
        UserEntity entity = mapper.toEntity(createDto);
        assertThat(entity.getId()).isNull();
        assertThat(entity.getFirstName()).isEqualTo("Ivan");
        assertThat(entity.getLastName()).isEqualTo("Ivanov");
        assertThat(entity.getPassport()).isEqualTo("1wr3v5y5br56500");
    }

    @Test
    void shouldUpdateFieldsFromDto() {
        UserEntity entity = new UserEntity(1L, "Ivan", "Ivanov", "1wr3v5y5br56500");
        UserUpdateDto updateDto = new UserUpdateDto("Fedor", "Fedorov");
        mapper.updateEntityFromDto(updateDto, entity);
        assertThat(entity.getFirstName()).isEqualTo("Fedor");
        assertThat(entity.getLastName()).isEqualTo("Fedorov");
        assertThat(entity.getPassport()).isEqualTo("1wr3v5y5br56500");
    }
}