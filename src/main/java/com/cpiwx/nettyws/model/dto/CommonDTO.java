package com.cpiwx.nettyws.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenPan
 * @date 2023-12-19 11:18
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonDTO<T> {
    private String name;

    private T value;

}
