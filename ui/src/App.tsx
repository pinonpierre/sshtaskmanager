import React, {useEffect} from 'react';
import logo from './logo.svg';
import './App.css';
import {Api} from "./ws/Api";
import {ConfigUtils} from "./utils/ConfigUtils";

async function init() {
    const configuration = await ConfigUtils.readConfiguration();
    const api = new Api(configuration.apiUrl);
    await api.login("admin", "admin");

    const version = await api.wsVersion().get();
    console.log(version);
}

function App() {

    //TODO: Create Context to store api
    //TODO: Router + rights
    //TODO: Cleanup default useless React files...
    //TODO: UI: Button (Info + Action) => Form

    useEffect(() => {
        init();
    });

    return (
        <div className="App">
            <header className="App-header">
                <img src={logo} className="App-logo" alt="logo"/>
                <p>
                    Edit <code>src/App.tsx</code> and save to reload.
                </p>
                <a
                    className="App-link"
                    href="https://reactjs.org"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    Learn React
                </a>
            </header>
        </div>
    );
}

export default App;
