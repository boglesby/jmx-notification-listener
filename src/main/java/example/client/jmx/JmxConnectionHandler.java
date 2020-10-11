package example.client.jmx;

public interface JmxConnectionHandler {

  void connectToMBeanServer() throws Exception;

  void checkConnection();
}
