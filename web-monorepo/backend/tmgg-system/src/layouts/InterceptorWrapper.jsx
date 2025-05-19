import React from "react";
import {HttpUtil, PageLoading, SysUtil} from "@tmgg/tmgg-base";
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
        if (siteInfoOnly) {
            interceptors = interceptors.filter(i=>i instanceof SiteInfoInterceptor)
        }


        this.setState({loading: true})
        for (let interceptor of interceptors) {
            const message = interceptor.getMessage();
            console.debug('拦截器', interceptor, message)

            this.setState({message});
            const code = await interceptor.preHandle()
            if (code === 401) {
                this.setState({error: true, code, loading: false, message: '登录被拦截'})
                return
            }

        }
        this.setState({loading: false, message: '全部处理完成'})
    };

    reLogin() {
        HttpUtil.get('/logout').finally(() => {
            SysUtil.setToken(null)
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
                return <Result status="403" title='401' subTitle='未登录或登录已过期,请重新登录'
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
