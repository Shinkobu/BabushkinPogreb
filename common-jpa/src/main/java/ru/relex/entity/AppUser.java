package ru.relex.entity;

import lombok.*;
import ru.relex.entity.enums.UserState;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter // lombok generates getters
@Setter // lombok generates setters
@EqualsAndHashCode(exclude = "id") // lombok generates equals and hashcode using all fields except excluded
@Builder // lombok. builder inner class
@NoArgsConstructor // lombok. constructor with no arguments
@AllArgsConstructor  // lombok. constructor with all arguments
@Entity // class will be an entity and connected to db table
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // db will generate value for id
    private Long id;
    private Long telegramUserId; // the user id in telegram
    private LocalDateTime firstLoginDate;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Boolean isActive;
    @Enumerated(EnumType.STRING) // enum will be transmitted to db as a String
    private UserState state;
}
