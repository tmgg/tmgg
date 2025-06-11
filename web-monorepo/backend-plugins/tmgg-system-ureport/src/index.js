import {registerRouteComponents} from "@tmgg/tmgg-base";

export function initKettle(){
    registerRouteComponents({
        'ureport': require('./pages/ureport').default,
    })
}




