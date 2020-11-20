# RUNUI - README #

## Create Image ##

`docker build -t runui .`

## Run a Container ##

`docker run --name runui -p 8080:8080 -v "/opt/runui/config:/app/config" runui`
