package ltd.yangliuqing.springsecurityjwtdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yang
 */
@RestController
public class TestController {

    @GetMapping("/user")
    public String getAuth() {
        return "You have user or admin role";
    }

    @GetMapping("/admin")
    public String getAdmin() {
        return "You have admin role";
    }

    @GetMapping("/permit")
    public String getPermit() {
        return "Everyone can see this message";
    }

}
