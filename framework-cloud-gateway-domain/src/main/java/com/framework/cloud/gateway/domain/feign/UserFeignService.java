package com.framework.cloud.gateway.domain.feign;

import com.framework.cloud.common.result.Result;
import com.framework.cloud.gateway.common.rpc.vo.PermissionRoleListVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author wusiwei
 */
@FeignClient(contextId = "UserFeignService", value = "${client.user}", path = "/permission", decode404 = true)
public interface UserFeignService {

    @ApiOperation(value = "权限列表")
    @GetMapping(value = "/list")
    Result<List<PermissionRoleListVO>> list();
}
