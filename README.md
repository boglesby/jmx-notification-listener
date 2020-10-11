# JMX Noticification Listener
## Description

This project provides a Spring Boot JMX Notification listener application that connects to and listens for JMX Notifications from Apache Geode members.

The **GeodeJmxNotificationListener**:

- connects to the MBeanServer running in the JMX manager (the locator)
- changes the DistributedSystemMXBean's alert level from *severe* (the default) to *warning*
- adds itself as a NotificationListener to the DistributedSystemMXBean
- handles Notifications by handing them off to a **JmxNotificationHandler** for processing

This project provides one **JmxNotificationHandler** called **LoggingJmxNotificationHandler** which just logs the Notification.

Some example Notifications are:

#### system.alerts
**DiskStoreMonitor disk usage warning:**

```
"source": "DistributedSystem(1)",
"type": "system.alert",
"sequenceNumber": 12,
"timeStamp": 1601942062695,
"userData": {
  "AlertLevel": "warning",
  "Member": "server-1",
  "Thread": "DiskStoreMonitor1 tid=0x2f"
},
"message": "The disk volume . for disk store DEFAULT has exceeded the warning usage threshold and is 95.2% full"
```
**GatewaySender remote locator warning:**

```
"source": "DistributedSystem(1)",
"type": "system.alert",
"sequenceNumber": 21,
"timeStamp": 1601942071601,
"userData": {
  "AlertLevel": "warning",
  "Member": "server-1",
  "Thread": "Event Processor for GatewaySender_ny_0 tid=0x43"
},
"message": "Remote locator host port information for remote site 2 is not available in local locator 192.168.1.10[10334]."
```
#### gemfire.distributedsystem.*
**gemfire.distributedsystem.cache.region.created:**

```
"source": "server-1",
"type": "gemfire.distributedsystem.cache.region.created",
"sequenceNumber": 59,
"timeStamp": 1601942089284,
"userData": "server-1",
"message": "Region Created With Name /PartitionedTrade"
```
**gemfire.distributedsystem.cache.member.joined:**

```
"source": "server-1",
"type": "gemfire.distributedsystem.cache.member.joined",
"sequenceNumber": 13,
"timeStamp": 1601942068493,
"userData": null,
"message": "Member Joined server-1"
```
**gemfire.distributedsystem.cache.member.suspect**:

```
"source": "server-1",
"type": "gemfire.distributedsystem.cache.member.suspect",
"sequenceNumber": 65,
"timeStamp": 1601942305311,
"userData": null,
"message": "Member Suspected server-1 By : locator"
```
**gemfire.distributedsystem.cache.member.departed**:

```
"source": "server-1",
"type": "gemfire.distributedsystem.cache.member.departed",
"sequenceNumber": 68,
"timeStamp": 1601942310608,
"userData": null,
"message": "Member Departed server-2 has crashed = true"
```
## Initialization
Modify the **GEODE** environment variable in the *setenv.sh* script to point to a Geode installation directory.
## Build
Build the Spring Boot JMX Notification listener application using gradle like:

```
./gradlew clean bootJar
```
## Start Spring Boot JMX Notification Listener Application
Note: Starting and configuring a DistributedSystem is not part of this example. Use one of my other examples to do that.

Start the Spring Boot JMX Notification listener application using *gradlew* script like:

```
./gradlew bootRun
```
An attempt will be made every 5 seconds to connect to the JMX manager.

Output like this will be logged if the connection to cannot be made:

