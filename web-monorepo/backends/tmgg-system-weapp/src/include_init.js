import {registerRoutes} from "@tmgg/tmgg-base";

export function initKettle(){
    registerRoutes({
        'ureport': require('./pages/ureport').default,
    })
}




