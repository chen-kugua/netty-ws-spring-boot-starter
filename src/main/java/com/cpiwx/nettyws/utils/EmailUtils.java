package com.cpiwx.nettyws.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Dict;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author chenPan
 * @date 2023-08-30 10:43
 **/
public class EmailUtils {

    public static void sendHtml(String title, List<String> receivers, String templatePath, Map<String, Object> params, File... files) {
        Assert.notEmpty(receivers, "邮件接收人不能为空");
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH));
        // email/birthdayRemind.ftl
        Template template = engine.getTemplate(templatePath);
        Dict dict = Dict.create();
        if (CollUtil.isNotEmpty(params)) {
            params.keySet().forEach((key) -> {
                dict.set(key, params.get(key));
            });
        }
        String content = template.render(dict);
        MailUtil.sendHtml(receivers, title, content, files);
    }
}
