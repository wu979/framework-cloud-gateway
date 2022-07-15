package com.framework.cloud.gateway.domain.feign;

import com.framework.cloud.common.result.Result;
import com.framework.cloud.gateway.common.rpc.vo.GatewayRouteListVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author wusiwei
 */
@FeignClient(contextId = "PlatFormFeignService", value = "framework-cloud-platform-api", path = "/gateway-route", decode404 = true)
public interface PlatformFeignService {

    @GetMapping("/list")
    Result<List<GatewayRouteListVO>> list();
}
