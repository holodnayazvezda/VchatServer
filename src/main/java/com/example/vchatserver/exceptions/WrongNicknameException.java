package com.example.vchatserver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The nickname is wrong")
public class WrongNicknameException extends RuntimeException{}
