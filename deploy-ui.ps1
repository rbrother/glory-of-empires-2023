# S3 deployment not compatible with cognito, so deploy as part of Lambda files and serve from lambda
npm install
Remove-Item -Path ".\backend\staticfiles\files\js\compiled" -Recurse
npm run release
cd .\backend
# Deploys to 'staticfiles' lambda at URL https://3jcv5yd21l.execute-api.eu-north-1.amazonaws.com/prod/
sam deploy
cd ..