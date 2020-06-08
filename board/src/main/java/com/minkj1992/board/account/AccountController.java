package com.minkj1992.board.account;

import com.minkj1992.board.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;


@Controller
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    // signUpForm이 바인딩 될때 마다 실행
    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute(new SignUpForm()); // Attribute 이름이 비슷한 경우 attribute key에 해당하는 str 생략가능
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up";
        }
        Account newAccount = accountService.processNewAccount(signUpForm);
        accountService.login(newAccount); // auto login
        return "redirect:/";
    }

    //TODO: checkEmailToken 호출 시퀀스 생성하기
    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String viewName = "account/checked-email";

        if (account == null) {
            model.addAttribute("error", "wrong.email");
            return viewName;
        }

        if (!account.isValidEmailToken(token)) {
            log.info("sl4j" + token + " : " + account.getEmailCheckToken());
            model.addAttribute("error", "wrong.token");
            return viewName;
        }

        account.completeSignUp();
        accountService.login(account); // auto login
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
        return viewName;
    }
}
