import {ApiContext} from "../Api";

export abstract class AbstractWs {
    protected readonly apiContext: ApiContext;
    protected readonly resource: string;

    protected constructor(apiContext: ApiContext, resource: string) {
        this.apiContext = apiContext;
        this.resource = resource;
    }
}
