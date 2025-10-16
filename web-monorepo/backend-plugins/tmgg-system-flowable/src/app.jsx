import {HttpUtil, initBase} from "@tmgg/tmgg-base";
import {formRegistry} from "./common/FormRegistry";
import React from "react";
import {Form, Input} from "antd";

initBase()


// demo
class DriverForm extends React.Component{


    submit = ()=>{
        return new Promise((resolve, reject) => {
            this.formRef.current.validateFields().then(values=>{
                // 模拟提交数据
                HttpUtil.get('/site-info').then(()=>{
                    resolve()
                }).catch(reject)
            }).catch(reject)
        })
    }

    formRef = React.createRef()

    render() {
      return  <div>
          派车的自定义表单， 属性：{JSON.stringify(Object.keys(this.props))}

          <Form ref={this.formRef} >
              <Form.Item name='driverName' label='司机姓名' rules={[{required:true}]}>
                  <Input />
              </Form.Item>
          </Form>

      </div>
    }
}


formRegistry.register("driverForm",DriverForm)
