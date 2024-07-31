export class EventBus {
  private readonly __stack: any = {};

  on(name: string, callback: Function, ctx: any) {
    (this.__stack[name] || (this.__stack[name] = [])).push({
      fn: callback,
      ctx,
    });

    return this; // chainable
  }

  once(name: string, callback: Function, ctx: any) {
    const listener = () => {
      this.off(name, listener);
      callback.apply(ctx, arguments);
    };

    listener.__callback = callback;
    return this.on(name, listener, ctx); // chainable
  }

  emit(name: string, params: any) {
    const list = this.__stack[name];

    if (list !== void 0) {
      const params = [].slice.call(arguments, 1);
      list.forEach((entry: any) => {
        entry.fn.apply(entry.ctx, params);
      });
    }

    return this; // chainable
  }

  off(name: string, callback: Function) {
    const list: any = this.__stack[name];

    const liveEvents: any[] = [];

    if (list !== void 0 && callback) {
      list.forEach((entry: any) => {
        if (entry.fn !== callback && entry.fn.__callback !== callback) {
          liveEvents.push(entry);
        }
      });

      if (liveEvents.length !== 0) {
        this.__stack[name] = liveEvents;
      } else {
        delete this.__stack[name];
      }
    }

    return this; // chainable
  }
}
const eventBus = new EventBus();
export { eventBus };
