import {registerRouteComponents} from "@tmgg/tmgg-base";

export function initKettle(){
    registerRouteComponents({
        'kettle': require('./pages/kettle').default,
    })
}




