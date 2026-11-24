package dev.protoskey.exception;

public class LoginAlreadyTakenException extends Exception {
    public LoginAlreadyTakenException(String login) {
        super("Логин \"" + login + "\" уже занят");
    }
}
