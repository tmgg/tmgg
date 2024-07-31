package io.tmgg.sys.auth.captcha.core;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class CaptchaVo {

    private String bgUrl;

    private String puzzleUrl;

}
