package io.tmgg.modules.sys.vo;

import io.tmgg.lang.obj.AjaxResult;
import lombok.Data;

@Data
public class UploadResult extends AjaxResult {


    // 兼容 tiny mce
    String location;

    String   id;
}
