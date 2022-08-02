package com.framework.cloud.gateway.domain.feign;

import com.framework.cloud.common.result.Result;
import com.framework.cloud.gateway.common.rpc.vo.GatewayRouteListVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author wusiwei
 */
@FeignClient(contextId = "PlatFormFeignService", value = "${client.platform}", path = "/gateway-route", decode404 = true)
public interface PlatformFeignService {

    @ApiOperation(value = "动态路由列表")
    @GetMapping("/list")
    Result<List<GatewayRouteListVO>> list();
}
