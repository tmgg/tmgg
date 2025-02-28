import {defineConfig} from 'umi';


import {config} from "@tmgg/tmgg-system/config/config";

config.plugins= ['@tmgg/tmgg-base/plugins/routes']

export default defineConfig(config);
