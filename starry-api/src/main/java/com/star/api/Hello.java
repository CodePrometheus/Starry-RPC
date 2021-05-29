package com.star.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: zzStar
 * @Date: 05-28-2021 15:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hello implements Serializable {

    private Integer id;

    private String message;

}
