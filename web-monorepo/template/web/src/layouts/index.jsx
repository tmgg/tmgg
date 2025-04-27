import React from "react";
import {Layouts} from "@tmgg/tmgg-system";
import logo from '../asserts/logo.png'

export default class extends React.Component {


  render() {
    return <Layouts {...this.props} logo={logo}></Layouts>
  }


}