```
2020-10-11 07:55:01.555  INFO 1352 --- [         task-1] e.c.jmx.GeodeJmxNotificationListener     : Attempting to connect to service:jmx:rmi://localhost/jndi/rmi://localhost:1099/jmxrmi
2020-10-11 07:55:01.559  WARN 1352 --- [         task-1] e.c.jmx.GeodeJmxNotificationListener     : Caught the following exception attempting to connect to the JMX manager at localhost:1099. Will retry in 5000 ms.

java.io.IOException: Failed to retrieve RMIServer stub: javax.naming.ServiceUnavailableException [Root exception is java.rmi.ConnectException: Connection refused to host: localhost; nested exception is: 
        java.net.ConnectException: Connection refused (Connection refused)]
        at javax.management.remote.rmi.RMIConnector.connect(RMIConnector.java:369) ~[na:1.8.0_151]
        at javax.management.remote.JMXConnectorFactory.connect(JMXConnectorFactory.java:270) ~[na:1.8.0_151]
        at javax.management.remote.JMXConnectorFactory.connect(JMXConnectorFactory.java:229) ~[na:1.8.0_151]
        at example.client.jmx.GeodeJmxNotificationListener.attemptToConnectToMBeanServer(GeodeJmxNotificationListener.java:80) ~[main/:na]
        at example.client.jmx.GeodeJmxNotificationListener.connectToMBeanServer(GeodeJmxNotificationListener.java:58) ~[main/:na]
Caused by: javax.naming.ServiceUnavailableException: null
        at com.sun.jndi.rmi.registry.RegistryContext.lookup(RegistryContext.java:136) ~[na:1.8.0_151]
        at com.sun.jndi.toolkit.url.GenericURLContext.lookup(GenericURLContext.java:205) ~[na:1.8.0_151]
        at javax.naming.InitialContext.lookup(InitialContext.java:417) ~[na:1.8.0_151]
        at javax.management.remote.rmi.RMIConnector.findRMIServerJNDI(RMIConnector.java:1955) ~[na:1.8.0_151]
        at javax.management.remote.rmi.RMIConnector.findRMIServer(RMIConnector.java:1922) ~[na:1.8.0_151]
        at javax.management.remote.rmi.RMIConnector.connect(RMIConnector.java:287) ~[na:1.8.0_151]
Caused by: java.rmi.ConnectException: Connection refused to host: localhost; nested exception is: 
        java.net.ConnectException: Connection refused (Connection refused)
        at sun.rmi.transport.tcp.TCPEndpoint.newSocket(TCPEndpoint.java:619) ~[na:1.8.0_151]
        at sun.rmi.transport.tcp.TCPChannel.createConnection(TCPChannel.java:216) ~[na:1.8.0_151]
        at sun.rmi.transport.tcp.TCPChannel.newConnection(TCPChannel.java:202) ~[na:1.8.0_151]
        at sun.rmi.server.UnicastRef.newCall(UnicastRef.java:338) ~[na:1.8.0_151]
        at sun.rmi.registry.RegistryImpl_Stub.lookup(RegistryImpl_Stub.java:112) ~[na:1.8.0_151]
        at com.sun.jndi.rmi.registry.RegistryContext.lookup(RegistryContext.java:132) ~[na:1.8.0_151]
Caused by: java.net.ConnectException: Connection refused (Connection refused)
        at java.net.PlainSocketImpl.socketConnect(Native Method) ~[na:1.8.0_151]
        at java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:350) ~[na:1.8.0_151]
        at java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.java:206) ~[na:1.8.0_151]
        at java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:188) ~[na:1.8.0_151]
        at java.net.SocksSocketImpl.connect(SocksSocketImpl.java:392) ~[na:1.8.0_151]
        at java.net.Socket.connect(Socket.java:589) ~[na:1.8.0_151]
        at java.net.Socket.connect(Socket.java:538) ~[na:1.8.0_151]
        at java.net.Socket.<init>(Socket.java:434) ~[na:1.8.0_151]
        at java.net.Socket.<init>(Socket.java:211) ~[na:1.8.0_151]
        at sun.rmi.transport.proxy.RMIDirectSocketFactory.createSocket(RMIDirectSocketFactory.java:40) ~[na:1.8.0_151]
        at sun.rmi.transport.proxy.RMIMasterSocketFactory.createSocket(RMIMasterSocketFactory.java:148) ~[na:1.8.0_151]
        at sun.rmi.transport.tcp.TCPEndpoint.newSocket(TCPEndpoint.java:613) ~[na:1.8.0_151]
```
Output like this will be logged once the connection is made:

