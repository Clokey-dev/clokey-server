package org.clokey.outer.api.config;

import feign.codec.Encoder;
import org.springframework.context.annotation.Bean;

public class KakaoAuthConfig {

    @Bean
    Encoder formEncoder() {
        return new feign.form.FormEncoder();
    }
}
