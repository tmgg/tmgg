import {registerField} from "@tmgg/pro-table";
import {FieldDictRadio} from "../components";
import {httpUtil} from "../utils";
import {SysUtil} from "./sys";


export function initSystem(){
    httpUtil.axiosInstance.defaults.baseURL = SysUtil.getServerUrl()

    registerField('dict', FieldDictRadio)
    registerField('dictRadio', FieldDictRadio)
}


