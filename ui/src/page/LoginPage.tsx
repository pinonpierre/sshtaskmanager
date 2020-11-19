import React, {ChangeEvent, FormEvent, useState} from "react";
import {Button, TextField} from "@material-ui/core";
import {useAppStore} from "../context/AppStoreContext";
import {observer} from "mobx-react";

export const LoginPage = observer(() => {
    const appStore = useAppStore();
    const [loginField, setLoginField] = useState<string>("");
    const [passwordField, setPasswordField] = useState<string>("");

    const onSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        try {
            await appStore.login(loginField, passwordField);
        } catch (e) {
            console.log("error", e);
        }
    };

    return <div>
        <form onSubmit={onSubmit}>
            <TextField label="Login" value={loginField} onChange={onChangeAdapter(setLoginField)}/>
            <TextField label="Password" value={passwordField} onChange={onChangeAdapter(setPasswordField)}/>

            <Button type="submit" variant="contained">Login</Button>
        </form>
    </div>;
});

const onChangeAdapter = (setter: (value: any) => void) => {
    return (event: ChangeEvent<HTMLInputElement>) => setter(event.target.value);
};
