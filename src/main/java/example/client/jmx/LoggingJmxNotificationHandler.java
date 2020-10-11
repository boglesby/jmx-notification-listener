package example.client.jmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.management.Notification;

@Service
public class LoggingJmxNotificationHandler implements JmxNotificationHandler {

  private static final Logger logger = LoggerFactory.getLogger(LoggingJmxNotificationHandler.class);

  @Override
  public void handleNotification(Notification notification, Object handback) {
    logger.info(toString(notification));
  }

  private String toString(Notification notification) {
    return new StringBuilder()
      .append(getClass().getSimpleName())
      .append(" received notification[")
      .append("source=")
      .append(notification.getSource())
      .append("; type=")
      .append(notification.getType())
      .append("; sequence number=")
      .append(notification.getSequenceNumber())
      .append("; time stamp=")
      .append(notification.getTimeStamp())
      .append("; message=")
      .append(notification.getMessage())
      .append("; userData=")
      .append(notification.getUserData())
      .append("]")
      .toString();
  }
}
