package com.nhx.core.transport;

public interface Transporter {
    /**
     * Returns the transport protocol
     */
    Protocol protocol();

    /**
     * 传输层协议.
     */
    enum Protocol {
        TCP,
        DOMAIN  // Unix domain socket
    }
}
