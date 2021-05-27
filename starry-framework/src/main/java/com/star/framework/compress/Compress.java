package com.star.framework.compress;

import com.star.framework.compress.impl.GzipCompress;
import com.star.framework.compress.impl.SnappyCompress;

/**
 * RPC通讯数据的压缩
 * 减轻网络带宽压力，但是这同时也会加重 CPU 的负担，因为压缩算法是 CPU 计算密集型操作，会导致操作系统的负载加重。
 * 所以，最终是否进行消息压缩，一定要根据业务情况加以权衡
 *
 * @Author: zzStar
 * @Date: 05-27-2021 14:52
 */
public interface Compress {

    Integer GZIP_COMPRESS = 0;
    Integer SNAPPY_COMPRESS = 1;

    /**
     * 默认gzip
     */
    Integer DEFAULT_COMPRESS = GZIP_COMPRESS;

    /**
     * switch Compress
     *
     * @param code
     * @return
     */
    static Compress getByCode(int code) {
        switch (code) {
            case 0:
                return new GzipCompress();
            case 1:
                return new SnappyCompress();
            default:
                return null;
        }
    }

    /**
     * 压缩
     *
     * @param bytes
     * @return
     */
    byte[] compress(byte[] bytes);

    /**
     * 解压缩
     *
     * @param bytes
     * @return
     */
    byte[] decompress(byte[] bytes);

    /**
     * code of compress
     *
     * @return
     */
    int getCode();
}
