package com.cosmos.usersmanagementsystem.controller;

import com.cosmos.usersmanagementsystem.dto.MailBody;
import com.cosmos.usersmanagementsystem.entity.ForgotPassword;
import com.cosmos.usersmanagementsystem.entity.OurUsers;
import com.cosmos.usersmanagementsystem.repository.ForgotPasswordRepository;
import com.cosmos.usersmanagementsystem.repository.UsersRepo;
import com.cosmos.usersmanagementsystem.service.EmailService;
import com.cosmos.usersmanagementsystem.utils.ChangePassword;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

    private final UsersRepo usersRepo;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;


    public ForgotPasswordController(UsersRepo usersRepo, EmailService emailService, ForgotPasswordRepository forgotPasswordRepository, PasswordEncoder passwordEncoder) {
        this.usersRepo = usersRepo;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //send mail for emial verification
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email){
        OurUsers ourUsers = usersRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email !"));

        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("this is the OTP for your forgot password request : " + otp)
                .subject("OTP for forgot password request")
                .build();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 20 * 1000))
                .ourUser(ourUsers)
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok("Email sent for verification !");
    }
    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email){
        OurUsers ourUsers = usersRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email !"));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, ourUsers)
                .orElseThrow(() -> new RuntimeException("Invalid OTP for email : " + email));

        if(fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepository.deleteById(fp.getFpId());
            return new ResponseEntity<>("OTP has expired!", HttpStatus.EXPECTATION_FAILED);
        }
        return ResponseEntity.ok("OTP verified!");
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword,
                                                        @PathVariable String email){

        if(!Objects.equals(changePassword.password(), changePassword.repeatPassword())){
            return new ResponseEntity<>("Please enter the password again!", HttpStatus.EXPECTATION_FAILED);
        }
        String endodedPassword = passwordEncoder.encode(changePassword.password());
        usersRepo.updatePassword(email, endodedPassword);

        return ResponseEntity.ok("Password has been changed!");
    }

    private Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
