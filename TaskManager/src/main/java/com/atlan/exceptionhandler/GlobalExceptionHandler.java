package com.atlan.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.atlan.entity.JobErrorResponse;


@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler
	public ResponseEntity<JobErrorResponse> jobErrorHandler(NumberFormatException ex)
	{
		JobErrorResponse error=new JobErrorResponse(HttpStatus.BAD_REQUEST.value(),"please enter a valid job id It should be Number only",System.currentTimeMillis());
		return new ResponseEntity<JobErrorResponse>(error,HttpStatus.BAD_REQUEST);
		
	}
	
}
