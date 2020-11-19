import React, {useEffect, useState} from 'react';
import './App.css';
import {BrowserRouter, Redirect, Route, Switch} from 'react-router-dom';
import {LoginPage} from "./page/LoginPage";
import {IndexPage} from "./page/IndexPage";
import {AppStoreContextProvider, useAppStore} from "./context/AppStoreContext";
import {observer} from "mobx-react";

const App = () => {
    //TODO: Cleanup default useless React files...
    //TODO: UI: Button (Info + Action) => Form
    //TODO: Styled components

    return <AppStoreContextProvider>
        <Routes/>
    </AppStoreContextProvider>;
}

const Routes = observer(() => {
    const appStore = useAppStore();
    const [ready, setReady] = useState(false);
    const [version, setVersion] = useState<string>();

    useEffect(() => {
        appStore.init().then(value => setReady(true));
    }, [appStore]);

    const isLogged = appStore.isLogged();
    useEffect(() => {
        if (appStore.isLogged()) {
            appStore.getApi().wsVersion().get().then(version => setVersion(version.versionTimestamp));
        }
    }, [appStore, isLogged]);

    return ready ? <BrowserRouter>
        <div>
            <Switch>
                {appStore.isLogged()
                    ? <>
                        <Route exact path="/" component={IndexPage}/>
                        <Redirect exact to={"/"}/>
                    </>
                    : <>
                        <Route exact path="/login" component={LoginPage}/>
                        <Redirect exact to={"/login"}/>
                    </>
                }
            </Switch>
            {version && <span>{version}</span>}
        </div>
    </BrowserRouter> : null;
});

export default App;