```
2020-10-11 07:55:06.563  INFO 1352 --- [         task-1] e.c.jmx.GeodeJmxNotificationListener     : Attempting to connect to service:jmx:rmi://localhost/jndi/rmi://localhost:1099/jmxrmi
2020-10-11 07:55:06.617  INFO 1352 --- [         task-1] e.c.jmx.GeodeJmxNotificationListener     : Connected to service:jmx:rmi://localhost/jndi/rmi://localhost:1099/jmxrmi
2020-10-11 07:55:06.617  INFO 1352 --- [         task-1] e.c.jmx.GeodeJmxNotificationListener     : Monitoring GemFire:service=System,type=Distributed
```
If the connection is lost, output like this will be logged, and the connection will be re-attempted:

```
2020-10-11 08:14:57.734  WARN 1870 --- [   scheduling-1] e.c.jmx.GeodeJmxNotificationListener     : Lost the connection to the JMX manager and will attempt to reconnect:

java.rmi.NoSuchObjectException: no such object in table
        at sun.rmi.transport.StreamRemoteCall.exceptionReceivedFromServer(StreamRemoteCall.java:283) ~[na:1.8.0_151]
        at sun.rmi.transport.StreamRemoteCall.executeCall(StreamRemoteCall.java:260) ~[na:1.8.0_151]
        at sun.rmi.server.UnicastRef.invoke(UnicastRef.java:161) ~[na:1.8.0_151]
        at com.sun.jmx.remote.internal.PRef.invoke(Unknown Source) ~[na:na]
        at javax.management.remote.rmi.RMIConnectionImpl_Stub.getDefaultDomain(Unknown Source) ~[na:1.8.0_151]
        at javax.management.remote.rmi.RMIConnector$RemoteMBeanServerConnection.getDefaultDomain(RMIConnector.java:1045) ~[na:1.8.0_151]
        at example.client.jmx.GeodeJmxNotificationListener.checkConnection(GeodeJmxNotificationListener.java:107) ~[main/:na]
```
Or:

```
2020-10-11 08:17:25.226  WARN 1906 --- [   scheduling-1] e.c.jmx.GeodeJmxNotificationListener     : Lost the connection to the JMX manager and will attempt to reconnect:

java.rmi.ConnectException: Connection refused to host: 192.168.1.10; nested exception is: 
        java.net.ConnectException: Connection refused (Connection refused)
        at sun.rmi.transport.tcp.TCPEndpoint.newSocket(TCPEndpoint.java:619) ~[na:1.8.0_151]
        at sun.rmi.transport.tcp.TCPChannel.createConnection(TCPChannel.java:216) ~[na:1.8.0_151]
        at sun.rmi.transport.tcp.TCPChannel.newConnection(TCPChannel.java:202) ~[na:1.8.0_151]
        at sun.rmi.server.UnicastRef.invoke(UnicastRef.java:129) ~[na:1.8.0_151]
        at com.sun.jmx.remote.internal.PRef.invoke(Unknown Source) ~[na:na]
        at javax.management.remote.rmi.RMIConnectionImpl_Stub.getDefaultDomain(Unknown Source) ~[na:1.8.0_151]
        at javax.management.remote.rmi.RMIConnector$RemoteMBeanServerConnection.getDefaultDomain(RMIConnector.java:1045) ~[na:1.8.0_151]
        at example.client.jmx.GeodeJmxNotificationListener.checkConnection(GeodeJmxNotificationListener.java:107) ~[main/:na]
Caused by: java.net.ConnectException: Connection refused (Connection refused)
        at java.net.PlainSocketImpl.socketConnect(Native Method) ~[na:1.8.0_151]
        at java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:350) ~[na:1.8.0_151]
        at java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.java:206) ~[na:1.8.0_151]
        at java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:188) ~[na:1.8.0_151]
        at java.net.SocksSocketImpl.connect(SocksSocketImpl.java:392) ~[na:1.8.0_151]
```