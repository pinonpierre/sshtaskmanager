import {Api} from "../ws/Api";
import {action, makeObservable, observable} from "mobx";
import {Configuration, ConfigurationUtils} from "../utils/ConfigurationUtils";
import assert from "assert";

export class AppStore {
    private static readonly LOCAL_STORAGE_TOKEN_KEY = "token";

    constructor() {
        makeObservable(this);
    }

    @observable
    private api?: Api;

    private configuration?: Configuration;

    init = async () => {
        this.configuration = await ConfigurationUtils.readConfiguration();
    }

    public getApi() {
        assert(this.api);

        return this.api;
    }

    @action
    public setApi(api?: Api) {
        this.api = api;
    }

    public async login(loginName: string, password: string) {
        this.logout();

        const api = new Api(this.configuration!.apiUrl);
        const token = await api.login(loginName, password);

        this.setApi(api);
        localStorage.setItem(AppStore.LOCAL_STORAGE_TOKEN_KEY, token.id!);
    }

    public async tryRelogin() {
        const tokenId = localStorage.getItem(AppStore.LOCAL_STORAGE_TOKEN_KEY);
        if (tokenId === null) {
            return false;
        }
        this.logout();

        const api = new Api(this.configuration!.apiUrl);
        try {
            await api?.loginByTokenId(tokenId);
        } catch (e) {
            localStorage.removeItem(AppStore.LOCAL_STORAGE_TOKEN_KEY);
            return false;
        }

        this.setApi(api);
        return true;
    }

    public isLogged() {
        return !!this.api;
    }

    public logout() {
        if (this.api) {
            this.api?.logout();
            this.setApi();
            localStorage.removeItem(AppStore.LOCAL_STORAGE_TOKEN_KEY);
        }
    }
}
