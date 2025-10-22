export const MathUtil = {

    /**
     * 对金额的四舍五入
     */
    roundAmt(value) {
        const factor = 100;
        return Math.round(value * factor) / factor;
    },
    roundTo(value, decimals) {
        const factor = Math.pow(10, decimals);
        return Math.round(value * factor) / factor;
    }
}
