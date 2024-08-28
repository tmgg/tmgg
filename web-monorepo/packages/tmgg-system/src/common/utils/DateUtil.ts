import moment from 'moment';

export class DateUtil {
  static getYear() {
    const d = new Date();
    return d.getFullYear();
  }

  static getQuarter() {
    let month = this.getMonth();

    return Math.ceil(month / 3);
  }

  /**
   *  从 1开始
   */
  static getMonth() {
    const d = new Date();
    return d.getMonth() + 1;
  }

  static today() {
    return moment().format('YYYY-MM-DD');
  }

  static beginOfMonth() {
    const m = moment().startOf('month');
    return m.format('YYYY-MM-DD');
  }

  static beginOfYear() {
    const m = moment().startOf('year');
    return m.format('YYYY-MM-DD');
  }
}
