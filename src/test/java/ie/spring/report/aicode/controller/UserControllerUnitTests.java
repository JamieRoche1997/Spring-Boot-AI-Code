package ie.spring.report.aicode.controller;

import ie.spring.report.aicode.model.AppUser;
import ie.spring.report.aicode.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTests {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser_returnsCreatedUser() {
        AppUser user = new AppUser();
        Mockito.when(userService.createUser(Mockito.any(AppUser.class))).thenReturn(user);

        AppUser result = userController.createUser(user);

        Assertions.assertNotNull(result);
        Mockito.verify(userService).createUser(Mockito.any(AppUser.class));
    }

    @Test
    void resetPassword_validUsername_updatesPassword() {
        AppUser user = new AppUser();
        Mockito.when(userService.resetPassword("testUser", "newPassword")).thenReturn(user);

        AppUser result = userController.resetPassword("testUser", "newPassword");

        Assertions.assertNotNull(result);
        Mockito.verify(userService).resetPassword("testUser", "newPassword");
    }
}
