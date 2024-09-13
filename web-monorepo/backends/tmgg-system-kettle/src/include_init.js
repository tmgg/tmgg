import {registerRoutes} from "@tmgg/tmgg-base";

export function initKettle(){
    registerRoutes({
        'kettle': require('./pages/kettle').default,
    })
}




