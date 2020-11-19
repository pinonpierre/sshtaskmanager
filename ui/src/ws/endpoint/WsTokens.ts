import {ApiContext} from "../Api";
import {AbstractWs} from "./AbstractWs";
import {Token} from "../model/Token";

export class WsTokens extends AbstractWs {
    static readonly RESOURCE = "tokens";

    constructor(apiContext: ApiContext) {
        super(apiContext, WsTokens.RESOURCE);
    }


    async post(token: Token): Promise<Token> {
        return this.apiContext.post<Token>(this.resource, token);
    }

    async delete(): Promise<Token> {
        return this.apiContext.delete<Token>(this.resource);
    }

    async get(): Promise<Token> {
        return this.apiContext.get<Token>(this.resource);
    }
}