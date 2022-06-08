package io.github.xahdy.socialquarkus.domain.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "followers")
@Data
public class Follower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //indica relacionamento muitos para um
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //indica relacionamento muitos para um
    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User follower;
}
