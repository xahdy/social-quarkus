package io.github.xahdy.socialquarkus.rest.dto;

import io.github.xahdy.socialquarkus.domain.model.Follower;
import lombok.Data;

@Data
public class FollowerResponse {
    private Long id;
    private String name;

    public FollowerResponse(){

    }

    //recebe um follower e pega seus dados
    public FollowerResponse(Follower follower){
        this(follower.getFollower().getId(), follower.getFollower().getName());
    }

    //utiliza os dados pegos pelo outro construtor, para definir os dados de um follower
    public FollowerResponse(Long id, String name){
        this.id = id;
        this.name = name;
    }
}
