//package com.dyonyon.The10000HourRule.Common;
//
//import jakarta.servlet.ReadListener;
//import jakarta.servlet.ServletInputStream;
//import jakarta.servlet.ServletOutputStream;
//import jakarta.servlet.WriteListener;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletRequestWrapper;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpServletResponseWrapper;
//import org.springframework.util.StreamUtils;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;
//
//public class ResponseBodyWrapper extends HttpServletResponseWrapper {
//
//    /**
//     * Constructs a request object wrapping the given request.
//     *
//     * @param request The request to wrap
//     * @throws IllegalArgumentException if the request is null
//     */
//    private String responseData;
//
//    public ResponseBodyWrapper(HttpServletResponse response) throws IOException {
//        super(response);
//        responseData = makeResponseData(response);
//    }
//
//    private String makeResponseData(HttpServletResponse response) throws IOException {
//        ServletOutputStream outputStream = response.getOutputStream();
//        byte[] rawData = StreamUtils.(outputStream);
//        return new String(rawData);
//    }
//
//    @Override
//    public ServletOutputStream getOutputStream() throws IOException {
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(this.responseData.getBytes(StandardCharsets.UTF_8));
//        return new ServletOutputStream() {
//            @Override
//            public void write(int b) throws IOException {
//
//            }
//
//            @Override
//            public boolean isReady() {
//                return true;
//            }
//
//            @Override
//            public void setWriteListener(WriteListener listener) {
//
//            }
//
//            @Override
//            public void setReadListener(ReadListener listener) {
//                throw new UnsupportedOperationException();
//            }
//
//            @Override
//            public int read() {
//                return inputStream.read();
//            }
//        };
//    }
//
//    @Override
//    public BufferedReader getReader() throws IOException {
//        return new BufferedReader(new InputStreamReader(this.getInputStream()));
//    }
//}
