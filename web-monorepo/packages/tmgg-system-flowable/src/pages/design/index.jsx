import React from "react";
import {Button, Card, Col, Empty, message, Modal, Row, Space} from "antd";

import 'bpmn-js/dist/assets/diagram-js.css'
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css'
import {getBusinessObject} from "bpmn-js/lib/util/ModelUtil";
import BpmnModeler from 'bpmn-js/lib/Modeler'

import './index.css'
import customTranslate from "../../components/flow/customTranslate/customTranslate";
import TimerEventDefinitionForm from "../../components/flow/design/form/TimerEventDefinitionForm";
import ServiceTaskForm from "../../components/flow/design/form/ServiceTaskForm";
import ConditionForm from "../../components/flow/design/form/ConditionForm";
import UserTaskForm from "../../components/flow/design/form/UserTaskForm";
import palette from "../../components/flow/design/palette";
import OriginModule from 'diagram-js-origin';
import contextPad from "../../components/flow/design/contextPad";
import {CloudUploadOutlined, EditOutlined, SaveOutlined} from "@ant-design/icons";
import {HttpClient} from "@crec/lang";
import qs from "qs";

export default class extends React.Component {


  state = {
    id: null,
    model: null,
    conditionVariable: null,

    elementType: null,
    elementName: '',

    showForm: false, // 表单切换过渡使用


    openXmlModal: false,
    xml: null

  }
  curBo = null
  curNode = null

  preXmlRef = React.createRef()

  componentDidMount() {
    let params = qs.parse(window.location.search.substring(1));

    this.state.id = params.id

    this.bpmnModeler = new BpmnModeler({
      additionalModules: [
        // 汉化翻译
        {
          translate: ['value', customTranslate]
        },
        palette,
        contextPad,
        OriginModule,
      ]
    });



    this.modeling = this.bpmnModeler.get('modeling'); // 建模， 包含很多方法
    this.moddle = this.bpmnModeler.get('moddle'); // 数据模型， 主要存储元数据




    HttpClient.get('flowable/model/detail', {id: this.state.id}).then(rs => {
      let {conditionVariable, model} = rs.data;
      this.setState({model, conditionVariable})
      this.initBpmn(model.content)
    })

    window.bpmnModeler = this.bpmnModeler
    window.moddle = this.moddle;
    window.modeling = this.modeling;
  }

  initBpmn(xml) {
    const id = '#flow-canvas-' + this.state.id
    this.bpmnModeler.attachTo(id);
    this.bpmnModeler.importXML(xml)

    this.bpmnModeler.on('element.contextmenu', e => e.preventDefault()) // 关闭右键，影响操作
    this.bpmnModeler.on('selection.changed', this.onSelectionChanged);
    //this.bpmnModeler.on('element.changed', this.refreshForm);
  }




  showXML = () => {
    this.bpmnModeler.saveXML({format: true}).then(res => {
      this.setState({
        openXmlModal:true,
        xml: res.xml
      })
    })
  }


  handleChangeXml = () => {
    const xml = this.preXmlRef.current.innerText;

    const root  = this.bpmnModeler.getDefinitions().rootElements[0]
    const {id, name} = root;


    this.bpmnModeler.importXML(xml)
    root.set('id', id)
    root.set('name',name)

    message.success('修改完成');
    this.setState({openXmlModal:false})
  }

  onSelectionChanged = e => {
    const {newSelection} = e;
    if (newSelection.length != 1) {
      this.setState({showForm: false})
      return null
    }
    const curNode = newSelection[0]
    const curBo = getBusinessObject(curNode)

    this.curBo = curBo;
    this.curNode = curNode;

    // 给一个过渡期
    this.setState({
      elementType: curBo.$type.replace("bpmn:", ""),
      elementName: curBo.get('name'),
    })
    this.refreshForm()
  }


  handleSubmit = () => {
    let id = this.state.id;

    return new Promise((resolve, reject) => {
      const hide = message.loading('保存中...', 0)
      this.bpmnModeler.saveXML().then(res => {
        HttpClient.post('/flowable/model/saveContent', {id: id, content: res.xml}).then(rs => {
          hide()
          message.success('服务端保存成功')
          resolve()
        }).catch(e => {
          hide()
          reject()
        })
      })
    })
  }
  handleDeploy = () => {
    let id = this.state.id;

    this.bpmnModeler.saveXML().then(res => {
      HttpClient.post('/flowable/model/deploy', {id: id, content: res.xml}).then(rs => {
        if(rs.success){
          message.success(rs.message)
        }else {
          message.error(rs.message)
        }

      })
    })
  }




  refreshForm = () => {
    this.setState({showForm: false}, () => this.setState({showForm: true}))
  }


  render() {

    return <Card title={'流程设计 - ' + this.state.model?.name}
                 extra={<Space>
                   <Button type='primary' icon={<SaveOutlined />} onClick={this.handleSubmit}>暂存</Button>
                   <Button type='danger' icon={<CloudUploadOutlined />} onClick={this.handleDeploy}>保存并部署</Button>
                   <Button icon={<EditOutlined />} onClick={this.showXML}>编辑文本</Button>
                 </Space>}>


      <Row gutter={16} wrap={false} style={{height: '90vh'}}>
        <Col flex='auto'>
          <div id={"flow-canvas-" + this.state.id} style={{width: '100%', height: '100%'}}></div>
        </Col>

        <Col flex='300px'>
          <Card title='属性面板'>
            {this.renderForm()}
          </Card>
        </Col>
      </Row>


      <Modal title='流程定义XML文本内容' open={this.state.openXmlModal}
             okText='修改'
             width='70vw'
             onOk={this.handleChangeXml}
              onCancel={()=>this.setState({openXmlModal:false})}>
        <div style={{maxHeight: '70vh', overflow:'auto'}}>
        {this.state.xml &&  <pre ref={this.preXmlRef}   contentEditable >
          {this.state.xml}
        </pre>}

        </div>
      </Modal>
    </Card>
  }


  renderForm() {
    if (!this.state.showForm) {
      return <Empty description='请选择节点'/>
    }
    const {elementType, conditionVariable} = this.state;
    const {curBo} = this

    switch (elementType) {
      case 'SequenceFlow':


        return <ConditionForm conditionVariable={conditionVariable}
                              moddle={this.moddle}
                              bo={curBo}
                              node={this.curNode}
                              modeling={this.modeling}
        />
      case 'UserTask':
        return <UserTaskForm bo={curBo} node={this.curNode} modeling={this.modeling}/>
      case 'ServiceTask':
        return <ServiceTaskForm bo={curBo}/>
      case 'StartEvent':
        if (curBo.eventDefinitions != null) {
          return <TimerEventDefinitionForm bo={curBo}/>
        }
    }


    return <></>


  }




}
