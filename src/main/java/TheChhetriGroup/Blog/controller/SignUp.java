package TheChhetriGroup.Blog.controller;


import TheChhetriGroup.Blog.Utils.JwtUtil;
import TheChhetriGroup.Blog.entity.Blog;
import TheChhetriGroup.Blog.entity.User;
import TheChhetriGroup.Blog.entity.UserNameCheck;
import TheChhetriGroup.Blog.repository.BlogRepository;
import TheChhetriGroup.Blog.repository.UserRepositoy;
import TheChhetriGroup.Blog.services.EmailService;
import TheChhetriGroup.Blog.services.UserService;
import TheChhetriGroup.Blog.services.UserServiceDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("sign-up")
public class SignUp {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserServiceDetailsImpl userServiceDetailsImpl;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepositoy userRepositoy;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BlogRepository blogRepository;

    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody User user){
        User existingUser=userRepositoy.findByUserName(user.getUserName());
        String body="Hello "+user.getUserName()+",\n" +
                "\n" +
                "Welcome to TCG's Blogs! \uD83C\uDF89\n" +
                "\n" +
                "Thank you for signing up. We're excited to have you join our blogging community.\n" +
                "\n" +
                "Here’s what you can do next:\n" +
                "- Explore the latest blogs and topics that interest you.\n" +
                "- Start writing your own blogs and share your ideas with the community.\n" +
                "\n" +
                "If you have any questions or need assistance, feel free to reply to this email.\n" +
                "\n" +
                "Happy Blogging!\n" +
                "The TCG's Blogs Team\n";
        if(existingUser==null){
            userService.saveNewUser(user);
            //emailService.sendMail(user.getEmail(),"Welcome to TCG's Blogs!",body);
            return new ResponseEntity<>(user.getUserName()+" have signup successfully", HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<>("User with username "+user.getUserName()+" already exist.\nPlease use different username.",HttpStatus.IM_USED);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> logIn(@RequestBody User user){
        try{
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword()));
            UserDetails userDetails=userServiceDetailsImpl.loadUserByUsername(user.getUserName());
            String jwt= jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt,HttpStatus.ACCEPTED);
        }
        catch (AuthenticationException e){
            log.error("Problem was occured while authenticating user. Username or password wrong.");
            return new ResponseEntity<>("Username or password is wrong.\nPlease use the correct credential to login.",HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/otp")
    public ResponseEntity<?> getOtp(@RequestBody User user){
        try{
            User userExist=userService.findUserByUserName(user.getUserName());
            HashMap<String, String> map=new HashMap<>();
            if(userExist !=null ){
                String token = jwtUtil.generateToken(user.getUserName());
                Random rand = new Random();
                String otp="";
                for(int i=0;i<6;i++){
                    otp += rand.nextInt(9);
                }
                String body =
                        "Hey " + user.getUserName() + ",\n\n" +
                                "Your one-time password (OTP) to verify your TCG’s Blog App account is:\n\n" +
                                "🔹 " + otp + " 🔹\n\n" +
                                "This code will expire in 5 minutes, so please use it as soon as possible.\n" +
                                "If you didn’t request this verification, you can safely ignore this message.\n\n" +
                                "— Team TCG’s";

                emailService.sendMail(userExist.getEmail(),"Your TCG’s Blog App Verification Code",body);
                map.put("token",token);
                map.put("otp",otp);
            return  new ResponseEntity<>(map,HttpStatus.OK);
            }
            return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Problem was occured while generating otp");
            return new ResponseEntity<>("Problem was occured while generating otp",HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("create-admin")
    public ResponseEntity<String> createAdmin(@RequestBody User user){
        User existingUser=userService.findUserByUserName(user.getUserName());
        if(existingUser==null){
            userService.saveNewAdmin(user);
            return new ResponseEntity<>(user.getUserName()+" has been successfully signed up.",HttpStatus.CREATED);
        }
        else{
            log.error("User already exist. Duplicate key Mongo exception");
            return new ResponseEntity<>("User with this username already exist please use other username or add special Characters.",HttpStatus.IM_USED);
        }
    }

    @PostMapping("/check-userName")
    public ResponseEntity<Boolean> userNameExist(@RequestBody UserNameCheck user){
        User userByUserName = userService.findUserByUserName(user.getUserName());
        if(userByUserName==null){
            return new ResponseEntity<>(false,HttpStatus.OK);
        }
        return new ResponseEntity<>(true,HttpStatus.IM_USED);
    }

}
