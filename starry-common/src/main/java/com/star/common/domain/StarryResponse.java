package com.star.common.domain;

import com.star.common.enums.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 生产者执行完成或出错后向消费者返回的结果对象
 *
 * @Author: zzStar
 * @Date: 05-27-2021 14:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StarryResponse<T> implements Serializable {

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应对应的请求号
     */
    private String requestId;

    /**
     * 响应状态码
     */
    private Integer statusCode;

    /**
     * 响应状态补充信息
     */
    private String message;

    public static <T> StarryResponse<T> success(T data, String requestId) {
        StarryResponse<T> response = new StarryResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    public static <T> StarryResponse<T> fail(ResponseCode code, String requestId) {
        StarryResponse<T> response = new StarryResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }

}
