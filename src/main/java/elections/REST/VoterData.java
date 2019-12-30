package elections.REST;


import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Data
public class VoterData {
    private String id;
    private String name;
    private String state;
    private String vote;

    public VoterData(
            @JsonProperty(value = "id", required = true) String id,
            @JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "state", required = true) String state,
            @JsonProperty(value = "vote", required = true) String vote
            ){
        this.id = id;
        this.name = name;
        this.state = state;
        this.vote = vote;
    }


    public String getId() {
        return id;
    }

    public VoterData setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public VoterData setName(String name) {
        this.name = name;
        return this;
    }

    public String getState() {
        return state;
    }

    public VoterData setState(String state) {
        this.state = state;
        return this;
    }

    public String getVote() {
        return vote;
    }

    public VoterData setVote(String vote) {
        this.vote = vote;
        return this;
    }
}
