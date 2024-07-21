package com.multi.laptellect.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    private int status;
    private String message;
    private String code;
}
