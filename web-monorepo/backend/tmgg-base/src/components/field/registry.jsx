import {FieldRemoteSelect} from "./FieldRemoteSelect";
import {FieldDictRadio} from "./dict";
import {FieldPassword} from "./FieldPassword";
import {FieldDateTimePickerString} from "./time";
import {FieldRadioBoolean} from "./FieldRadioBoolean";
import {FieldInput} from "./FieldInput";


export const fieldRegistry = {
    'input':FieldInput,
    'remoteSelect': FieldRemoteSelect,
    'dict':FieldDictRadio,
    'dictRadio':FieldDictRadio,
    'password':FieldPassword,
    'datetime':FieldDateTimePickerString,
    'boolean':FieldRadioBoolean
}


