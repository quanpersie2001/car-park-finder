package com.quannv.carparkfinder.exeception;

import com.quannv.carparkfinder.dto.GeneralResponse;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.Objects;

import static com.quannv.carparkfinder.utils.Constants.ORGANIZATION;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ResponseStatus(INTERNAL_SERVER_ERROR)
	@ExceptionHandler({GeneralException.class, Exception.class, Throwable.class})
	public ResponseEntity<GeneralResponse<Object>> exceptionGeneral(Exception ex, WebRequest request) {
		return customHandleExceptionInternal(ex,
		                                     new GeneralResponse<>(false,
		                                                           ErrorCode.INTERNAL_SERVER_ERROR,
		                                                           null),
		                                     new HttpHeaders(),
		                                     INTERNAL_SERVER_ERROR,
		                                     request);
	}

	@ExceptionHandler(ConversionFailedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<GeneralResponse<Object>> handleConversion(RuntimeException ex, WebRequest request) {
		return customHandleExceptionInternal(ex,
		                                     new GeneralResponse<>(false,
		                                                           ErrorCode.BAD_REQUEST,
		                                                           null),
		                                     new HttpHeaders(),
		                                     INTERNAL_SERVER_ERROR,
		                                     request);
	}

	private ResponseEntity<GeneralResponse<Object>> customHandleExceptionInternal(Exception ex,
	                                                                              @Nullable GeneralResponse<Object> body,
	                                                                              HttpHeaders headers,
	                                                                              HttpStatus status,
	                                                                              WebRequest request) {

		StackTraceElement stackTraceElement = Arrays.stream(ex.getStackTrace())
		                                            .filter(error -> error.toString().contains(ORGANIZATION))
		                                            .findFirst()
		                                            .orElse(ex.getStackTrace()[0]);

		log.error("Exception occurred in {}.{} at line {}. Error message: {}",
		          stackTraceElement.getClassName(), stackTraceElement.getMethodName(),
		          stackTraceElement.getLineNumber(), ex.getMessage(), ex);

		// return handleExceptionInternal(ex, body, headers, status, request);

		if (request instanceof ServletWebRequest servletWebRequest) {
			HttpServletResponse response = servletWebRequest.getResponse();
			if (Objects.nonNull(response) && response.isCommitted()) {
				log.warn("Response already committed. Ignoring: " + ex);
				return null;
			}
		}

		if (Objects.nonNull(body) && Objects.isNull(body.getResponse()) && ex instanceof ErrorResponse errorResponse) {
			body.setResponse(errorResponse.updateAndGetBody(this.getMessageSource(), LocaleContextHolder.getLocale()));
		}

		if (status.equals(HttpStatus.INTERNAL_SERVER_ERROR) && body == null) {
			request.setAttribute("jakarta.servlet.error.exception", ex, 0);
		}

		return new ResponseEntity<>(body, headers, status);
	}
}
