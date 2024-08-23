package io.tmgg.sys.auth.captcha.core;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class VerifyParam {


    @NotNull
    private String clientId;


    int x; // 拼图 x 轴移动值（拼图和滑块按钮可移动距离不一样，这里的移动距离是计算后的拼图移动距离。）
    int y; // y 轴移动值（按下鼠标到释放鼠标 y 轴的差值）
    int sliderOffsetX; // 滑块 x 轴偏移值（暂时没有什么场景会用到）
    int duration; // 操作持续时长
    int[][] trail; // 移动轨迹
    int errorCount; // 连续错误次数
}
