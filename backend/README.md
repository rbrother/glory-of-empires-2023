# simple-websockets-chat-app

**Update**: *We have released an updated and more 
feature-rich Websocket chat application sample 
using AWS CDK that you can find [here](https://github.com/aws-samples/websocket-chat-application).*

This is the code and template for the simple-websocket-chat-app.
There are three functions contained within the directories and a SAM template that wires them up to a DynamoDB table and provides the minimal set of permissions needed to run the app:

```
.
├── README.md                   <-- This instructions file
├── onconnect                   <-- Source code onconnect
├── ondisconnect                <-- Source code ondisconnect
├── sendmessage                 <-- Source code sendmessage
└── template.yaml               <-- SAM template for Lambda Functions and DDB
```


# Deploying to your account

You can install the [AWS SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html) and use it to package, deploy, and describe your application.  These are the commands you'll need to use:

```
sam deploy --guided

aws cloudformation describe-stacks \
    --stack-name simple-websocket-chat-app --query 'Stacks[].Outputs'
```

**Note:** `.gitignore` contains the `samconfig.toml`, hence make sure backup this file, or modify your .gitignore locally.
