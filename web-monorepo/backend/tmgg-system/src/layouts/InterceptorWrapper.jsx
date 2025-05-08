import React from "react";
import {HttpUtil, PageLoading} from "@tmgg/tmgg-base";
import {SiteInfoInterceptor} from "./interceptor/SiteInfoInterceptor";
import {Button, Result} from "antd";
import {AuthInterceptor} from "./interceptor/AuthInterceptor";
import {UserInfoInterceptor} from "./interceptor/UserInfoInterceptor";
import {history} from "umi";

export class InterceptorWrapper extends React.Component {

    state = {
        code: null,
        message: null,
        error: false,
        loading: true
    }


    interceptors = [
        new SiteInfoInterceptor(),
        new AuthInterceptor(),
        new UserInfoInterceptor()
    ]

    async componentDidMount() {
        await this.process();
    }



    componentDidUpdate(prevProps, prevState, snapshot) {
        let pre = prevProps.pathname;
        let cur = this.props.pathname;
        console.log('比较路由', pre, cur)
        if (pre !== cur) {
            if (pre === '/login' || cur === '/login') {
                this.process()
            }
        }
    }


    process = async () => {
        this.setState({
            code: null,
            message: null,
            error: false,
            loading: true
        })
        let interceptors = this.interceptors;
        let siteInfoOnly = this.props.siteInfoOnly;
        console.log('开始处理拦截器, siteInfoOnly='+siteInfoOnly)
        this.setState({loading: true})
        for (let interceptor of interceptors) {

            if (siteInfoOnly) {
                if (!(interceptor instanceof SiteInfoInterceptor)) {
                    continue
                }
            }

            const message = interceptor.getMessage();
            console.log('拦截器', interceptor, message)

            this.setState({message});
            const code = await interceptor.preHandle()
            console.log('拦截器code', code)
            if (code === 401) {
                this.setState({error: true, code, loading: false, message: '登录被拦截'})
                return
            }

        }
        this.setState({loading: false, message: '全部处理完成'})
    };

    reLogin() {
        localStorage.clear()
        HttpUtil.get('/logout').finally(() => {
            history.push('/login')
        })
    }

    render() {
        let {loading,code, error,message} = this.state

        if (loading === true) {
            return <PageLoading message={message}/>
        }

        if (error) {

            if (code === 401) {
                return <Result status='warning' title='401' subTitle='未登录或登录过期,请重新登录'
                               extra={[
                                   <Button type="primary" key="console" size='large' onClick={this.reLogin}>
                                       重新登录
                                   </Button>,
                               ]}
                />
            }
            return <Result
                status="500"

                subTitle={message}
            />
        }

        return this.props.children
    }
}
