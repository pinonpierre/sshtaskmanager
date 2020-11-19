import React, {ProviderProps} from "react";
import {AppStore} from "../store/AppStore";

const appStore = new AppStore()

const AppStoreContext = React.createContext<AppStore>(appStore);

export const AppStoreContextProvider = ({children}: Omit<ProviderProps<AppStore>, 'value'>) =>
    <AppStoreContext.Provider value={appStore}>{children}</AppStoreContext.Provider>;

export const useAppStore = () =>
    React.useContext(AppStoreContext);
