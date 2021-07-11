package com.cn.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.cn.config.Logger;
import com.cn.config.LoggerManager;

/**
 * IP地址
 */
public class IpUtil {
    private static Logger logger = LoggerManager.getLogger(IpUtil.class);

    /**
     * Get host IP address
     *
     * @return IP Address
     */
    public static InetAddress getAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                // 去除回环接口，子接口，未运行和接口
                if (netInterface.isLoopback() || netInterface.isVirtual()|| !netInterface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = addresses.nextElement();
                    if (ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && !ip.getHostAddress().contains(":")){
                        return ip;
                    }
                }
            }
        } catch (SocketException e) {
            logger.severe("getAddress failed:{" + e.getMessage() + "}");
            e.printStackTrace();
        }
        return null;
    }
}