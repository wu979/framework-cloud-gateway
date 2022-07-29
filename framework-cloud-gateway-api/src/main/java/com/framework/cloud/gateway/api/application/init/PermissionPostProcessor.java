package com.framework.cloud.gateway.api.application.init;

import com.framework.cloud.core.event.ApplicationInitializingEvent;
import com.framework.cloud.gateway.common.constant.GatewayConstant;
import com.framework.cloud.gateway.domain.feign.UserFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wusiwei
 */
@Slf4j
@Component
@AutoConfigureOrder(GatewayConstant.LOAD_BALANCER_CLIENT_FILTER_ORDER + 101)
public class PermissionPostProcessor implements ApplicationListener<ApplicationInitializingEvent> {

    

    @Resource
    private UserFeignService userFeignService;

    @Override
    public void onApplicationEvent(ApplicationInitializingEvent event) {
        log.info("Initialized permission");

    }

}
