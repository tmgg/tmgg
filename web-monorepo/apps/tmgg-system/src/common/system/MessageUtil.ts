import { Modal, notification } from 'antd';
import React from 'react';

/**
 * 弃用，请使用UIUtil
 */


export class MessageUtil {
  static DEFAULT_ERROR_MSG = '未知错误';

  static alert(msg: string) {
    Modal.info({
      title: '提示',
      content: msg,
    });
  }

  static confirm = (msg: string) => {
    return new Promise((resolve) => {
      Modal.confirm({
        title: '确认',
        content: msg,
        okText: '确定',
        cancelText: '取消',
        onOk() {
          resolve(msg);
        },
      });
    });
  };

  static alertError(msg: string) {
    Modal.error({
      title: '提示',
      content: msg,
    });
  }

  static alertSuccess(msg: string) {
    Modal.success({
      title: '提示',
      content: msg || '操作成功',
    });
  }

  static notificationError(msg: string) {
    msg = msg || this.DEFAULT_ERROR_MSG;
    this._runDebounce(() => {
      notification.error({ message: '提示', description: msg });
    }, msg);
  }

  static notificationErrorTitleAndMessage(title: string, msg: string) {
    msg = msg || this.DEFAULT_ERROR_MSG;
    this._runDebounce(() => {
      notification.error({ message: title, description: msg });
    }, msg);
  }

  static _msgHistory = {};

  static _runDebounce(fn: Function, msg: string, ignoreTime = 1000) {
    // @ts-ignore
    let lastTime = this._msgHistory[msg];
    let distanceTime = new Date().getTime() - lastTime;
    console.log('间隔时间', distanceTime);
    if (lastTime == null || distanceTime > ignoreTime) {
      fn();
    }

    // @ts-ignore
    this._msgHistory[msg] = new Date().getTime();
  }
}
