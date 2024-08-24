# Deploy UI to S3 bucket. Separate deployment for Websocker / DynamoDB backend in backend-folder
npm install
Remove-Item -Path ".\resources\public\js\compiled" -Recurse
npm run release
# S3 deployment not compatible with cognito
# aws s3 cp .\resources\public  s3://glory-of-empires/ --recursive
Remove-Item -Recurse -Force .\backend\staticfiles\files\
Copy-Item -Path ".\resources\public" -Destination ".\backend\staticfiles\files" -Recurse -Force
cd .\backend
# Deploys to 'staticfiles' lambda at URL https://3jcv5yd21l.execute-api.eu-north-1.amazonaws.com/prod/
.\deploy-backend.ps1
cd ..