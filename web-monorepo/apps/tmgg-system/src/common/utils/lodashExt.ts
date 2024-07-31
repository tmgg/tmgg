/**
 * lodash 扩展
 */


import lodash from 'lodash'




const lodashExt = {
  ...lodash,

  toQueryString: (params:any)=>{
    if(params == null){
      return "";
    }

    const arr = [];

    const keys = Object.keys(params);
    for (let key of keys) {
      const value = params[key]
      if(value != null){
        arr.push(key+"=" + value)
      }
    }

    return arr.join("&")
  }



}



export {
  lodashExt
}


