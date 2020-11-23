# SSH Task Manager - README #

## Create Image ##

`docker build -t xesnet/sshtaskmanager .`

## Pull Docker Image ##

`docker pull xesnet/sshtaskmanager`

## Run a Container ##

`docker run --name sshtaskmanager -p 8080:8080 -v "/opt/sshtaskmanager/config:/app/config" xesnet/sshtaskmanager`

## Configuration ##

Default `config.yaml` file will be created the first startup.

```
---
host: null                  #0.0.0.0 - Server Host
port: 8080                  #Server Port
logLevel: "WARNING"         #OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL - Log Level
tokenTimeout: 1800          #Token Timeout (in seconds)
runManager:
  numberOfThreads: 16       #Number of Threads for Execution and Monitoring
  statusPollInterval: 100   #Time between Status Poll check (in milliseconds)
  timeout: 300              #Monitoring Timeout (in seconds)
  cleanInterval: 300        #Clean Interval (in seconds)
  retention: 1800           #Run Retention (in seconds)
```

## Security ##

Default `users.yaml` file will be created the first startup.

```
users:
- login: "admin"
  password: "admin"
  admin: true
- login: "guest"
  password: "password"
```

## Invalid Private Key Error ##

Error: "invalid privatekey: [B@59c40796"

Recent versions of OpenSSH (7.8 and newer) generate keys in new OpenSSH format by default, which start with:
**-----BEGIN OPENSSH PRIVATE KEY-----**

To generate supported key: `ssh-keygen -t rsa -m PEM`

Or ro convert existing private key: `ssh-keygen -p -f file -m pem -P passphrase -N passphrase`
