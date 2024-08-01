import hutool from "@moon-cn/hutool";
import {message} from "antd";


function errorMessageHandler(msg) {
  message.error(msg)
}


hutool.http.init({
  errorMessageHandler: errorMessageHandler,
})


