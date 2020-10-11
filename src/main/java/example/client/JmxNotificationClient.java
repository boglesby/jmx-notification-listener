package example.client;

import example.client.jmx.JmxConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.geode.boot.autoconfigure.ClientCacheAutoConfiguration;
import org.springframework.geode.boot.autoconfigure.ClientSecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

// Note: No client cache is necessary, but the geode-core.jar is required on the classpath
// so that org.apache.geode.management.internal.ContextAwareSSLRMIClientSocketFactory is available
@SpringBootApplication(exclude = {ClientCacheAutoConfiguration.class, ClientSecurityAutoConfiguration.class})
@EnableAsync
@EnableScheduling
public class JmxNotificationClient {

  @Autowired
  private JmxConnectionHandler jmxConnectionHandler;

  private static final Logger logger = LoggerFactory.getLogger(JmxNotificationClient.class);

  public static void main(String[] args) {
    SpringApplication.run(JmxNotificationClient.class, args);
  }

  @Bean
  void connectJmxConnectionHandler() {
    try {
      this.jmxConnectionHandler.connectToMBeanServer();
    } catch (Exception e) {
      logger.warn("The JmxConnectionHandler failed to start listener due to:", e);
    }
  }
}
