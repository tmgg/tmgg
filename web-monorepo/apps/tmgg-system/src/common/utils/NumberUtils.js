export default class NumberUtils {
  static tryGetNumberByStr(str) {
    if (str == null) {
      return null;
    }
    const result = str - 0;

    if (Number.isNaN(result)) {
      return null;
    }

    return result;
  }

  static isNumber(a) {
    if (a == null || a == '' || a == '-') {
      return false;
    }

    if (typeof a == 'number') {
      return true;
    }
    return false;
  }

  static displayPercent(v) {
    if (v.toFixed) {
      let toFixed = v.toFixed(2);
      toFixed = toFixed.replace('.00', '');

      return toFixed + '' + '%';
    }

    return v + '%';
  }

  static convertNumberToPercent = (v) => {
    v = this.tryGetNumberByStr(v);
    if (v != null) {
      v = v * 100;
      return NumberUtils.displayPercent(v);
    }
  };
}
