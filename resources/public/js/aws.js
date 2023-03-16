var region = "eu-north-1"
var accountId = "886559219659"
var userPoolId = "eu-north-1_Ytg6JkOy8"
var identityPoolId = "eu-north-1:434228c0-d69b-4dd3-93be-65105e8ef28b"
var issuer_key = "cognito-idp." + region + ".amazonaws.com/" + userPoolId

var awsCredentials = null

function CognitoIdentityGetId(id_token, callback) {
    // Calling GetID as instructed at https://docs.aws.amazon.com/cognito/latest/developerguide/authentication-flow.html
    console.log("CognitoIdentityGetId: ", issuer_key)
    var params = {
      IdentityPoolId: identityPoolId,
      AccountId: accountId,
      // name-value pairs that map provider names to provider tokens
      Logins: {
        // Have to use [key] "computed key" syntax from ES2015 below, since in JavaScript
        // would interprete issuer_key: id_token as literal "issuer_key": id_token
        [issuer_key]: id_token
      }
    }
    var cognitoIdentity = new AWS.CognitoIdentity({region: region})
    cognitoIdentity.getId(params, function(err, data) {
      if (err) console.log(err, err.stack)
      else     callback(data);           // successful response
    })
}

function CognitoGetCredentialsForIdentity(identity_id, id_token, callback) {
    // Calling is instructed in https://docs.aws.amazon.com/cognito/latest/developerguide/authentication-flow.html
    console.log("CognitoGetCredentialsForIdentity: ", identity_id)
    var params = {
      IdentityId: identity_id, // earlier we were mistakenly putting user-id from user-pool here
      // We should not need the Logins-map, since we don't use eternal providers
      Logins: {
        // Have to use [key] "computed key" syntax from ES2015 below, since in JavaScript
        // would interprete issuer_key: id_token as literal "issuer_key": id_token
        [issuer_key]: id_token
      }
    }
    var cognitoIdentity = new AWS.CognitoIdentity({region: region})
    cognitoIdentity.getCredentialsForIdentity(params, function(err, data) {
      if (err) {
        console.log(err, err.stack)
      } else {
        const c =  data.Credentials
        awsCredentials = new AWS.Credentials(c.AccessKeyId, c.SecretKey, c.SessionToken)
        callback(data)
      }
    })
}

function DynamoDBGetGame(game_id, callback_fn) {
    console.log("DynamoDBGetItem")
    console.log(awsCredentials)
    const dynamoService = new AWS.DynamoDB(
        {region: region,
        credentials: awsCredentials})
    dynamoService.getItem(
        {TableName: "glory-of-empires",
        Key: {id: {"S": game_id}}},
         function(err, data) {
            if (err) {
                 console.log(err, err.stack); // an error occurred
            } else {
                const json = AWS.DynamoDB.Converter.unmarshall(data.Item)
                callback_fn(json);           // successful response)
            }
        }
    )
}
