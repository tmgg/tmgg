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
  isSsoLoginEnable(){
   return globalEnableSsoLogin
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







  getServerUrl(): string {
    return process.env.API_BASE_URL || '/';
  }


  public readonly AUTH_TOKEN_NAME = 'token';

  getToken() {
    return localStorage.getItem(this.AUTH_TOKEN_NAME);
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
