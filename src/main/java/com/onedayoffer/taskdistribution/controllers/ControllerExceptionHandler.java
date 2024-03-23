package com.onedayoffer.taskdistribution.controllers;

import com.onedayoffer.taskdistribution.DTO.ExceptionDTO;
import com.onedayoffer.taskdistribution.exceptions.TaskNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice(annotations= RestController.class)
public class ControllerExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ExceptionDTO> handleException(TaskNotFoundException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDTO> handleException(Exception exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
