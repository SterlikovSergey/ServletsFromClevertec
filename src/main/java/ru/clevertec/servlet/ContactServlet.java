package ru.clevertec.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.clevertec.dto.ContactDto;
import ru.clevertec.mapper.ContactMapper;
import ru.clevertec.model.Contact;
import ru.clevertec.repository.Repository;
import ru.clevertec.service.IUserService;
import ru.clevertec.service.impl.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/users/contacts/*")
public class ContactServlet extends HttpServlet {

    private IUserService userService;
    private final ContactMapper contactMapper = ContactMapper.INSTANCE;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        Repository repository = (Repository) getServletContext().getAttribute("repository");
        userService = new UserService(repository);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.length() <= 1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("User ID is missing in the path");
            return;
        }

        try {
            Long userId = Long.parseLong(pathInfo.substring(1));

            ContactDto contactDto = objectMapper.readValue(request.getInputStream(), ContactDto.class);
            Contact contact = contactMapper.toContact(contactDto);

            Contact addedContact = userService.addContact(userId, contact);

            if (addedContact != null) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(addedContact));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("User not found");
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid user ID format");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error adding contact: " + e.getMessage());
        }
    }
}
