package example.client.jmx;

import javax.management.Notification;
import java.util.List;

public interface JmxNotificationsAccessor {

  List<Notification> getNotifications();

  void clearNotifications();
}
