import React from "react";
import {Card} from "antd";
import {FieldEditor} from "@tmgg/tmgg-base";


export default class extends React.Component {

  state = {
  }

  render() {
    const arr = new Array(100).fill(1);

    return <Card>
      欢迎使用本系统

      <FieldEditor />
    </Card>
  }

}
