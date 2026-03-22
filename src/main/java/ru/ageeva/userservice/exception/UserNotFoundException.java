package ru.ageeva.userservice.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
    }

    public UserNotFoundException(String passport) {
        super("User not found with passport: " + passport);
    }
}
