package ru.relex.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.*;

/**
 * Here all incoming updates will be saved. And a unique id primary key will be assigned to every update
 *
 * jsonb is a feature of postgre, an upgraded optimised version of json
 *
 */

//@Data // lombok. all getters, setters, equals and hashcode
@Getter // lombok generates getters
@Setter // lombok generates setters
@EqualsAndHashCode (exclude = "id") // lombok generates equals and hashcode using all fields except excluded
@Builder // lombok. builder inner class
@NoArgsConstructor // lombok. constructor with no arguments
@AllArgsConstructor  // lombok. constructor with all arguments
@Entity // class will be an entity and connected to db table
@Table(name = "raw_data")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class) // to let jsonb work
public class RawData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // db will generate value for id
    private Long id;
    @Type(type="jsonb") // to let jsonb work
    @Column(columnDefinition = "jsonb") // to let jsonb work
    private Update event;
}
