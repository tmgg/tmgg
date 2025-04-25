import React from "react";
import img from "../../public/welcome.png"
import './index.less'


export default class extends React.Component {



  render() {
    return <div className='flex-center'>
      <img src={img}/>
    </div>
  }


}
