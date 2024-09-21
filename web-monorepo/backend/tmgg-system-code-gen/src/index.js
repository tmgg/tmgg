import {registerRoutes} from "@tmgg/tmgg-base";


export function initCodeGen(){
    registerRoutes({
        'code': require('./pages/code').default,
    })
}




