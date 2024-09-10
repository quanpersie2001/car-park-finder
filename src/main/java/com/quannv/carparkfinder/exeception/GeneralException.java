package com.quannv.carparkfinder.exeception;

import com.quannv.carparkfinder.dto.GeneralResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
public class GeneralException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 7452389194316874203L;

	protected ErrorCode errorCode;

	protected HttpStatus httpStatus;

	private Map<String, String> errorFields;

	@Override
	public String getMessage() {
		return errorCode.name();
	}

	public GeneralResponse<Object> getResponse() {
		return new GeneralResponse<>(false, errorCode, null);
	}

}