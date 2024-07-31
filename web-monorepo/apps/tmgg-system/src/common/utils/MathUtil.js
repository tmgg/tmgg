export default class MathUtil {
  /**
   * 计算完成率
   * @param a
   * @param b
   * @returns {string}
   */
  static calcRatePercent(a, b) {
    if (a != null && b != null) {
      a = parseFloat(a);
      b = parseFloat(b);
      return ((a / b) * 100).toFixed(2);
    }
  }

  static calcAdd(a, b) {
    if (a != null && b != null) {
      a = Number.isInteger(a) ? parseInt(a) : parseFloat(a);
      b = Number.isInteger(b) ? parseInt(b) : parseFloat(b);
      let c = a + b;

      if (Number.isInteger(c)) {
        return c;
      }
      return c.toFixed(2);
    }
  }

  static calcMinus(a, b) {
    if (a != null && b != null) {
      a = parseFloat(a);
      b = parseFloat(b);
      return (a - b).toFixed(2);
    }
  }

  /**
   * 环比
   * @param a 当月
   * @param b 上月
   */
  static calcMomPercent(a, b) {
    if (a != null && b != null) {
      a = parseFloat(a);
      b = parseFloat(b);
      if (b == 0) {
        return 0;
      }
      return (((a - b) / b) * 100).toFixed(2);
    }
  }

  /**
   * 转换数字， 小数保留小数2位
   * @param n
   */
  static convertNumber(n) {}
}
