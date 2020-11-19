import {Api} from "../ws/Api";
import {action, makeObservable, observable} from "mobx";
import {Configuration, ConfigUtils} from "../utils/ConfigUtils";
import assert from "assert";

export class AppStore {
    constructor() {
        makeObservable(this);
    }

    @observable
    private api?: Api;

    private configuration?: Configuration;

    init = async () => {
        this.configuration = await ConfigUtils.readConfiguration();
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
        await api.login(loginName, password);

        this.setApi(api);
    }

    public async loginByTokenId(tokenId: string) {
        this.logout();

        const api = new Api(this.configuration!.apiUrl);
        await api?.loginByTokenId(tokenId);

        this.setApi(api);
    }

    public isLogged() {
        return !!this.api;
    }

    public logout() {
        if (this.api) {
            this.api?.logout();
            this.setApi();
        }
    }
}
