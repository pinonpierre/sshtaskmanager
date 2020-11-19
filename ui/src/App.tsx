import React, {useEffect} from 'react';
import logo from './logo.svg';
import './App.css';
import {Api} from "./ws/Api";

function App() {

  useEffect(() => {
    const api = new Api("http://localhost:8080/api");
    api.login("admin", "admin").then(() => {
      api.wsVersion().get().then(value => console.log(value));
    });

  });


  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
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
