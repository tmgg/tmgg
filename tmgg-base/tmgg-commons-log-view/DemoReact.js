import {LazyLog, ScrollFollow} from "react-lazylog";
import {Component} from "react";

class App extends Component {
    render() {
        console.log('render App')

        if (!location.search) {
            return <>需要路径参数，如 ?path=D:\demo.log</>
        }

        let isDev = process.env.NODE_ENV === "development";
        let path = location.search.substring(location.search.indexOf('=') + 1);
        let port = isDev ? 8080 : location.port;

        let url = "ws://" + location.hostname + ":" + port + "/api/log-view?path=" + path;
        return <div style={{width: '100%', height: '100%', minHeight: '100vh'}}>
            <ScrollFollow
                startFollowing={true}
                render={({follow, onScroll}) =>
                    <LazyLog
                        websocket
                        fetchOptions={{credentials: 'include'}}
                        url={url}
                        selectableLines={true}
                        follow={follow}
                        onScroll={onScroll}/>}
            />
        </div>;
    }
}

export default App;
