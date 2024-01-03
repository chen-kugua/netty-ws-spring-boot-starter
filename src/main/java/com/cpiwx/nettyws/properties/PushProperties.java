package com.cpiwx.nettyws.properties;

import com.cpiwx.nettyws.model.dto.FeiShuDTO;
import com.cpiwx.nettyws.model.dto.MailDTO;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author chenPan
 * @date 2023-08-25 17:58
 **/
@Data
@ConfigurationProperties(prefix = "notice.push")
public class PushProperties {

    private FeiShuDTO feiShu;

    private MailDTO mail;

}
