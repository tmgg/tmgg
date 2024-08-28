export class StorageUtil {
  static set(k, v) {
    localStorage.setItem(k, JSON.stringify(v));
  }

  static get(k) {
    const info = localStorage.getItem(k);
    if (info != null && info.length > 0) {
      return JSON.parse(info);
    }
  }
}
