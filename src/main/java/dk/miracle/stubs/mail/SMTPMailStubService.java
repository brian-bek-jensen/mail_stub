package dk.miracle.stubs.mail;


import org.apache.log4j.Logger;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

/**
 * Simple mail stub that lets users list the messages that have been sent to the SMTP server.
 */
public class SMTPMailStubService implements SMTPMailStubServiceMBean {
    private static final org.apache.log4j.Logger logger = Logger.getLogger(SMTPMailStubService.class);

    Wiser wiser = new Wiser();
    private Integer port = 2552;

    public String listMessages() {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering list messages...");
        }
        StringBuffer result = new StringBuffer();
        List<WiserMessage> messages = wiser.getMessages();
        for (WiserMessage message : messages) {

            try {
                MimeMessage mess = message.getMimeMessage();
                String url = String.format("/jmx-console/HtmlAdaptor?action=invokeOpByName&name=miracle.jbossSMTPTestService:service=JBossSMTPTestService&methodName=showMessage&arg0=%s&argType=java.lang.String", URLEncoder.encode(mess.getMessageID(), "UTF-8"));
                result.append(String.format("<a href=\"%s\">Subject:%s | Recipients:%s</a><br/>", url, mess.getSubject(), Arrays.deepToString((mess.getAllRecipients()))));
            } catch (MessagingException e) {
                logger.error("Messaging exception caught in list messages.", e);
            } catch (UnsupportedEncodingException e) {
                logger.error("Unsupported encoding exception caught in list messages.", e);
            }
        }
        String url = String.format("/jmx-console/HtmlAdaptor?action=invokeOpByName&name=miracle.jbossSMTPTestService:service=JBossSMTPTestService&methodName=deleteMessages");
        result.append("<br/><a href=\"" + url + "\">Clear messages</a>");
        return result.toString();
    }

    public String showMessage(String id) {
        if (logger.isDebugEnabled()) {
            logger.debug("The method show messages have been called with id = " + (id == null ? "NULL" : id));
        }
        StringBuffer result = new StringBuffer();
        for (WiserMessage message : wiser.getMessages()) {

            try {
                MimeMessage mess = message.getMimeMessage();
                if (mess.getMessageID().equals(id)) {
                    try {
                        result.append(String.format("ID:%s<br>Subject:%s<br>Recipients:%s<br>Message:%s", mess.getMessageID(), mess.getSubject(), Arrays.deepToString(mess.getAllRecipients()), mess.getContent().toString()));
                    } catch (IOException e) {
                        logger.error("IO exception caught in show message.", e);
                    }
                    String url = String.format("/jmx-console/HtmlAdaptor?action=invokeOpByName&name=miracle.jbossSMTPTestService:service=JBossSMTPTestService&methodName=listMessages");
                    result.append("<br/><a href=\"" + url + "\">All messages</a>");
                }
            } catch (MessagingException e) {
                logger.error("Messaging exception caught in show message.", e);
            }
        }
        return result.toString();
    }

    public String deleteMessages() {
        if (logger.isDebugEnabled()) {
            logger.debug("The method delete messages have been called.");
        }
        StringBuffer result = new StringBuffer();
        stopServer();
        result.append("Server stopped<br/>");
        wiser = new Wiser();
        result.append("reinitialized<br/>");

        wiser.setPort(getPort());
        wiser.start();
        result.append("Server started<br/>");

        String url = String.format("/jmx-console/HtmlAdaptor?action=invokeOpByName&name=miracle.jbossSMTPTestService:service=JBossSMTPTestService&methodName=listMessages");
        result.append("<br/><a href=\"" + url + "\">All messages</a>");
        return result.toString();
    }

    public String restartServer() {
        if (logger.isDebugEnabled()) {
            logger.debug("The restart server method have been called.");
        }
        StringBuffer buf = new StringBuffer();
        buf.append(stopServer());
        buf.append("<br/>");
        buf.append(startServer());
        return buf.toString();
    }

    public String restartServer(Integer port) {
        if (logger.isDebugEnabled()) {
            logger.debug("The restart server method have been called with a port parameter.");
            if (port != null) {
                logger.debug("The port is = " + port.toString());
            }
        }
        if (port == null) {
            String error = "In restart server method the parameter port is undefined. Server not started.";
            logger.error(error);
            return error;
        }
        setPort(port);
        return restartServer();
    }

    public String stopServer() {
        if (logger.isDebugEnabled()) {
            logger.debug("The stop server method have been called.");
        }
        wiser.stop();
        return "server stopped";
    }

    public String startServer(Integer port) {
        if (logger.isDebugEnabled()) {
            logger.debug("The start server method have been called with a port parameter.");
        }
        if (port == null) {
            String error = "In start server method the parameter port is undefined. Server not started.";
            logger.error(error);
            return error;
        }
        setPort(port);
        return startServer();
    }


    public String startServer() {
        if (logger.isDebugEnabled()) {
            logger.debug("The start server method have been called.");
        }
        if (getPort() == null) {
            String error = "In start server method the port is undefined. Server not started.";
            logger.error(error);
            return error;
        }
        wiser.setPort(Integer.valueOf(getPort()));
        wiser.start();
        return "server started";
    }

    public void start() throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("The start method have been called.");
        }
        startServer(getPort());
    }


    public void stop() {
        if (logger.isDebugEnabled()) {
            logger.debug("The stop method have been called...");
        }
        stopServer();
    }

    public Integer getPort() {
        if (logger.isDebugEnabled()) {
            logger.debug("Getting the porty value for the smtp server.");
        }
        return port;
    }

    public void setPort(Integer port) {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering set port.");
            if (port != null) {
                logger.debug("The port value is = " + port.toString());
            }
        }
        if (port == null) {
            logger.error("You are trying to set the port to NULL.");
        }
        this.port = port;
    }
}
