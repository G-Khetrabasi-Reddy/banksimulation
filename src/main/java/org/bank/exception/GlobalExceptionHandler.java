package org.bank.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        int status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(); // default: 500 internal error
        //Account-related exceptions
        if (exception instanceof DuplicateAccountException) {
            status = Response.Status.CONFLICT.getStatusCode();
        } else if (exception instanceof InvalidTransactionException) {
            status = Response.Status.BAD_REQUEST.getStatusCode();
        } else if (exception instanceof AccountNotFoundException) {
            status = Response.Status.NOT_FOUND.getStatusCode();
        } else if (exception instanceof AccountAlreadyClosedException) {
            status = Response.Status.CONFLICT.getStatusCode();
        } else if (exception instanceof InsufficientBalanceException) {
            status = Response.Status.BAD_REQUEST.getStatusCode();
        }
        // Customer-related exception
        else if (exception instanceof InvalidCustomerDataException) {
            status = Response.Status.BAD_REQUEST.getStatusCode();
        } else if (exception instanceof CustomerNotFoundException) {
            status = Response.Status.NOT_FOUND.getStatusCode();
        } else if (exception instanceof DuplicateCustomerException) {
            status = Response.Status.CONFLICT.getStatusCode();
        }
        // Transaction-related exception
        else if (exception instanceof TransactionFailedException) {
            status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        }


        return Response.status(status)
                .entity(Map.of("error", exception.getMessage()))
                .build();
    }
}
