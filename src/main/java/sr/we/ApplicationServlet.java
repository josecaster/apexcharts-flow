package sr.we;

import com.vaadin.flow.server.InitParameters;
import com.vaadin.flow.server.VaadinServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/*@WebServlet(urlPatterns = "/*", name = "app", asyncSupported = true, loadOnStartup = 1,
        initParams = {
                 })*/
@WebServlet(urlPatterns = "/*", name = "slot", asyncSupported = true,loadOnStartup = 1, initParams = {//
        @WebInitParam(name = InitParameters.I18N_PROVIDER, value = "sr.we.TranslationProvider"),//
        @WebInitParam(name = InitParameters.SERVLET_PARAMETER_HEARTBEAT_INTERVAL, value = "10"),//
        @WebInitParam(name = InitParameters.SERVLET_PARAMETER_CLOSE_IDLE_SESSIONS, value = "true")})
public class ApplicationServlet extends VaadinServlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
    }
}