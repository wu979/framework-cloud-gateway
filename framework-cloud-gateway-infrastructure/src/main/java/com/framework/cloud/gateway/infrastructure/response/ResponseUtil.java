package com.framework.cloud.gateway.infrastructure.response;

import com.framework.cloud.common.enums.GlobalMessage;
import com.framework.cloud.common.result.R;
import com.framework.cloud.common.result.Result;
import com.framework.cloud.common.utils.FastJsonUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * @author wusiwei
 */
public interface ResponseUtil {

    default Mono<Void> error(ServerWebExchange exchange, Result<Void> result) {
        return responseWriter(exchange, result);
    }

    default Mono<Void> error(ServerWebExchange exchange, String msg) {
        return responseWriter(exchange, R.error(GlobalMessage.FAIL.getCode(), msg));
    }

    default Mono<Void> error(ServerWebExchange exchange, int code, String msg) {
        return responseWriter(exchange, R.error(code, msg));
    }

    default Mono<Void> responseWriter(ServerWebExchange exchange, Result<Void> result) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setAccessControlAllowCredentials(true);
        response.getHeaders().setAccessControlAllowOrigin("*");
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        DataBuffer buffer = dataBufferFactory.wrap(FastJsonUtil.toJSONString(result).getBytes(Charset.defaultCharset()));
        return response.writeWith(Mono.just(buffer)).doOnError((error) -> {
            DataBufferUtils.release(buffer);
        });
    }
}
