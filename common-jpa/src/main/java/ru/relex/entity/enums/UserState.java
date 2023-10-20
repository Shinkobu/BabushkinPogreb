package ru.relex.entity.enums;

/**
 *
 */
public enum UserState {
    BASIC_STATE, // User may only use specific commands or send file, otherwise he will get error message
    WAIT_FOR_EMAIL_STATE // App will accept only email or registration cancellation command
}
