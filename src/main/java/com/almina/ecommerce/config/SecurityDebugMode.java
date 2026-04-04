package com.almina.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("securityDebugMode")
public class SecurityDebugMode {

    @Value("${app.security.debug-permit-all:false}")
    private boolean debugPermitAll;

    public boolean isDebugPermitAll() {
        return debugPermitAll;
    }
}
