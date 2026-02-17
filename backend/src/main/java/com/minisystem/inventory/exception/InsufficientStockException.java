package com.minisystem.inventory.exception;

public class InsufficientStockException extends RuntimeException{
    public InsufficientStockException(String msg){
        super(msg);
    }
}
