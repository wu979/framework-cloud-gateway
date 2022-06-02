package com.framework.cloud.gateway;

import com.framework.cloud.common.utils.GsonUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        String rt = "{ \"corpId\": \"150000000\" }";
        T1 t1 = GsonUtil.toJavaBean(rt, T1.class);
        SpringApplication.run(GatewayApplication.class, args);
    }

}
