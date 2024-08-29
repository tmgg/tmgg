// 调整umi 默认配置


import {http} from "@tmgg/tmgg-base";
import {sys} from "@tmgg/tmgg-system";

http.axiosInstance.defaults.baseURL = sys.getServerUrl()






