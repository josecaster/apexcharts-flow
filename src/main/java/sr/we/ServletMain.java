package sr.we;

import com.vaadin.flow.server.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

//@Component("vaadinServlet")
@WebServlet(urlPatterns = "/VAADIN/*", name = "MyUIServlet", asyncSupported = true)
//@VaadinServletConfiguration(ui = VaadinUI.class, productionMode = false)
public class ServletMain extends VaadinServlet implements SessionInitListener, SessionDestroyListener {

    public ServletMain() {
        System.out.println("Here");
    }

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        getService().addSessionInitListener(this);
        getService().addSessionDestroyListener(this);

    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
    }

    @Override
    public void sessionDestroy(SessionDestroyEvent sessionDestroyEvent) {

    }

    @Override
    public void sessionInit(SessionInitEvent sessionInitEvent) throws ServiceException {

    }
}