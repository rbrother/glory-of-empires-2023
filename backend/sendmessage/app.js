// Copyright 2018-2020Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

const AWS = require('aws-sdk');

const ddb = new AWS.DynamoDB.DocumentClient({ apiVersion: '2012-08-10', region: process.env.AWS_REGION });

const { TABLE_NAME } = process.env;

exports.handler = async event => {
  let connectionData;
  
  try {
    connectionData = await ddb.scan({ TableName: TABLE_NAME, ProjectionExpression: 'connectionId' }).promise();
  } catch (e) {
    return { statusCode: 500, body: e.stack };
  }

  const senderConnectionId = event.requestContext.connectionId
  
  const apigwManagementApi = new AWS.ApiGatewayManagementApi({
    apiVersion: '2018-11-29',
    endpoint: event.requestContext.domainName + '/' + event.requestContext.stage
  });
  
  const inputData = JSON.parse(event.body)
  // Here we could modify the message going to clients, now we pass on the message from one client to all as-is
  const postData = JSON.stringify(inputData)
  
  const postCalls = connectionData.Items.map(async ({ connectionId }) => {
    try {
      if (connectionId != senderConnectionId) {
          console.log("Sending client message: ", connectionId, postData)
          await apigwManagementApi.postToConnection({ ConnectionId: connectionId, Data: postData }).promise();
      }
    } catch (e) {
      if (e.statusCode === 410) {
        console.log(`Found stale connection, deleting ${connectionId}`);
        await ddb.delete({ TableName: TABLE_NAME, Key: { connectionId } }).promise();
      } else {
        console.log(e)
        throw e
      }
    }
  })
  
  try {
    await Promise.all(postCalls);
  } catch (e) {
    return { statusCode: 500, body: e.stack };
  }
  console.log("All Messages sent")
  return { statusCode: 200, body: 'Data sent.' };
};
