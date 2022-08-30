package com.framework.cloud.gateway.domain.utils;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.cloud.client.loadbalancer.Response;

import java.net.URI;

/**
 * @author wusiwei
 */
public class LoadBalancerUtil {

    private LoadBalancerUtil() {
    }

    public static URI reconstructUri(ServiceInstance serviceInstance, URI original) {
        return LoadBalancerUriTools.reconstructURI(serviceInstance, original);
    }

    public static Response<ServiceInstance> empty(String serviceId) {
        return new EmptyResponse();
    }

}
