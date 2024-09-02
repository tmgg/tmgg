import {http} from "@tmgg/tmgg-base";
import {Modal} from "antd";

httpUtil.init({
  errorMessageHandler: function (msg) {
    Modal.error({
      title: '请求异常',
      content: msg
    })
  }
})
