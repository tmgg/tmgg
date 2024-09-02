import {StorageUtil} from "../utils";
import {ReactNode} from "react";
import {KeyValueDataMap} from "../DataStructure";
import {http} from "@tmgg/tmgg-base"
/**
 * 配置有关的统一入口
 */
type SlotType = 'TopRightSettingMenuItem' | 'TopRightButton' | 'LOGIN_PAGE_FORM_BUTTON_NEXT'

export interface Events {
  afterLoadLoginData?: (loginData: any) => void;
}


let globalEnableSsoLogin = false


class Config {

  private hooks = new KeyValueDataMap()

  setSsoLoginEnable() {
    globalEnableSsoLogin = true
  }

  isSsoLoginEnable(){
   return globalEnableSsoLogin
  }

  addHook<K extends keyof Events>(key: K, callback: Events[K]): void {
    console.log('添加钩子 hook', key, callback)
    this.hooks.addItem(key, callback)
  }

  runHook(key: string, data: any) {
    const promiseList: Promise<any>[] = [];

    const fns = this.hooks.getValues(key)
    console.log(key + ' 回调个数', fns.length)

    for (let fn of fns) {
      promiseList.push(new Promise((resolve, reject) => {
        fn(data).then(resolve).catch(reject)
      }))
    }

    return Promise.all(promiseList)
  }


  setConfig(key: string, value: any) {
    localStorage.setItem(key, JSON.stringify(value))
  }

  getConfig(key: string) {
    const str = localStorage.getItem(key)
    if (!str) {
      return []
    }
    return JSON.parse(str)
  }

  getLoginInfo() {
    const info = localStorage.getItem('LOGIN_INFO');
    if (info != null && info.length > 0) {
      return JSON.parse(info);
    }
    return {}
  }

  loadLoginInfo() {
    localStorage.removeItem('LOGIN_INFO');
    return new Promise(resolve => {
      http.get('/getLoginInfo').then(rs => {
        const {data} = rs;
        localStorage.setItem('LOGIN_INFO', JSON.stringify(data));
        if (data.avatar != null) {
          localStorage.setItem('AVATAR', this.getServerUrl() + '/sysFile/preview/' + data.avatar)
        }
        if (this.initDataLoadedListeners.length > 0) {
          for (let listener of this.initDataLoadedListeners) {
            listener(data)
          }
        }
        resolve(true)
      })
    })


  }

  loadDict() {
    http.get('sysDict/tree').then(rs => {
      StorageUtil.set("DICT", rs.data)
    })
  }


  setPermissions(perms: string[]) {
    this.setConfig("permissions", perms)
  }

  getPermissions(): string[] {
    return this.getConfig("permissions")
  }


  getServerUrl(): string {
    return process.env.API_BASE_URL || '/';
  }


  public readonly AUTH_TOKEN_NAME = 'token';

  getToken() {
    return localStorage.getItem(this.AUTH_TOKEN_NAME);
  }

  getFullUrl(url: string, appendToken = false) {
    let rs = this.getServerUrl() + url;
    if (appendToken) {
      let join = url.indexOf('?') == -1 ? '?' : '&';
      rs += join + "token=" + this.getToken();
    }
    return rs
  }


  getHeader() {
    const headers = {};
    let token = this.getToken();
    if (token != null || token == '') {
      // @ts-ignore
      headers['Authorization'] = this.getToken();
    }
    return headers;
  }

  getLoginUrl() {
    return process.env.LOGIN_URL || "/login";
  }

  isPageNeedLogin(url: string) {
    // 也行还有其他url也不需登录呢
    return url != this.getLoginUrl() && url != '/sso';
  }

  /**
   * 系统插槽， 比如右上角加个什么啊
   *
   *
   */
  private slots: { type: SlotType; node: ReactNode }[] = [];

  addSlot = (type: SlotType, node: ReactNode) => {
    this.slots.push({type, node})
  }

  getSlot = (type: SlotType) => {
    let slots = this.slots

    for (const item of slots) {
      if (type == item.type) {
        return item.node
      }
    }
  }


  /**
   * 过去登录过后的初始化数据
   * @param callback
   */
  public loadLoginData() {
    return new Promise((resolve, reject) => {
      console.log("正在加载登录信息")
      this.loadLoginInfo().then(r => {
        console.log("SysConfig: 加载登录信息成功， 准备执行hook")
        this.runHook('afterLoadLoginData', r).then(() => {
          console.log("SysConfig: 执行hook完成")
          resolve(r)
        })
      }).catch(reject)

      this.loadDict();
    })
  }




  public appendTokenToUrl(url: string): string {
    return url + (url.indexOf('?') == -1 ? '?' : '&') + 'token=' + this.getToken();
  }


  private initDataLoadedListeners: any[] = [];



}

let instance = new Config();
/**
 * @deprecated, 使用简写 sys
 */
export const SysConfig = instance;
export const sys = instance;
