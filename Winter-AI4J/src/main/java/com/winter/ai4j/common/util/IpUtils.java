package com.winter.ai4j.common.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.winter.ai4j.common.model.IpLocation;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * ClassName: IpUtils
 * <p>
 *
 * </p >
 *
 * @author wyh
 * Date: 2024/6/3 下午3:36
 */
@Slf4j
public class IpUtils {


    private static final String ZERO="0";

    private static final String LOCALHOST="127.0.0.1";


    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (LOCALHOST.equals(ipAddress)) {
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    public static IpLocation getLocation(String ip) {
        IpLocation location = new IpLocation();
        location.setIp(ip);
        try (InputStream inputStream = IpUtils.class.getResourceAsStream("/ip2region.xdb");) {
            byte[] bytes = IoUtil.readBytes(inputStream);
            Searcher searcher = Searcher.newWithBuffer(bytes);
            String region = searcher.search(ip);
            if (StrUtil.isAllNotBlank(region)) {
                String[] result = region.split("\\|");
                location.setCountry(ZERO.equals(result[0])?StrUtil.EMPTY:result[0]);
                location.setProvince(ZERO.equals(result[2])?StrUtil.EMPTY:result[2]);
                location.setCity(ZERO.equals(result[3])?StrUtil.EMPTY:result[3]);
                location.setIsp(ZERO.equals(result[4])?StrUtil.EMPTY:result[4]);
            }
            searcher.close();
        } catch (Exception e) {
            log.error("IP address resolution exception,error:{}",e);
            return location;
        }
        return location;
    }
}
