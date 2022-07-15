package com.framework.cloud.gateway.api.application.configuration;

import com.framework.cloud.core.ApplicationConfiguration;
import com.framework.cloud.core.HttpMessageConfiguration;
import com.framework.cloud.core.ObjectMapperConfiguration;
import org.springframework.context.annotation.Import;

/**
 *
 *
 * @author wusiwei
 */
@Import({ApplicationConfiguration.class, ObjectMapperConfiguration.class, HttpMessageConfiguration.class})
public class GatewayAutoConfiguration {


}
