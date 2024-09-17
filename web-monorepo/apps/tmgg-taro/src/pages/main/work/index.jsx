import {Text, View} from '@tarojs/components'

import './index.scss'
import React, {Component} from "react";
import MainTabs from "../../../components/MainTabs";
import {Grid, GridItem} from "@antmjs/vantui";
import {HttpUtil} from "@tmgg/tmgg-app-base";
import Taro from "@tarojs/taro";


export default class extends Component {

  state = {
    menuTree: []
  }

  componentDidMount() {
    HttpUtil.get('menuTree').then(rs => {
      this.setState({menuTree: rs})
    })
  }

  jumpTo(key){
    Taro.navigateTo({
      url: '/pages/system/' + key +'/index'
    })
  }
  render() {
    return <View className='page page-pa'>

      {this.state.menuTree.map(menu => <View>
        <View>
          {menu.label}
        </View>
        <Grid>
          {menu.children.map(m => <GridItem onClick={()=>this.jumpTo(m.key)}   icon="photo-o" text={m.label}/>)}
        </Grid>
      </View>)}


      <MainTabs value='work'></MainTabs>
    </View>;

  }


}
