import {FetchUtils} from "../utils/FetchUtils";
import {WsTokens} from "./endpoint/WsTokens";
import {Token} from "./model/Token";
import {WsVersion} from "./endpoint/WsVersion";

export enum HttpMethodEnum {
    GET = "GET",
    POST = "POST",
    PUT = "PUT",
    DELETE = "DELETE"
}

export class WsError extends Error {
    constructor(public url: string, public response?: Response, public responseBody?: string, public requestBody?: string, public requestHeaders?: Headers, message?: string, public error?: Error) {
        super(message);
    }
}

export class ApiContext {
    private static readonly HEADER_ACCEPT_KEY = "accept";
    private static readonly HEADER_ACCEPT_VALUE = "application/json";
    private static readonly HEADER_CONTENT_TYPE_KEY = "content-type";
    private static readonly HEADER_CONTENT_TYPE_VALUE = "application/json";
    public static readonly HEADER_AUTHORIZATION_KEY = "authorization";
    private static readonly headerAuthorizationValue = (tokenId: string) => `Token ${tokenId}`;

    private tokenId?: string;

    constructor(readonly baseUrl: string) {

    }

    async doRequest<T>(method: HttpMethodEnum, resource: string, requestEntity: T, timeOut?: number): Promise<{ response: Response; responseEntity: T | undefined }> {
        const url = this.baseUrl + resource;

        const requestHeaders = new Headers();
        requestHeaders.append(ApiContext.HEADER_ACCEPT_KEY, ApiContext.HEADER_ACCEPT_VALUE);
        requestHeaders.append(ApiContext.HEADER_CONTENT_TYPE_KEY, ApiContext.HEADER_CONTENT_TYPE_VALUE);
        if (this.tokenId) {
            requestHeaders.append(ApiContext.HEADER_AUTHORIZATION_KEY, ApiContext.headerAuthorizationValue(this.tokenId));
        }

        const requestBody = requestEntity !== undefined ? JSON.stringify(requestEntity) : undefined;

        let error: Error | undefined = undefined;
        let response: Response | undefined = undefined;
        try {
            switch (method) {
                case HttpMethodEnum.GET:
                    response = await FetchUtils.get(url, requestHeaders, timeOut);
                    break;
                case HttpMethodEnum.POST:
                    response = await FetchUtils.post(url, requestBody, requestHeaders, timeOut);
                    break;
                case HttpMethodEnum.PUT:
                    response = await FetchUtils.put(url, requestBody, requestHeaders, timeOut);
                    break;
                case HttpMethodEnum.DELETE:
                    response = await FetchUtils.delete(url, requestHeaders, timeOut);
                    break;
            }
        } catch (e) {
            error = new WsError(url, response, undefined, requestBody, requestHeaders, "Fetch Error", e);
        }

        const responseBody = response === undefined ? undefined : await response?.text();

        if (!error && response && !response.ok) {
            error = new WsError(url, response, responseBody, requestBody, requestHeaders, "Response not successful");
        }

        if (error) {
            throw error;
        }

        return {
            response: response!,
            responseEntity: responseBody !== undefined ? JSON.parse(responseBody) : undefined
        };
    }

    async delete<T>(resource: string, timeOut?: number): Promise<T> {
        const result = await this.doRequest(HttpMethodEnum.DELETE, resource, undefined, timeOut);
        return result.responseEntity!;
    }

    async put<T>(resource: string, object: T, timeOut?: number): Promise<T> {
        const result = await this.doRequest(HttpMethodEnum.PUT, resource, object, timeOut);
        return result.responseEntity!;
    }

    async post<T>(resource: string, object: T, timeOut?: number): Promise<T> {
        const result = await this.doRequest(HttpMethodEnum.POST, resource, object, timeOut);
        return result.responseEntity!;
    }

    async get<T>(resource: string, params?: string, timeOut?: number): Promise<T> {
        const result = await this.doRequest(HttpMethodEnum.GET, resource + (params === undefined ? "" : params), undefined, timeOut);
        return result.responseEntity!;
    }

    hasTokenId() {
        return this.tokenId !== undefined;
    }

    setTokenId(tokenId?: string) {
        this.tokenId = tokenId;
    }
}

export class Api {
    private readonly context: ApiContext;

    constructor(baseUrl: string) {
        this.context = new ApiContext(baseUrl.endsWith("/") ? baseUrl : baseUrl + "/");
    }

    async loginByTokenId(tokenId: string) {
        //If already logged, logout before
        if (this.context.hasTokenId()) {
            this.logout();
        }

        this.context.setTokenId(tokenId);

        return this.initToken(await this.wsTokens().get());
    }

    async login(loginName: string, password: string): Promise<Token> {
        console.log("login");
        //If already logged, logout before
        if (this.context.hasTokenId()) {
            this.logout();
        }

        const token = new Token(loginName, password);

        return this.initToken(await this.wsTokens().post(token));
    }

    initToken(token: Token) {
        if (token) {
            this.context.setTokenId(token.id!);
            return token;
        }

        this.context.setTokenId();
        throw new Error("Empty token Received");
    }

    async logout() {
        if (this.context.hasTokenId()) {
            try {
                return await this.wsTokens().delete();
            } catch (e) {
                //Do nothing
            } finally {
                this.context.setTokenId();
            }
        }

        return undefined;
    }

    wsTokens(): WsTokens {
        return new WsTokens(this.context);
    }

    wsVersion(): WsVersion {
        return new WsVersion(this.context);
    }
}
