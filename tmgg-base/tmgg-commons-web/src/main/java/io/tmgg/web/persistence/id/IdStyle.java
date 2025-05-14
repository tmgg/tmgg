package io.tmgg.web.persistence.id;

public enum IdStyle {

    UUID,

    /**
     * 例如：USER2025040500383074634b0215f9a44
     */
    DATETIME_UUID,

    /**
     * 例如：
     * USER2025040508583662400000000001，
     * USER2025040508583670900000000002
     */
    DATETIME_SEQ,

    /**
     * 每日的序号会重置, 数据库中（否则应用重启会id重复）。
     *
     * 例如：
     * USR2025040600001
     * USR2025040600002
     */
    DAILY_SEQ,


}
