import {registerRoutes} from "@tmgg/tmgg-base";


export function initJob(){
    registerRoutes({
        'job': require('./pages/job').default,
        'job/logList': require('./pages/job/logList').default,
    })

}




