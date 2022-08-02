package com.framework.cloud.gateway.api.application.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.cloud.endpoint.event.RefreshEventListener;
import org.springframework.context.ApplicationEvent;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 *
 * @author wusiwei
 */
@Slf4j
public class GatewayRefreshEventListener extends RefreshEventListener {

    private final static String KEY = "framework.gateway.ignored-url";
    private final ContextRefresher refresh;
    private AtomicBoolean ready = new AtomicBoolean(false);

    public GatewayRefreshEventListener(ContextRefresher refresh) {
        super(refresh);
        this.refresh = refresh;
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationReadyEvent.class.isAssignableFrom(eventType)
                || RefreshEvent.class.isAssignableFrom(eventType);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            this.handle((ApplicationReadyEvent) event);
        }
        else if (event instanceof RefreshEvent) {
            this.handle((RefreshEvent) event);
        }
    }

    @Override
    public void handle(ApplicationReadyEvent event) {
        this.ready.compareAndSet(false, true);
    }

    @Override
    public void handle(RefreshEvent event) {
        if (this.ready.get()) {
            log.debug("Event received " + event.getEventDesc());
            Set<String> keys = this.refresh.refresh();
            log.info("Refresh keys changed: " + keys);
        }
    }
}
