package in.co.dipankar.bengalisuspense.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Car {

  private String color;
  private String type;

  public Car() {}

  public String getColor() {
    return color;
  }

  @JsonProperty("color")
  public void setColor(String color) {
    this.color = color;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Car(String color, String type) {
    this.color = color;
    this.type = type;
  }
  // standard getters setters
}
