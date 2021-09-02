package ltd.yangliuqing.springsecurityjwtdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yang
 */
@RestController
public class TestController {

    @GetMapping("/authentication")
    public String getAuth() {
        return "authentication";
    }

    @GetMapping("/permit")
    public String getPermit() {
        return "permit";
    }

}
