import {registerField} from "@tmgg/pro-table";
import {FieldDictRadio} from "../components";
import {http} from "../tools";
import {sys} from "./SysConfig";


export function initSystem(){
    http.axiosInstance.defaults.baseURL = sys.getServerUrl()

    registerField('dict', FieldDictRadio)
    registerField('dictRadio', FieldDictRadio)
}


