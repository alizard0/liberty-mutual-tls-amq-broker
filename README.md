# README
This projects runs webserver that connects against an AMQ Broker using CORE protocol.

## Create ssl folder on this project
```
$ mkdir -p -- ssl/client ssl/server
```

## Change {{ADD-FULL-PATH}} variable at src/main/liberty/config/server.xml

## Broker Configuration
Add the following acceptor to your broker.xml
```
<acceptor name="artemis">tcp://0.0.0.0:61717?tcpSendBufferSize=1048576;tcpReceiveBufferSize=1048576;amqpMinLargeMessageSize=102400;protocols=CORE,AMQP,STOMP,HORNETQ,MQTT,OPENWIRE;useEpoll=true;amqpCredits=1000;amqpLowCredits=300;amqpDuplicateDetection=true;supportAdvisory=false;suppressInternalManagementObjects=false;sslEnabled=true;keyStorePath=/path/liberty-mutual-tls-amq-core/ssl/server/server.ks;keyStorePassword=123456;trustStorePath=/path/liberty-mutual-tls-amq-core/ssl/server/server.ts;trustStorePassword=123456;needClientAuth=true</acceptor>
```

## Setup Mutual TLS
1. Generate CA certificate
```
$ openssl genrsa -out ca.key 2048
$ openssl req -new -x509 -days 1826 -key ca.key -out ca.crt
```
2. Generate Server Key
```
$ openssl genrsa -out server.key 2048
```
3. Generate Server Certificate
```
$ openssl req -new -out server.csr -key server.key
$ openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out server.crt -days 360
```

4. Import Certificate and Private Key on Server KeyStore
```
$ keytool -import -file ca.crt -alias myCA -keystore server.ks
$ keytool -import -file server.crt -alias myServerCertificate -keystore server.ks
$ openssl pkcs12 -export -in server.crt -inkey server.key -out serverStore.p12 -CAfile ca.crt
$ keytool -importkeystore -srckeystore serverStore.p12 -destkeystore server.ks -srcstoretype pkcs12
```

5. Generate Client Key
```
$ openssl genrsa -out client.key 2048
```

7. Generate Client Certificate
```
$ openssl req -new -out client.csr -key client.key
$ openssl x509 -req -in client.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out client.crt -days 360
```

7. Import Client CA Certificate on Server TrustStore
```
$ keytool -import -file client.crt -alias myCA -keystore ../server/server.ts
```

8. Import Certificate and Private Key on Client KeyStore
```
$ keytool -import -file ca.crt -alias myCA -keystore client.ks
$ keytool -import -file client.crt -alias myClientCertificate -keystore client.ks
$ openssl pkcs12 -export -in client.crt -inkey client.key -out clientStore.p12 -CAfile ca.crt
$ keytool -importkeystore -srckeystore clientStore.p12 -destkeystore client.ks -srcstoretype pkcs12
```

9. Import Server CA Certificate on Client TrustStore
```
$ keytool -import -file server.crt -alias myCA -keystore ../client/client.ts
```

## Run the project
```
$ mvn liberty:run
```

## Access the endpoint
http://192.168.1.69:9080/LibertyMutualTlsForAMQBroker/
