package mk.ukim.finki.wp.kol2022.g3.model;

import javax.persistence.*;

@Entity
public class Interest {

    public Interest() {
    }

    public Interest(String name) {
        this.name = name;
    }

    private Long id;

    private String name;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
