package org.apache.catalina.ext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Created by dinghui on 14-4-24.
 */
public class TypeFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;

        String responseType = response.getContentType();
        if(!getAllowTypes().contains(responseType)){
            TypeResponseWrapper wrappedResponse = new TypeResponseWrapper(response);
            chain.doFilter(request, wrappedResponse);
            wrappedResponse.setContentLength();
        }else{
            chain.doFilter(request, response);
        }
    }

    private ArrayList<String> getAllowTypes(){
        return new ArrayList<String>();
    }

    @Override
    public void destroy() {

    }
}

class TypeResponseWrapper extends HttpServletResponseWrapper{
    private NoBodyOutputStream noBody;
    private PrintWriter writer;
    private boolean didSetContentLength;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response
     * @throws IllegalArgumentException if the response is null
     */
    public TypeResponseWrapper(HttpServletResponse response) {
        super(response);
        noBody = new NoBodyOutputStream();
    }

    @Override
    public void setContentLength(int len) {
        super.setContentLength(len);
        didSetContentLength = true;
    }

    @Override
    public void setHeader(String name, String value) {
        super.setHeader(name, value);
        checkHeader(name);
    }

    @Override
    public void addHeader(String name, String value) {
        super.addHeader(name, value);
        checkHeader(name);
    }

    @Override
    public void setIntHeader(String name, int value) {
        super.setIntHeader(name, value);
        checkHeader(name);
    }

    @Override
    public void addIntHeader(String name, int value) {
        super.addIntHeader(name, value);
        checkHeader(name);
    }

    void setContentLength() {
        if (!didSetContentLength) {
            if (writer != null) {
                writer.flush();
            }
            super.setContentLength(noBody.getContentLength());
        }
    }

    private void checkHeader(String name){
        if ("content-length".equalsIgnoreCase(name)) {
            didSetContentLength = true;
        }
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException{
        return noBody;
    }

    @Override
    public PrintWriter getWriter() throws UnsupportedEncodingException{
        if(writer == null){
            OutputStreamWriter w;

            w = new OutputStreamWriter(noBody, getCharacterEncoding());
            writer = new PrintWriter(w);
        }

        return writer;
    }

}

class NoBodyOutputStream extends ServletOutputStream {
    private int contentLength = 0;

    NoBodyOutputStream(){
        // NOOP
    }

    int getContentLength() {
        return contentLength;
    }

    @Override
    public void write(int b) throws IOException {
        contentLength++;
    }

    @Override
    public void write(byte[] buf, int offset, int len) throws IOException{
        if (buf == null) {
            throw new NullPointerException("err.io.nullArray");
        }

        if (offset < 0 || len < 0 || offset+len > buf.length) {
            String msg = "err.io.indexOutOfBounds";
            Object[] msgArgs = new Object[3];
            msgArgs[0] = Integer.valueOf(offset);
            msgArgs[1] = Integer.valueOf(len);
            msgArgs[2] = Integer.valueOf(buf.length);
            msg = MessageFormat.format(msg, msgArgs);
            throw new IndexOutOfBoundsException(msg);
        }

        contentLength += len;
    }
}
