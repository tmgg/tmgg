import {registerField} from "@tmgg/pro-table";
import {FieldDictRadio} from "../components";
import {HttpUtil} from "../utils";
import {SysUtil} from "./sys";


export function initSystem(){
    HttpUtil.axiosInstance.defaults.baseURL = SysUtil.getServerUrl()

    registerField('dict', FieldDictRadio)
    registerField('dictRadio', FieldDictRadio)
}


