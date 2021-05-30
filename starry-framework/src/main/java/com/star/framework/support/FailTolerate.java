package com.star.framework.support;

import com.star.common.domain.StarryRequest;
import com.star.common.extension.SPI;

/**
 * 容错机制
 *
 * @Author: zzStar
 * @Date: 05-30-2021 11:45
 */
@SPI
public interface FailTolerate {

    /**
     * call
     *
     * @param request
     * @return
     */
    Object invoke(StarryRequest request);

}
