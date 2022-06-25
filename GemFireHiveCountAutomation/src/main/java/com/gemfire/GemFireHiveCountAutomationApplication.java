package com.gemfire;

import java.text.ParseException;
import javax.mail.MessagingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@EnableScheduling
@SpringBootApplication
@Component
public class GemFireHiveCountAutomationApplication extends Logic{
  
  public static void main(String[] args) throws ParseException, MessagingException {
	  
    SpringApplication.run(GemFireHiveCountAutomationApplication.class, args);
    codeFlow();
  }
  
 

}