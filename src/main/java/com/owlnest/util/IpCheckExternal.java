package com.owlnest.util;

import java.net.InetAddress;

/**
 * Local-dev stub for missing proprietary owlnest library.
 */
public class IpCheckExternal {
    public String getIp() throws Exception {
        return InetAddress.getLocalHost().getHostAddress();
    }
}
