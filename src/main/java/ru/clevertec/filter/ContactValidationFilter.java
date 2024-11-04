package ru.clevertec.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.clevertec.dto.ContactDto;
import ru.clevertec.model.enums.ContactType;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;

@WebFilter("/api/users/contacts/*")
public class ContactValidationFilter implements Filter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Буферизация тела запроса
        BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(httpRequest);

        try {
            ContactDto contactDto = objectMapper.readValue(bufferedRequest.getReader(), ContactDto.class);
            ContactType contactType = contactDto.getType();
            String contactValue = contactDto.getValue();

            if (contactType == null || contactValue == null) {
                sendErrorResponse(httpResponse, "Contact type and value are required");
                return;
            }

            if (!validateContact(contactType, contactValue)) {
                sendErrorResponse(httpResponse, "Invalid contact information");
                return;
            }

            chain.doFilter(bufferedRequest, response);
        } catch (IOException e) {
            sendErrorResponse(httpResponse, "Invalid JSON format");
        }
    }

    private boolean validateContact(ContactType type, String value) {
        switch (type) {
            case PHONE:
                return value.matches("\\+?[0-9]{10,15}");
            case EMAIL:
                return value.matches("^[A-Za-z0-9+_.-]+@(.+)$");
            case SKYPE:
                return value.matches("^[a-zA-Z][a-zA-Z0-9_.,-]{5,31}$");
            default:
                return false;
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    // Класс-обертка для буферизации тела запроса
    private static class BufferedRequestWrapper extends HttpServletRequestWrapper {
        private final String body;

        public BufferedRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = request.getReader();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            body = stringBuilder.toString();
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new StringReader(body));
        }

        @Override
        public ServletInputStream getInputStream() {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
            return new ServletInputStream() {
                @Override
                public int read() {
                    return byteArrayInputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return byteArrayInputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                }
            };
        }
    }
}