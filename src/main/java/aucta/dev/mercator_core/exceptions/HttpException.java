package aucta.dev.mercator_core.exceptions;

public class HttpException extends Exception{

    private Integer code;

    public HttpException(String message, Integer code){
        super(message);
        this.code = code;
    }
}