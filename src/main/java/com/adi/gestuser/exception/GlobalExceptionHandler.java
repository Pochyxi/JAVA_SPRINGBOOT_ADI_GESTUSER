package com.adi.gestuser.exception;

import com.adi.gestuser.dto.ErrorDetailsDTO;
import com.adi.gestuser.dto.ValidationDetailDTO;
import com.adi.gestuser.dto.ValidationErrorDetailsDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            ResourceNotFoundException.class,
            appException.class,
            DataIntegrityViolationException.class,
            Exception.class
    })
    public ResponseEntity<ErrorDetailsDTO> handleResourceNotFoundException( Exception exception,
                                                                            WebRequest webRequest ) {

        switch (exception) {
            // ECCEZIONE DI RISORSA NON TROVATA
            case ResourceNotFoundException resourceNotFoundException -> {
                ErrorDetailsDTO errorDetailsDto = new ErrorDetailsDTO( new Date(), resourceNotFoundException.getMessage(),
                        webRequest.getDescription( false ) );

                return new ResponseEntity<>( errorDetailsDto, HttpStatus.NOT_FOUND );

            }
            // ECCEZIONE GENERICA
            case appException appException -> {
                ErrorDetailsDTO errorDetailsDto = new ErrorDetailsDTO( new Date(), appException.getMessage(),
                        webRequest.getDescription( false ) );

                return new ResponseEntity<>( errorDetailsDto, appException.getStatus() );
            }
            // ECCEZIONE DI VALIDAZIONE DEI CAMPI DI UNA RICHIESTA
            case MethodArgumentNotValidException methodArgumentNotValidException -> {
                ErrorDetailsDTO errorDetailsDto = new ErrorDetailsDTO( new Date(), methodArgumentNotValidException.getMessage(),
                        webRequest.getDescription( false ) );

                return new ResponseEntity<>( errorDetailsDto, HttpStatus.BAD_REQUEST );
            }

            // ECCEZIONE DI VIOLAZIONE DELL'INTEGRITÀ DEI DATI
            case DataIntegrityViolationException dataIntegrityViolationException -> {
                Throwable rootCause = dataIntegrityViolationException.getRootCause();
                String rootCauseMessage = rootCause != null ? rootCause.getMessage() : dataIntegrityViolationException.getMessage();



                ErrorDetailsDTO errorDetailsDto = new ErrorDetailsDTO( new Date(), rootCauseMessage, webRequest.getDescription( false ) );

                return new ResponseEntity<>( errorDetailsDto, HttpStatus.BAD_REQUEST );
            }

            case null, default -> {
                assert exception != null;
                ErrorDetailsDTO errorDetailsDto = new ErrorDetailsDTO( new Date(), exception.getMessage(),
                        webRequest.getDescription( false ) );

                return new ResponseEntity<>( errorDetailsDto, HttpStatus.INTERNAL_SERVER_ERROR );
            }
        }

    }


  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid( MethodArgumentNotValidException ex,
                                                                 HttpHeaders headers,
                                                                 HttpStatusCode status,
                                                                 WebRequest request
  ) {
    ValidationErrorDetailsDTO errorDetails = new ValidationErrorDetailsDTO();
    List<ValidationDetailDTO> validationDetailDTOList = new ArrayList<>();

    ex.getBindingResult().getAllErrors().forEach( ( error ) -> {
        String fieldName = "";
        String objectName = "";

        try {
            fieldName = (( FieldError ) error).getField();
            objectName = error.getObjectName();
        } catch (Exception e) {
            objectName = error.getObjectName();
            fieldName = "N/D";
        }



      String errorMessage = error.getDefaultMessage();

      errorDetails.setTimestamp(new Date());
      errorDetails.setMessage( "Validation Errors" );
      validationDetailDTOList.add( new ValidationDetailDTO( fieldName, objectName, errorMessage ) );
    } );
    errorDetails.setValidationDetailDTOList( validationDetailDTOList );
    errorDetails.setDetails(validationDetailDTOList.size() + " errors" );
    return new ResponseEntity<>( errorDetails, HttpStatus.BAD_REQUEST );
  }

}
