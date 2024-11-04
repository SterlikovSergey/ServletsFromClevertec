package ru.clevertec.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.clevertec.dto.UserDto;
import ru.clevertec.mapper.UserMapper;
import ru.clevertec.model.User;
import ru.clevertec.repository.Repository;
import ru.clevertec.service.IUserService;
import ru.clevertec.service.impl.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {

    private IUserService userService;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        Repository repository = (Repository) getServletContext().getAttribute("repository");
        userService = new UserService(repository);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath() + "/api/users";
        String idPart = requestURI.substring(contextPath.length());

        if (idPart == null || idPart.equals("/")) {
            List<User> users = userService.getAllUsers();
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(users));
        } else {
            try {
                Long id = Long.parseLong(idPart.substring(1));
                User user = userService.getUserById(id);
                if (user != null) {
                    resp.setContentType("application/json");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(objectMapper.writeValueAsString(user));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("User not found");
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Invalid user ID format");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            UserDto userDto = objectMapper.readValue(req.getInputStream(), UserDto.class);
            User user = userMapper.toUser(userDto);
            User createdUser = userService.createUser(user);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            resp.getWriter().write(objectMapper.writeValueAsString(createdUser));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Error creating user: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        Long id = Long.parseLong(pathInfo.substring(1));
        UserDto userDto = objectMapper.readValue(req.getInputStream(), UserDto.class);
        User user = userMapper.toUser(userDto);
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser != null) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.getWriter().write(objectMapper.writeValueAsString(updatedUser));
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("User not found");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        Long id = Long.parseLong(pathInfo.substring(1));
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("User deleted successfully");
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("User not found");
        }
    }
}
