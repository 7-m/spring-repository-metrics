package dev.mufaddal.simplerest;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Person  {

  @Id
  String id;
  String name;
  Address address;

}
