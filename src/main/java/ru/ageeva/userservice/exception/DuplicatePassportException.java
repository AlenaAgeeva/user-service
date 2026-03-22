package ru.ageeva.userservice.exception;

public class DuplicatePassportException extends RuntimeException {
    public DuplicatePassportException(String passport) {
        super("User with passport '" + passport + "' already exists");
    }
}
