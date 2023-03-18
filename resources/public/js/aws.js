var region = "eu-north-1"
var accountId = "886559219659"
var userPoolId = "eu-north-1_Ytg6JkOy8"
var identityPoolId = "eu-north-1:434228c0-d69b-4dd3-93be-65105e8ef28b"
var issuer_key = "cognito-idp." + region + ".amazonaws.com/" + userPoolId

function CognitoIdentityCredentials(id_token) {
    return new AWS.CognitoIdentityCredentials({
        IdentityPoolId: identityPoolId,
        Logins: {
            // Have to use [key] "computed key" syntax from ES2015 below, since in JavaScript
            // would interpret issuer_key: id_token as literal "issuer_key": id_token
            [issuer_key]: id_token
        }
    },
    {region: region})
}

