package dk.miracle.stubs.mail;


/**
 * Add description..
 * <p/>
 * User: Morten Hauch
 * Date: Feb 24, 2009
 * Time: 8:17:58 AM
 */
public interface SMTPMailStubServiceMBean  {
     String listMessages();
    String deleteMessages();
    String restartServer(Integer port);
    String stopServer();
    String startServer(Integer port);
    Integer getPort();
    void setPort(Integer port);

    void start() throws Exception;

    void stop();

    String restartServer();

    String startServer();

    String showMessage(String id);
}
