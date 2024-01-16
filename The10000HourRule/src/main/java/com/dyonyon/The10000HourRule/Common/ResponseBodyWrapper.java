package com.dyonyon.The10000HourRule.Common;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ResponseBodyWrapper  extends HttpServletResponseWrapper {

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    private String responseData;

    public ResponseBodyWrapper(HttpServletResponse response) throws IOException {
        super(response);
        responseData = makeResponseData(response);
    }

    public String getResponseData() {
        return responseData;
    }

    private String makeResponseData(HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        return new String(outputStream.toString());
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(this.responseData.getBytes(StandardCharsets.UTF_8));

        return new ServletOutputStream() {

            @Override
            public void write(int b) throws IOException {
                outputStream.write(b);
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener listener) {

            }
        };
    }
}
