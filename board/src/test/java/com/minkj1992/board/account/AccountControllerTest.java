package com.minkj1992.board.account;

import com.minkj1992.board.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean JavaMailSender javaMailSender;

    @DisplayName("회원가입 화면 표시 여부 테스트")
    @Test
    public void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());

    }

    @DisplayName("회원 가입 - 입력값 오류")
    @Test
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "user")
                .param("email", "jejulover")
                .param("password", "12345")
                .with(csrf()))
                .andExpect(status().isOk())

                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }


    @DisplayName("회원 가입 - 입력값 정상")
    @Test
    void signUpSubmit_with_correct_input() throws Exception {
        String userEmail = "minkj1992@email.com";
        String userPassword = "jejulover";

        String userName = "minkj1992";
        mockMvc.perform(post("/sign-up")
                .param("nickname", userName)
                .param("email", userEmail)
                .param("password", userPassword)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername(userName));

        Account newAccount = accountRepository.findByEmail(userEmail);
        assertNotNull(newAccount);
        assertNotEquals(newAccount.getPassword(), userPassword); // should encrpt
        assertNotNull(newAccount.getEmailCheckToken());
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }


    @DisplayName("회원 가입 - 메일 인증 토큰 오류")
    @Test
    void checkEmailToken_with_wrong_input() throws Exception {
        String wrongToken = "wadlkfjdsflkdjfewoif";
        mockMvc.perform(get("/check-email-token")
                .param("token", wrongToken)
                .param("email", "email@email.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated());
    }


    @DisplayName("회원 가입 - 메일 인증 토큰 정상")
    @Test
    @Transactional
    void checkEmailToken() throws Exception {
        String userName = "minkj1992";
        Account account = Account.builder()
                .email("test@email.com")
                .password("12345678")
                .nickname(userName)
                .build();
        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                .param("token", newAccount.getEmailCheckToken())
                .param("email", newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated().withUsername(userName));
    }

}