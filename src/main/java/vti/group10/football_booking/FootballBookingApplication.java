package vti.group10.football_booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// @SpringBootApplication(
//     exclude = {
//         org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
//         org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
//     }
// )
@EnableScheduling
@SpringBootApplication
public class FootballBookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(FootballBookingApplication.class, args);
	}

}
