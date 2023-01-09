package sr.we;

import org.springframework.stereotype.Component;
import sr.we.data.controller.ImagesService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("vaadinImageServlet")
@WebServlet(urlPatterns = "/myimages", name = "app images", loadOnStartup = 1)
public class ServletImage extends HttpServlet {

    public ServletImage() {
        System.out.println("Here Images");
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("image/svg+xml");
        String reference = req.getParameter("reference");
        String referenceId = req.getParameter("referenceId");
        if (reference == null) {
            reference = "";
        }
        if (referenceId == null) {
            referenceId = "";
        }

        ImagesService businessService = ContextProvider.getBean(ImagesService.class);
        byte[] bytes = businessService.get(referenceId, reference);
        resp.getWriter().write(new String(bytes));
    }


}