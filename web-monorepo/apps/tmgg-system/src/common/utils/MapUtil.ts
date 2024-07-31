export class MapUtil {
  static initArrayValue(map:any, key:string) {
    if (map[key] == null) {
      map[key] = [];
    }
  }
}
