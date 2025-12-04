package com.tuandatcoder.trekkerbackend.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //    ACCOUNTS | CODE: 1XXX
    //    Accounts | Auth
    UNCATEGORIZED_EXCEPTION(1001, "Uncategorized Exception", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1004, "User not found", HttpStatus.NOT_FOUND),
    PASSWORD_INVALID(1005, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    TOKEN_INVALID(1006, "Invalid token", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_VERIFY(1007, "This account has not been verified", HttpStatus.FORBIDDEN),
    INTERNAL_SERVER_ERROR(1008, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    OLD_PASSWORD_INCORRECT(1009, "Old password incorrect", HttpStatus.BAD_REQUEST),
    PASSWORD_REPEAT_INCORRECT(1010, "Password repeat do not match", HttpStatus.BAD_REQUEST),
    NOT_LOGIN(1011, "You need to login", HttpStatus.UNAUTHORIZED),
    USERNAME_PASSWORD_NOT_CORRECT(1012, "Username or password is not correct", HttpStatus.UNAUTHORIZED),
    ACCOUNT_NOT_FOUND(1013, "Account not found", HttpStatus.NOT_FOUND),
    EMAIL_NOT_FOUND(1014, "Email not found, please register account.", HttpStatus.NOT_FOUND),
    USERNAME_TAKEN(1015, "Username is already taken", HttpStatus.CONFLICT),

    //    Accounts | Emails | CODE: 2XXX
    INVALID_EMAIL(2000, "Invalid email", HttpStatus.BAD_REQUEST),
    EMAIL_WAIT_VERIFY(2001, "This email has been registered and is not verified, please verify and login", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(2002, "This email has been registered, please log in!", HttpStatus.BAD_REQUEST),
    SUCCESS(2003, "Success", HttpStatus.OK),

    //    TRIPS | CODE: 3XXX - MỚI THÊM TỪ ĐÂY
    TRIP_NOT_FOUND(3001, "Trip not found or has been deleted", HttpStatus.NOT_FOUND),
    TRIP_FORBIDDEN(3002, "You don't have permission to perform this action on this trip", HttpStatus.FORBIDDEN),
    TRIP_TITLE_REQUIRED(3003, "Trip title is required", HttpStatus.BAD_REQUEST),
    TRIP_DESTINATION_REQUIRED(3004, "Destination is required", HttpStatus.BAD_REQUEST),
    TRIP_DATE_INVALID(3005, "End date must be after or equal to start date", HttpStatus.BAD_REQUEST),
    TRIP_PRIVACY_INVALID(3006, "Invalid privacy setting", HttpStatus.BAD_REQUEST),
    TRIP_STATUS_INVALID(3007, "Invalid trip status", HttpStatus.BAD_REQUEST),

    //    PHOTOS & MEDIA | CODE: 4XXX (dự phòng cho tương lai)
    PHOTO_NOT_FOUND(4001, "Photo not found", HttpStatus.NOT_FOUND),
    PHOTO_UPLOAD_FAILED(4002, "Failed to upload photo", HttpStatus.INTERNAL_SERVER_ERROR),
    PHOTO_FORBIDDEN(4003, "You can only manage your own photos", HttpStatus.FORBIDDEN),
    COVER_PHOTO_NOT_FOUND(4004, "Cover photo not found", HttpStatus.NOT_FOUND),

    //    PARTICIPANTS & COLLABORATIVE | CODE: 5XXX
    PARTICIPANT_ALREADY_EXISTS(5001, "You are already a participant of this trip", HttpStatus.BAD_REQUEST),
    PARTICIPANT_NOT_FOUND(5002, "Participant not found in this trip", HttpStatus.NOT_FOUND),
    TRIP_NOT_COLLABORATIVE(5003, "This trip is not collaborative", HttpStatus.BAD_REQUEST),
    MAX_PARTICIPANTS_REACHED(5004, "This trip has reached maximum participants", HttpStatus.BAD_REQUEST),

    //    GENERAL | AUTHORIZATION
    UNAUTHENTICATED(9001, "You must be logged in to perform this action", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(9003, "You don't have permission to access this resource", HttpStatus.FORBIDDEN),
    NOT_FOUND(9404, "Resource not found", HttpStatus.NOT_FOUND);

    private final Integer code;
    @Setter
    private String message;
    private final HttpStatus httpStatus;

    ErrorCode(Integer code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}