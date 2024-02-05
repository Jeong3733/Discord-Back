package com.hansori.ws.stomp.dto.response.error;

import lombok.Getter;
import lombok.ToString;

@Getter
public enum ErrorCode {

    USER_NOT_FOUND(1001, "User Not Found"),
    USER_ALREADY_EXIST(1002, "User Already Exist"),
    USER_NOT_MATCH(1003, "User Not Match"),
    USER_NOT_LOGIN(1004, "User Not Login"),
    USER_NOT_AUTHORIZED(1005, "User Not Authorized"),
    USER_NOT_ACTIVE(1006, "User Not Active"),
    USER_NOT_ADMIN(1007, "User Not Admin"),
    USER_NOT_OWNER(1008, "User Not Owner"),
    USER_NOT_MEMBER(1009, "User Not Member"),
    USER_NOT_ALLOWED(1010, "User Not Allowed"),
    USER_NOT_ACCEPTED(1011, "User Not Accepted"),
    USER_NOT_CONFIRMED(1012, "User Not Confirmed"),
    USER_NOT_VERIFIED(1013, "User Not Verified"),
    USER_NOT_APPROVED(1014, "User Not Approved"),
    USER_NOT_DELETED(1015, "User Not Deleted"),
    USER_NOT_UPDATED(1016, "User Not Updated"),
    USER_NOT_CREATED(1017, "User Not Created"),
    USER_NOT_LOGGED_IN(1018, "User Not Logged In"),
    USER_NOT_LOGGED_OUT(1019, "User Not Logged Out"),
    USER_NOT_REGISTERED(1020, "User Not Registered"),
    USER_NOT_REMOVED(1021, "User Not Removed"),
    USER_NOT_RECOVERED(1022, "User Not Recovered"),
    USER_NOT_RESET(1023, "User Not Reset"),
    USER_NOT_RESENT(1024, "User Not Resent"),
    USER_NOT_SENT(1025, "User Not Sent"),
    USER_NOT_SIGNED(1026, "User Not Signed"),
    USER_NOT_SIGNED_UP(1027, "User Not Signed Up"),
    USER_NOT_SIGNED_IN(1028, "User Not Signed In"),
    USER_NOT_SIGNED_OUT(1029, "User Not Signed"),

    INTERNAL_SERVER_ERROR(5001, "Internal Server Error");



    private final int code;
    private final String message;


    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


}
