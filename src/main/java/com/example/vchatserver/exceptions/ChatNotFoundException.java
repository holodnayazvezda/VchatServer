package com.example.vchatserver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "There is no such chat")
public class ChatNotFoundException extends RuntimeException {
}
