package com.quannv.carparkfinder.dto;

import com.quannv.carparkfinder.exeception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneralResponse<T> {
	private boolean acknowledge;

	private ErrorCode errorCode;

	private T response;
}
