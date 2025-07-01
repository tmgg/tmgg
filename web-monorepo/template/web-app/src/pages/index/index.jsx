import {View, Text} from '@tarojs/components'
import './index.less'
import {Component} from "react";
import {HttpUtil} from "../../common/http";
import {Button} from "@taroify/core";

export default class Index extends Component {


  ping(){
    HttpUtil.get('/app-api/ping').then(res=>{
      console.log('正常',res)
    }).catch(e => {
      console.log('异常', e)
    })
  }




  render() {
    return (
      <View className='index'>你好
        <Button onClick={this.ping} color='primary'>ping</Button>
      </View>
    )
  }
}
