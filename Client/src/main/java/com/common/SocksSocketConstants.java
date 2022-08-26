package com.common;

interface SocksSocketConstants {
    /**
     * The SOCKS service's conventional TCP port
     */
    int SOCKS_PORT = 1080;

    /**
     * SOCKS Protocol Version
     * <p>
     * SOCKS 4A is a simple extension to SOCKS 4 protocol.
     * SOCKS 4A allow the use of SOCKS 4A on hosts
     * which are not capable of resolving all domain names.
     */
    int SOCKS_VERSION_4A = 4;
    int SOCKS_VERSION_5 = 5;

    /**
     * SOCKS request commands
     */
    int COMMAND_CONNECT = 1;
    int COMMAND_BIND = 2;
    int COMMAND_UDP_ASSOCIATE = 3;

    /**
     * SOCKS5 Identifier/method selection message values
     */
    int NO_AUTHENTICATION_REQUIRED = 0;
    int GSSAPI = 1;
    int USERNAME_AND_PASSWORD = 2;
    int CHAP = 3;
    int NO_ACCEPTABLE_METHODS = 0xFF;

    /**
     * IP address type of following address
     */
    int IP_V4 = 1;
    int DOMAINNAME = 3;
    int IP_V6 = 4;

    /**
     * NULL is a byte of all zero bits.
     */
    int NULL = 0;

    /**
     * SOCKS4 reply code
     */
    int REQUEST_GRANTED = 90;
    int REQUEST_REJECTED = 91;
    int REQUEST_REJECTED_NO_IDENTD = 92;
    int REQUEST_REJECTED_DIFF_IDENTS = 93;

    /**
     * Socks5 reply code
     */
    int SUCCEEDED = 0;
    int FAILURE = 1;
    int NOT_ALLOWED = 2;
    int NETWORK_UNREACHABLE = 3;
    int HOST_UNREACHABLE = 4;
    int REFUSED = 5;
    int TTL_EXPIRED = 6;
    int COMMAND_NOT_SUPPORTED = 7;
    int ADDRESS_TYPE_NOT_SUPPORTED = 8;
    int INVALID_ADDRESS = 9;

    /**
     *
     */
    int INTERFACE_REQUEST = 1;
    int USECLIENTSPORT = 4;
}
