package example.client.jmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class GeodeJmxNotificationListener implements NotificationListener, JmxConnectionHandler, JmxNotificationsAccessor {

  @Value("${jmx.listener.manager.host.name:localhost}")
  private String hostName;

  @Value("${jmx.listener.manager.port:1099}")
  private int port;

  private volatile boolean connected = false;

  private MBeanServerConnection serverConnection;

  private ObjectName distributedSystem;

  private final List<Notification> notifications;

  private final List<JmxNotificationHandler> notificationHandlers;

  private static final String CONNECTION_URL = "service:jmx:rmi://%s/jndi/rmi://%s:%d/jmxrmi";

  private static final String DISTRIBUTED_SYSTEM_MBEAN_NAME = "GemFire:service=System,type=Distributed";

  private static final Logger logger = LoggerFactory.getLogger(GeodeJmxNotificationListener.class);

  @Value("${jmx.listener.wait.time:5000}")
  private long waitTime;

  @Autowired
  public GeodeJmxNotificationListener(List<JmxNotificationHandler> notificationHandlers) {
    this.notificationHandlers = notificationHandlers;
    this.notifications = new CopyOnWriteArrayList<>();
  }

  @Override
  @Async
  public void connectToMBeanServer() throws Exception {
    while (true) {
      try {
        attemptToConnectToMBeanServer();
        break;
      } catch (Exception e) {
        logger.warn("Caught the following exception attempting to connect to the JMX manager at {}:{}. Will retry in {} ms.", this.hostName, this.port, this.waitTime, e);
        Thread.sleep(this.waitTime);
      }
    }

    // Change alert level
    changeAlertLevel();

    // Add notification listener
    addNotificationListener();
  }

  private void attemptToConnectToMBeanServer() throws Exception {
    // Create JMXServiceURL
    String urlStr = String.format(CONNECTION_URL, this.hostName, this.hostName, this.port);
    JMXServiceURL url = new JMXServiceURL(urlStr);
    logger.info("Attempting to connect to {}", url);

    // Create the JMXConnector
    JMXConnector jmxConnector = JMXConnectorFactory.connect(url);
    logger.info("Connected to {}", url);

    // Get the MBeanServerConnection
    this.serverConnection = jmxConnector.getMBeanServerConnection();
    this.connected = true;

    // Initialize the system mbean name
    this.distributedSystem = new ObjectName(DISTRIBUTED_SYSTEM_MBEAN_NAME);
    logger.info("Monitoring {}", this.distributedSystem);
  }

  private void changeAlertLevel() throws Exception {
    this.serverConnection.invoke(this.distributedSystem, "changeAlertLevel", new String[] {"warning"}, new String[] {"java.lang.String"});
  }

  private void addNotificationListener() throws Exception {
    this.serverConnection.addNotificationListener(this.distributedSystem, this, null, null);
  }

  @Override
  @Scheduled(fixedDelayString = "${jmx.listener.check.connection.delay:1000}")
  public void checkConnection() {
    if (this.connected) {
      // Verify the default domain is accessible.
      // See ClientCommunicatorAdmin checkConnection and RemoteMBeanServerConnection getDefaultDomain.
      try {
        this.serverConnection.getDefaultDomain();
      } catch (Exception e) {
        logger.warn("Lost the connection to the JMX manager and will attempt to reconnect:", e);
        this.connected = false;
        try {
          connectToMBeanServer();
        } catch (Exception e2) {
          logger.warn("Caught the following exception attempting to reconnect to the JMX manager:", e2);
        }
      }
    }
  }

  @Override
  public void handleNotification(Notification notification, Object handback) {
    this.notifications.add(notification);
    this.notificationHandlers.forEach(handler -> handler.handleNotification(notification, handback));
  }

  @Override
  public List<Notification> getNotifications() {
    return this.notifications;
  }

  @Override
  @Scheduled(fixedDelayString = "${jmx.listener.clear.notifications.delay:86400000}")
  public void clearNotifications() {
    logger.info("Clearing {} notifications", this.notifications.size());
    this.notifications.clear();
  }

  public String toString() {
    return new StringBuilder()
      .append(getClass().getSimpleName())
      .append("[")
      .append("hostName=")
      .append(this.hostName)
      .append("; port=")
      .append(this.port)
      .append("]")
      .toString();
  }
}
