import {HttpUtil, SysUtil} from "./system";
import {registerField} from "@tmgg/pro-table";
import {fieldRegistry} from "./components/field/registry";

export * from './components'
export * from './system'

export function initBase(){
    HttpUtil.axiosInstance.defaults.baseURL = SysUtil.getServerUrl()

    for (let componentType in fieldRegistry){
        const componentClass = fieldRegistry[componentType]
        registerField(componentType, componentClass)
    }


}
