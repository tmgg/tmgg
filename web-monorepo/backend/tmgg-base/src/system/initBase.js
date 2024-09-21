import {registerField} from "@tmgg/pro-table";
import {FieldDictRadio, FieldPassword} from "../components";
import {SysUtil} from "./sys";
import {HttpUtil} from "./http";


export function initBase(){
    HttpUtil.axiosInstance.defaults.baseURL = SysUtil.getServerUrl()

    registerField('dict', FieldDictRadio)
    registerField('dictRadio', FieldDictRadio)
    registerField('password', FieldPassword)
}


