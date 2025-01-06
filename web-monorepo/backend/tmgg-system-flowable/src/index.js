import {registerRoutes} from "@tmgg/tmgg-base";



export function initFlowable(){
    registerRoutes({
        'flowable/design': require('./pages/flowable/design/index.jsx').default,
        'flowable/task': require('./pages/flowable/task/index.jsx').default,
        'flowable/task/form': require('./pages/flowable/task/form.jsx').default,
        'flowable/monitor': require('./pages/flowable/monitor.jsx').default,
        'flowable': require('./pages/flowable/index.jsx').default,
        'flowable/test': require('./pages/flowable/test.jsx').default,
    })
}




