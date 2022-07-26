package com.framework.cloud.gateway.infrastructure.handler;

import cn.hutool.core.lang.Assert;
import com.framework.cloud.common.result.R;
import com.framework.cloud.common.result.Result;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * gateway global exception
 *
 * @author wusiwei
 */
@SuppressWarnings({"all"})
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private List<HttpMessageReader<?>> messageReaders = Collections.emptyList();

    private List<HttpMessageWriter<?>> messageWriters = Collections.emptyList();

    private List<ViewResolver> viewResolvers = Collections.emptyList();

    public void setMessageReaders(List<HttpMessageReader<?>> messageReaders) {
        Assert.notNull(messageReaders, "'messageReaders' must not be null");
        this.messageReaders = messageReaders;
    }

    public void setViewResolvers(List<ViewResolver> viewResolvers) {
        this.viewResolvers = viewResolvers;
    }

    public void setMessageWriters(List<HttpMessageWriter<?>> messageWriters) {
        Assert.notNull(messageWriters, "'messageWriters' must not be null");
        this.messageWriters = messageWriters;
    }

    @NotNull
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }
        ServerRequest newRequest = ServerRequest.create(exchange, this.messageReaders);
        return RouterFunctions
                .route(RequestPredicates.all(), request -> renderErrorResponse(request, ex))
                .route(newRequest)
                .switchIfEmpty(Mono.error(ex))
                .flatMap((handler) -> handler.handle(newRequest))
                .flatMap((response) -> write(exchange, response));

    }

    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request, Throwable ex) {
        Result<Void> result = result(ex);
        return ServerResponse.status(result.getCode()).contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(result));
    }

    private Mono<? extends Void> write(ServerWebExchange exchange, ServerResponse response) {
        exchange.getResponse().getHeaders().setContentType(response.headers().getContentType());
        return response.writeTo(exchange, new ResponseContext());
    }

    private class ResponseContext implements ServerResponse.Context {

        @Override
        public List<HttpMessageWriter<?>> messageWriters() {
            return GlobalExceptionHandler.this.messageWriters;
        }

        @Override
        public List<ViewResolver> viewResolvers() {
            return GlobalExceptionHandler.this.viewResolvers;
        }

    }

    private Result<Void> result(Throwable ex) {
        return R.error(ex.getMessage());
    }

}
