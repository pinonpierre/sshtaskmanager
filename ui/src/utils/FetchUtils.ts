const DEFAULT_TIMEOUT = 20000;

export class FetchUtils {
    static async put(url: string, body?: string, headers?: Headers, timeOut: number = DEFAULT_TIMEOUT): Promise<Response> {
        const request = {
            body: body,
            headers,
            method: "PUT"
        };
        return FetchUtils.timeoutPromise<Response>(timeOut, fetch(url, request));
    }

    static async post(url: string, body?: string, headers?: Headers, timeOut: number = DEFAULT_TIMEOUT): Promise<Response> {
        const request = {
            body: body,
            headers,
            method: "POST"
        };
        return FetchUtils.timeoutPromise<Response>(timeOut, fetch(url, request));
    }

    static async delete(url: string, headers?: Headers, timeOut: number = DEFAULT_TIMEOUT) {
        return FetchUtils.timeoutPromise<Response>(timeOut, fetch(url,
            {
                headers,
                method: "DELETE"
            }));
    }

    static async get(url: string, headers?: Headers, timeOut: number = DEFAULT_TIMEOUT): Promise<Response> {
        return FetchUtils.timeoutPromise<Response>(timeOut, fetch(url,
            {
                headers,
                method: "GET"
            }));
    }

    static stringify(params: any, encodeParam = true): string {
        if (params !== undefined) {
            const queryString: string = Object.keys(params)
                .filter(key => params[key] != null &&
                    (!Array.isArray(params[key]) || (Array.isArray(params[key]) && params[key].length > 0))
                )
                .map(key => {
                    return key + "=" + (encodeParam ? encodeURIComponent(params[key]) : params[key]);
                })
                .join("&");
            if (queryString != null && queryString.length > 0) {
                return "?" + queryString;
            }
        }
        return "";
    }

    private static timeoutPromise<T>(ms: number, promise: Promise<T>): Promise<T> {
        if (ms === 0) {
            return promise;
        }
        return new Promise((resolve, reject) => {
            const timeoutId = setTimeout(() => {
                reject(new Error("Promise timeout after " + ms + "ms."));
            }, ms);
            promise.then(
                (res) => {
                    clearTimeout(timeoutId);
                    resolve(res);
                },
                (err) => {
                    clearTimeout(timeoutId);
                    reject(err);
                }
            );
        });
    }
}