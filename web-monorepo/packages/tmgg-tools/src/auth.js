import {get, set} from "./storage";

let globalIsLogin = get('isLogin',false);


export function isLogin(){
    return globalIsLogin
}
export function setIsLogin(b){
    globalIsLogin = b;
    set('isLogin', b)
}

export function addPerm(permCode){

}

export function hasPerm(permCode){

}
