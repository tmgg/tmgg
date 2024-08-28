import {Card, Tabs} from 'antd'
import React from 'react'
import JobList from "./pages/JobList";
import LogList from "./pages/LogList";


export default class extends React.Component {


    render() {
        return <>

            <Card>
                任务调度管理
            </Card>

            <div style={{margin: 12}}></div>

            <Card >
                <Tabs items={[
                    {label: '任务管理', key: 'app', children: <JobList/>},
                    {label: '执行记录', key: 'user', children: <LogList />},
                ]}
                      destroyInactiveTabPane
                ></Tabs>
            </Card>
        </>
    }
}



