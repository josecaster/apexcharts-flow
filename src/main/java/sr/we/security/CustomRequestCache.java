package sr.we.security;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class CustomRequestCache extends HttpSessionRequestCache {

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {

        if (!SecurityConfiguration.isFrameworkInternalRequest(request)) {
            super.saveRequest(request, response);
        }
    }
}