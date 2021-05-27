package com.star.framework.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: zzStar
 * @Date: 05-27-2021 14:38
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StarryRpcProperties {

    private String version;
    private String group;
    private String serviceName;

    @Override
    public String toString() {
        return serviceName + "_" + group + "_" + version;
    }

}
