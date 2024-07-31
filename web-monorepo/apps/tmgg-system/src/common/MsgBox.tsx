import {Modal} from "antd";

export class MsgBox {

  static error(content:string){
    Modal.error({
      title: '错误',
      content: content,
      autoFocusButton: 'ok',
      okText: '确定',
      maskClosable: false,
    })
  }

  static alert(content:string){
    Modal.info({
      icon: null,
      title: '操作提示',
      content: content,
      autoFocusButton: 'ok',
      okText: '确定',
      maskClosable: false,
    })
  }

  static success(content:string, onOk:any = null){
    Modal.success({
      title: '操作成功提示',
      content: content,
      autoFocusButton: 'ok',
      okText: '确定',
      maskClosable: false,
      onOk: () => {
        if(onOk){
          onOk();
        }
      },
    })
  }
}
