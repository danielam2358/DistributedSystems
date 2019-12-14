package app.models;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class Employee {
    private String name;
    private String role;
    public Employee(@JsonProperty(value = "name", required = true) String name,
                    @JsonProperty(value = "role", required = true) String role) {
        this.name = name;
        this.role = role;
    }
    public Employee setName(String name) {
        this.name = name;
        return this;
    }
    public Employee setRole(String role) {
        this.role = role;
        return this;
    }
    public String getName() { return name; }
    public String getRole() { return role; }
}