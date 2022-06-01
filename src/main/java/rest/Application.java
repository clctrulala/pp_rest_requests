package rest;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import rest.model.User;

import java.util.List;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    ApplicationRunner runRunner() {
        final String url = "http://94.198.50.185:7081/api/users";
        final String cookie = "Set-Cookie";
        final String session = "Cookie";

        return args -> {
            HttpHeaders header = new HttpHeaders();
            RestTemplate template = new RestTemplate();
            User userJames = new User(3L, "James", "Brown", (byte)12);

            // get session
            System.out.println("GET =====================");
            ResponseEntity<List> response = template.getForEntity(url, List.class);
            header.add(session, response.getHeaders().getFirst(cookie));
            header.setContentType(MediaType.APPLICATION_JSON);
            List users = response.getBody();
            if(HttpStatus.OK != response.getStatusCode()) {
                System.out.println("Status = " + response.getStatusCode());
                System.exit(111);
            }
            users.forEach(System.out::println);
            System.out.println("cookie = " + header);
            System.out.println("cookie = " + header.getFirst(cookie));

            // create id = 3, name = James, lastName = Brown, age = 12
            // get code
            System.out.println("POST =====================");
            RequestEntity<User> firstRequest = RequestEntity.post(url).headers(header).body(userJames);
            ResponseEntity<String> firstCode = template.postForEntity(url, firstRequest, String.class);
            if(null != firstCode) {
                System.out.println("First part of code: " + firstCode.getBody());
                System.out.println("Post header: " + firstCode.getHeaders().getFirst(cookie));
                System.out.println("Post header change: " + header);
            } else {
                System.out.println("ERROR on getting FIRST part of code");
            }

            // change id = 3, name = Thomas, lastName = Shelby, age = 12
            // get code
            System.out.println("PUT =====================");
            userJames.setName("Thomas");
            userJames.setLastName("Shelby");
            RequestEntity<User> secondRequest = RequestEntity.put(url).headers(header).body(userJames);
            ResponseEntity<String> secondCode = template.exchange(url, HttpMethod.PUT, secondRequest, String.class);
            if(null != secondCode) {
                System.out.println("Second part of code: " + secondCode.getBody());
                System.out.println("Put header: " + firstCode.getHeaders().getFirst(cookie));
                System.out.println("Put header change: " + header);
            } else {
                System.out.println("ERROR on getting SECOND part of code");
            }

            // delete id = 3
            // get code
            System.out.println("DELETE =====================");
            RequestEntity<Void> lastRequest = RequestEntity.delete(url+"/{id}", userJames.getId()).headers(header).build();
            ResponseEntity<String> lastCode = template.exchange(url+"/{id}", HttpMethod.DELETE, secondRequest, String.class, userJames.getId());
            if(null != lastCode) {
                System.out.println("Second part of code: " + lastCode.getBody());
            } else {
                System.out.println("ERROR on getting SECOND part of code");
            }
        };
    }
}