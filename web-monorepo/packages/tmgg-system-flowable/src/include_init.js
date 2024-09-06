import {registerRoutes} from "@tmgg/tmgg-base";


export function initJob(){
    registerRoutes({
        'job': require('./pages/job').default,
        'job/log': require('./pages/job/log').default,
    })

}




