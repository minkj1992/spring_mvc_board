package com.minkj1992.board.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute(new SignUpForm()); // Attribute 이름이 비슷한 경우 attribute key에 해당하는 str 생략가능
        return "account/sign-up";
    }
}
