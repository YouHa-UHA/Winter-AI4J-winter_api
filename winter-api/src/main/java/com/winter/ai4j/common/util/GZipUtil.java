package com.winter.ai4j.common.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * ClassName: GZipUtil
 * <blockquote><pre>
 * Description: GZIP工具类，用于压缩和解压缩字符串
 * </pre></blockquote>
 *
 * @author
 * @version 1.0.0
 * @since 1.0.0
 * Date: 2024/6/26 下午5:26
 */

public class GZipUtil {


    /**
     * 压缩字符串
     */
    public static byte[] compressString(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(str.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }


    /**
     * 解压缩字符串
     */
    public static String decompressString(byte[] compressed) {
        if (ArrayUtils.isEmpty(compressed)) {
            return "";
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPInputStream gunzip = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = gunzip.read(buffer)) > 0) {
                out.write(buffer, 0, n);
            }
            return out.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
