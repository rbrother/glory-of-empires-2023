var fs = require('fs')

exports.handler = async event => {
    try {
        let path = event.rawPath // Eg. "/fdofkdofkdofk/fdfdfdjf"
        if (path === "" || path === "/") { path = "/index.html" }
        path = `files/${path}`
        const extension = path.split('.').pop()
        const contentType = 
            (extension == "html" || extension == "htm") ? "text/html" :
            (extension == "js") ? "text/javascript" : 
            (extension == "ico") ? "image/vnd.microsoft.icon" :
            "text/plain"         
        // Read the file as binary data
        const data = fs.readFileSync(path);
            
        let bodyStr;
        let isBinary;

        // Check if the file should be served as binary
        if (extension === "ico") {
            bodyStr = data.toString('base64'); // Encode binary data to base64
            isBinary = true;
        } else {
            bodyStr = data.toString('utf8'); // Convert text files to UTF-8 string
            isBinary = false;
        }        
        return { 
            statusCode: 200, 
            headers: {
                'Content-Type': contentType
            },
            body: bodyStr,
            isBase64Encoded: isBinary 
        }
    } catch(e) {
        return { statusCode: 500, body: e.stack }
    }
}

const exampleEvent = 
{
    "version": "2.0",
    "routeKey": "$default",
    "rawPath": "/prod/fdofkdofkdofk/fdfdfdjf",
    "rawQueryString": "",
    "headers": {
        "accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
        "accept-encoding": "gzip, deflate, br, zstd",
        "accept-language": "en,en-GB;q=0.9,en-US;q=0.8,fi;q=0.7",
        "cache-control": "max-age=0",
        "content-length": "0",
        "host": "3jcv5yd21l.execute-api.eu-north-1.amazonaws.com",
        "upgrade-insecure-requests": "1",
        "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36",
        "x-amzn-trace-id": "Root=1-66ca4b15-7fd9f130170a6edd51c5b9ff",
        "x-forwarded-for": "85.156.182.118",
        "x-forwarded-port": "443",
        "x-forwarded-proto": "https"
    },
    "requestContext": {
        "accountId": "886559219659",
        "apiId": "3jcv5yd21l",
        "domainName": "3jcv5yd21l.execute-api.eu-north-1.amazonaws.com",
        "domainPrefix": "3jcv5yd21l",
        "http": {
            "method": "GET",
            "path": "/prod/fdofkdofkdofk/fdfdfdjf",
            "protocol": "HTTP/1.1",
            "sourceIp": "85.156.182.118",
            "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36"
        },
        "requestId": "dCCrWh5rAi0EJJg=",
        "routeKey": "$default",
        "stage": "prod",
        "time": "24/Aug/2024:21:05:25 +0000",
        "timeEpoch": 1724533525162
    },
    "isBase64Encoded": false
}
