package com.framework.cloud.gateway.domain.feign;

import com.framework.cloud.common.result.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author wusiwei
 */
@FeignClient(contextId = "OauthFeignService", value = "${client.oauth}", path = "/oauth", decode404 = true)
public interface OauthFeignService {

    @ApiOperation(value = "获取公钥")
    @PostMapping("/rsa/publicKey")
    Result<String> getPublicKey();

}
