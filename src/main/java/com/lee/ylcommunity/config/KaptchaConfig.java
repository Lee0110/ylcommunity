package com.lee.ylcommunity.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Bean
    public Producer kaptchaProducer() {
        DefaultKaptcha kaptcha = new DefaultKaptcha();

        // 验证码生成设置
        Properties properties = new Properties();
        properties.put("kaptcha.image.width", "100");
        properties.put("kaptcha.image.height", "40");
        properties.put("kaptcha.textproducer.font.size", "32");
        properties.put("kaptcha.textproducer.font.color", "0,0,0");
        properties.put("kaptcha.textproducer.char.string", "0123456789qwertyuiopasdfghjklzxcvbnm");
        properties.put("kaptcha.textproducer.char.length", "4");
        properties.put("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");

        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
