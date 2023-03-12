
function DynamoDBGetItem(identity_id, callback_fn) {
    AWS.config.region = "eu-north-1"
    const credentials = new AWS.CognitoIdentityCredentials(
            {IdentityPoolId: "eu-north-1:434228c0-d69b-4dd3-93be-65105e8ef28b",
            IdentityId: identity_id})
    console.log("Credentials: ", credentials)
    AWS.config.credentials = credentials
    const dynamoService = new AWS.DynamoDB()
    dynamoService.getItem(
        {Key: {id: {"S": "38462387647832647"}},
         TableName: "glory-of-empires"},
         callback_fn)
}