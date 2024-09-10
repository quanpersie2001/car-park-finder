package com.quannv.carparkfinder.configuration;

import com.uber.h3core.H3Core;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.SocketTimeoutException;

@Configuration
public class CommonConfiguration {
	@Bean
	public H3Core h3Core() throws IOException {
		return H3Core.newInstance();
	}

	@Bean
	public GeometryFactory geometryFactory() {
		return new GeometryFactory();
	}

	@Bean
	public CloseableHttpClient closeableHttpClient() {
		HttpRequestRetryHandler retryHandler = (exception, executionCount, httpContext) -> {
			if (executionCount >= 3) {  // Maximum retries
				return false;
			}
			if (exception instanceof org.apache.http.NoHttpResponseException) {
				// Retry if the server dropped connection on us
				return true;
			}

			// Retry on socket timeout
			if (exception instanceof SocketTimeoutException) {
				return true;
			}

			return false; // Otherwise, do not retry;
		};
		return HttpClients.custom().setRetryHandler(retryHandler).build();
	}
}
