package com.framework.cloud.gateway.domain.loadBalancer;

import cn.hutool.core.util.ObjectUtil;
import com.framework.cloud.common.constant.NacosConstant;
import com.framework.cloud.common.utils.WeightUtil;
import com.framework.cloud.common.weight.WeightMeta;
import com.framework.cloud.gateway.domain.properties.GatewayProperties;
import com.framework.cloud.gateway.domain.utils.LoadBalancerUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The accessed microservices are determined according to the version number and weight
 *
 * @author wusiwei
 */
@Slf4j
@AllArgsConstructor
public class VersionLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private final String serviceId;
    private final GatewayProperties gatewayProperties;
    private final ObjectProvider<ServiceInstanceListSupplier> provider;

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        if (this.provider != null) {
            ServiceInstanceListSupplier supplier = provider.getIfAvailable(NoopServiceInstanceListSupplier::new);
            Flux<List<ServiceInstance>> listFlux = supplier.get();
            return listFlux.next().map(this::getServiceInstance);
        }
        return null;
    }

    private Response<ServiceInstance> getServiceInstance(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            return LoadBalancerUtil.empty(serviceId);
        }
        String version = gatewayProperties.getVersion();
        Map<String, String> versionMap = new HashMap<>(1);
        versionMap.put(NacosConstant.VERSION, version);
        Set<Map.Entry<String, String>> attributes = Collections.unmodifiableSet(versionMap.entrySet());
        Map<ServiceInstance, Integer> instancesList = instances.stream()
                .filter(instance -> instance.getMetadata().entrySet().containsAll(attributes))
                .filter(instance -> instance.getMetadata().containsKey(NacosConstant.WEIGHT))
                .collect(Collectors.toMap(Function.identity(), entry -> Integer.parseInt(entry.getMetadata().get(NacosConstant.WEIGHT))));
        WeightMeta<ServiceInstance> weightMeta = WeightUtil.randomWeightMeta(instancesList);
        if (ObjectUtil.isNull(weightMeta)) {
            return LoadBalancerUtil.empty(serviceId);
        }
        ServiceInstance serviceInstance = weightMeta.random();
        if (ObjectUtil.isNull(serviceInstance)) {
            return LoadBalancerUtil.empty(serviceId);
        }
        return new DefaultResponse(serviceInstance);
    }

}
