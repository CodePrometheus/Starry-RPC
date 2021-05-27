package com.star.framework.codec;

import com.star.common.domain.StarryRequest;
import com.star.common.enums.PackageType;
import com.star.framework.compress.Compress;
import com.star.framework.serialization.Serialization;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 传输
 *
 * @Author: zzStar
 * @Date: 05-27-2021 14:49
 */
public class ObjectWriter {

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    public static void writeObject(OutputStream outputStream, Object object, Serialization serializer, Compress compress) throws IOException {
        outputStream.write(intToBytes(MAGIC_NUMBER));
        if (object instanceof StarryRequest) {
            outputStream.write(intToBytes(PackageType.REQUEST_PACK.getCode()));
        } else {
            outputStream.write(intToBytes(PackageType.RESPONSE_PACK.getCode()));
        }
        outputStream.write(intToBytes(serializer.getCode()));
        outputStream.write(intToBytes(compress.getCode()));
        byte[] bytes = serializer.serialize(object);
        bytes = compress.compress(bytes);
        outputStream.write(intToBytes(bytes.length));
        outputStream.write(bytes);
        outputStream.flush();

    }

    private static byte[] intToBytes(int value) {
        byte[] des = new byte[4];
        des[3] = (byte) ((value >> 24) & 0xFF);
        des[2] = (byte) ((value >> 16) & 0xFF);
        des[1] = (byte) ((value >> 8) & 0xFF);
        des[0] = (byte) (value & 0xFF);
        return des;
    }

}
