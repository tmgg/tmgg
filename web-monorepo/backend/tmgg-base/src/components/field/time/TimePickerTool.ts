import moment from "moment";


export function valueToDate(value:any, fmt:string){
  if (value != null && value != '') {
    let type = typeof value;
    if (type == 'string' || type == 'number') {
      value = moment(value, fmt);
    }

  }

  return value;
}
