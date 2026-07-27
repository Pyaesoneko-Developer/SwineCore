package com.example.swinecore.util;

import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Resolves a QR URL that phones on the same LAN can open. */
@Component
public class NetworkBaseUrlResolver {

    @Value("${app.payment-base-url:}")
    private String configuredBaseUrl;

    @Value("${server.port:8080}")
    private int serverPort;

    public String resolve() {
        if (configuredBaseUrl != null && !configuredBaseUrl.isBlank()) {
            return stripTrailingSlash(configuredBaseUrl.trim());
        }

        try {
            for (NetworkInterface network : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!network.isUp() || network.isLoopback() || network.isVirtual()) continue;
                for (var address : Collections.list(network.getInetAddresses())) {
                    if (address instanceof Inet4Address && address.isSiteLocalAddress()) {
                        return "http://" + address.getHostAddress() + ":" + serverPort;
                    }
                }
            }
        } catch (Exception ignored) {
            // Localhost keeps development usable when no LAN adapter is available.
        }
        return "http://localhost:" + serverPort;
    }

    private String stripTrailingSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
