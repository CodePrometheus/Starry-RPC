package com.star.framework.compress.impl;

import com.star.framework.compress.Compress;
import org.xerial.snappy.Snappy;

import java.io.IOException;

/**
 * Snappy（以前称Zippy）是Google基于LZ77的思路用C++语言编写的快速数据压缩与解压程序库，
 * 并在2011年开源。它的目标并非最大压缩率或与其他压缩程序库的兼容性，而是非常高的速度和合理的压缩率
 *
 * @Author: zzStar
 * @Date: 05-27-2021 14:57
 */
public class SnappyCompress implements Compress {

    @Override
    public byte[] compress(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes is null");
        }

        try {
            return Snappy.compress(bytes);
        } catch (IOException e) {
            throw new RuntimeException("snappy compress error", e);
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        try {
            return Snappy.uncompress(bytes);
        } catch (IOException e) {
            throw new RuntimeException("snappy decompress error", e);
        }
    }

    @Override
    public int getCode() {
        return SNAPPY_COMPRESS;
    }
}
