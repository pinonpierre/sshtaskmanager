# SSH Task Manager - README #

## Create Image ##

`docker build -t sshtaskmanager .`

## Run a Container ##

`docker run --name sshtaskmanager -p 8080:8080 -v "/opt/sshtaskmanager/config:/app/config" sshtaskmanager`
