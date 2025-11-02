package TheChhetriGroup.Blog.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;


    public void sendMail(String to, String subject, String body){

        try {
            SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
            simpleMailMessage.setText(body);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setTo(to);
            javaMailSender.send(simpleMailMessage);
        }
        catch (Exception e){
            log.error("Error while sending email "+e);
        }
    }

    public void sendPasswordChangeMail(String to, String subject, String body){

        try {
            SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
            simpleMailMessage.setText(body);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setTo(to);
            javaMailSender.send(simpleMailMessage);
        }
        catch (Exception e){
            log.error("Error while sending email "+e);
        }
    }

    public void sendUserNameChangeMail(String to, String subject, String body){

        try {
            SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
            simpleMailMessage.setText(body);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setTo(to);
            javaMailSender.send(simpleMailMessage);
        }
        catch (Exception e){
            log.error("Error while sending email "+e);
        }
    }
}
