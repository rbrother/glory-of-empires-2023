# Deploy UI to S3 bucket. Separate deployment for Websocker / DynamoDB backend in backend-folder
npm install
Remove-Item -Path ".\resources\public\js\compiled" -Recurse
npm run release
# Publish to http://glory-of-empires.s3.eu-north-1.amazonaws.com/
# THIS IS NOT COMPATIBLE WITH COGNITO, WHICH REQURES HTTPS REDIRECT URL
aws s3 cp .\resources\public  s3://glory-of-empires/ --recursive
