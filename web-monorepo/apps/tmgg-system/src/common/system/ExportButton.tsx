import React from "react";
import {SysConfig} from "./SysConfig";
import {Button} from "antd";
import {FileExcelOutlined} from "@ant-design/icons";
import {lodashExt as _} from "../utils";

interface IProps {
  form: any;
  url: string;
}

export class ExportButton extends React.Component<IProps, any> {



  render() {
    return <Button type='primary'  onClick={this.onClick}
                   icon={<FileExcelOutlined/>}>导出Excel</Button>
  }

  onClick = ()=>{
    let url = SysConfig.getServerUrl() + this.props.url
    if (this.props.form) {
      const param = this.props.form.getFieldFormatValue()
      url += "?" + _.toQueryString(param)
      url = SysConfig.appendTokenToUrl(url);
    }

    window.open(encodeURI(url))
  }
}
