import {View} from "@tarojs/components";
import {Button, Image, Input} from "@taroify/core";
import React from "react";
import HttpClient from "../util/HttpClient";
import UserUtil from "../util/UserUtil";

// https://developers.weixin.qq.com/community/develop/doc/00022c683e8a80b29bed2142b56c01
export default class extends React.Component {

  state = {

    avatarUrl: UserUtil.getUserInfo().avatarUrl,
    nickName: null
  }

  onChooseAvatar = (e) => {
    const avatarUrl = e.detail.avatarUrl


    HttpClient.uploadFile("/app/weapp/uploadAvatar", avatarUrl).then(rs => {
      let user = rs.data;
      UserUtil._setUserInfo(user)
    })
  }

  onChooseNickname = (e) => {
    const nickName = e.detail.value

    HttpClient.postForm("/app/weapp/updateUser", {nickName}).then(rs => {
      let user = rs.data;
      UserUtil._setUserInfo(user)
    })
  }


  render() {
    return <View style={{margin: 30}}>

      <Button open-type="chooseAvatar" onChooseAvatar={this.onChooseAvatar} color='info'>
        选择头像
      </Button>
      <Image src={this.state.avatarUrl} style={{width: 50, height: 50}}></Image>




      昵称：
      <Input type="nickname" placeholder="请输入昵称" onBlur={this.onChooseNickname}/>




    </View>
  }
}
