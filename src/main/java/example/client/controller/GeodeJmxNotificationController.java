package example.client.controller;

import example.client.jmx.JmxNotificationsAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.management.Notification;
import java.util.List;

@RestController
public class GeodeJmxNotificationController {

  @Autowired
  private JmxNotificationsAccessor accessor;

  @GetMapping("/getnotifications")
  public List<Notification> getNotifications() {
    return this.accessor.getNotifications();
  }

  @PostMapping("/clearnotifications")
  public void clearNotifications() {
    this.accessor.clearNotifications();
  }
}
