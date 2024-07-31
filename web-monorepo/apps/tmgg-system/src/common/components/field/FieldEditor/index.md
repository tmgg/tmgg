# FieldEditor



光标焦点离开才会触发onChange
```jsx
import React from 'react';
import { FieldEditor } from 'crec-web';


export default class extends React.Component {

    state = {
        value: '你好'
    }
    
  render() {
    return (
      <div>
        <FieldEditor value={this.state.value} onChange={v=>this.setState({value:v})}/>
        <div>输入的值为:</div>
        {this.state.value}
      </div>
    )
  }
}
```

# 简写Editor
```jsx
import React from 'react';
import { Editor } from 'crec-web';


export default class extends React.Component {

  render() {
    return (
        <Editor />
    )
  }
}
```


