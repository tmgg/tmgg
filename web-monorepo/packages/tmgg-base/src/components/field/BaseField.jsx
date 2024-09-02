import React from "react";

export default class extends React.Component {

    static defaultProps = {
        mode: 'edit', // read, edit
        value: undefined,
        onChange: ()=>{}
    }



    render() {
        if(this.props.mode === 'read'){
            return this.renderRead()
        }

        return  this.renderEdit();
    }

    renderRead(){

    }
    renderEdit(){

    }

}
