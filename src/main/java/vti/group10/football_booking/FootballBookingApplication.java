package vti.group10.football_booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FootballBookingApplication {

	public static void main(String[] args) {
		System.out.println("=== Starting Football Booking Application ===");
		SpringApplication.run(FootballBookingApplication.class, args);
		System.out.println("=== Application Started Successfully ===");
	}
}