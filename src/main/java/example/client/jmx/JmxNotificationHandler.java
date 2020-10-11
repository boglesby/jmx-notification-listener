package example.client.jmx;

import javax.management.Notification;

public interface JmxNotificationHandler {

  void handleNotification(Notification notification, Object handback);
}
