package aucta.dev.mercator_core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BadRequestError extends HttpException{

    public BadRequestError(String message){
        super(message, HttpStatus.BAD_REQUEST.value());
    }
}
