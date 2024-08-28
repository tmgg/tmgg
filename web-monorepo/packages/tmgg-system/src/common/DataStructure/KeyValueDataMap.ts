export class KeyValueDataMap {
  private data: Map<string, any[]>;

  constructor() {
    this.data = new Map<string, any[]>(); // 使用 Map 对象存储键值对
  }

  public addItem(key: string, value: any): void {
    if (this.data.has(key)) {
      this.data.get(key)!.push(value);
    } else {
      this.data.set(key, [value]);
    }
  }

  public getValues(key: string): any[] {
    if (this.data.has(key)) {
      return this.data.get(key)!;
    } else {
      return [];
    }
  }

  public removeItem(key: string, value: any): void {
    if (this.data.has(key)) {
      const values = this.data.get(key)!;
      const index = values.indexOf(value);
      if (index !== -1) {
        values.splice(index, 1);
        if (values.length === 0) {
          this.data.delete(key);
        }
      }
    }
  }
}


