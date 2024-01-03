package com.cpiwx.nettyws.anaotations;

import com.cpiwx.nettyws.config.AutoConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chenPan
 * @date 2023-08-25 15:23
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(AutoConfig.class)
public @interface EnableLog {
}
