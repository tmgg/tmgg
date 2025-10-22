export const MathUtil = {


    /**
     * 保留2位小数
     * @param value
     */
    round2(value) {
        const factor = 100;
        return Math.round(value * factor) / factor;
    },
    roundTo(value, decimals) {
        const factor = Math.pow(10, decimals);
        return Math.round(value * factor) / factor;
    }
}
