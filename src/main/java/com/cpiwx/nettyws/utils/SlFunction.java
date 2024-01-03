package com.cpiwx.nettyws.utils;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author chenPan
 * @date 2023-08-31 09:24
 **/
@FunctionalInterface
public interface SlFunction<T, R> extends Function<T, R>, Serializable {


}
