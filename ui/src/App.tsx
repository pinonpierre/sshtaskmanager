import React, {useEffect, useState} from 'react';
import './App.css';
import {BrowserRouter, Redirect, Route, Switch} from 'react-router-dom';
import {LoginPage} from "./page/LoginPage";
import {IndexPage} from "./page/IndexPage";
import {AppStoreContextProvider, useAppStore} from "./context/AppStoreContext";
import {observer} from "mobx-react";
import {css, Global} from "@emotion/react";
import styled from "@emotion/styled";
import {Button, Tooltip} from "@material-ui/core";
import {Version} from "./ws/model/Version";

const App = () => {
    //TODO: Cleanup default useless React files...
    //TODO: UI: Button (Info + Action) => Form
    //TODO: Title

    return <AppStoreContextProvider>
        <AppLayout/>
    </AppStoreContextProvider>;
}

const globalStyles = css`
    html, html > body, html > body > div#root {
        height: 100%;
    }
`;

const MainDiv = styled.div`
    background-color: lightblue;
    height: 100%;
`;

const AppLayout = observer(() => {
    const appStore = useAppStore();
    const [ready, setReady] = useState(false);
    const [version, setVersion] = useState<Version>();

    useEffect(() => {
        appStore.init().then(() => appStore.tryRelogin().then(() => setReady(true)));
    }, [appStore]);

    const isLogged = appStore.isLogged();
    useEffect(() => {
        if (appStore.isLogged()) {
            appStore.getApi().wsVersion().get().then(setVersion);
        }
    }, [appStore, isLogged]);

    const logout = async () => {
        try {
            await appStore.logout();
        } catch (e) {
            //Do nothing
        }
    }

    return <>
        <Global styles={globalStyles}/>

        <MainDiv>
            {ready && <BrowserRouter>
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
            </BrowserRouter>}
            {appStore.isLogged() && <>
                {version && <Tooltip title={version.versionTimestamp}><span>{version.version}</span></Tooltip>}
                <Button variant="contained" onClick={logout}>Logout</Button>
            </>}
        </MainDiv>
    </>;
});

export default App;
