import {ApiContext} from "../Api";
import {AbstractWs} from "./AbstractWs";
import {Version} from "../model/Version";

export class WsVersion extends AbstractWs {
    static readonly RESOURCE = "version";

    constructor(apiContext: ApiContext) {
        super(apiContext, WsVersion.RESOURCE);
    }

    async get(): Promise<Version> {
        return this.apiContext.get<Version>(this.resource);
    }
}
