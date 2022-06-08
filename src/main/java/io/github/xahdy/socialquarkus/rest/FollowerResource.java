package io.github.xahdy.socialquarkus.rest;

import io.github.xahdy.socialquarkus.domain.model.Follower;
import io.github.xahdy.socialquarkus.domain.repository.FollowerRepository;
import io.github.xahdy.socialquarkus.domain.repository.PostRepository;
import io.github.xahdy.socialquarkus.domain.repository.UserRepository;
import io.github.xahdy.socialquarkus.rest.dto.FollowerRequest;
import io.github.xahdy.socialquarkus.rest.dto.FollowerResponse;
import io.github.xahdy.socialquarkus.rest.dto.FollowersPerUseResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private UserRepository userRepository;
    private FollowerRepository followerRepository;

    @Inject
    public FollowerResource(UserRepository userRepository, FollowerRepository followerRepository) {
        this.userRepository = userRepository;

        this.followerRepository = followerRepository;
    }

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest followerRequest) {

        if (userId.equals(followerRequest.getFollowerId())) {
            return Response.status(Response.Status.CONFLICT).entity("You can't follow yourself").build();
        }

        //buscar um user pelo ID passado na url
        var user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        //busca um usuario pelo ID passado via body da requisição.
        var follower = userRepository.findById(followerRequest.getFollowerId());

        boolean follows = followerRepository.follows(follower, user);

        if (!follows) {
            //cria um novo Seguidor e informa quem está sendo seguido e quem está seguindo.
            var entity = new Follower();
            //usuario que está sendo seguido é aquele informado pela url
            entity.setUser(user);
            //usuario seguindo é aquele informado pelo body da requisição.
            entity.setFollower(follower);

            //persisitimos esses dados
            followerRepository.persist(entity);
        }


        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId) {

        var user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        //cria uma lista com os resultados do findByUser do repositorio.
        var list = followerRepository.findByUser(userId);
        // cria um novo objeto do tipo FollowersPerUseResponse
        FollowersPerUseResponse responseObject = new FollowersPerUseResponse();
        //define a quantidade de seguidores de acordo com o tamanho da lista criada anteriormente
        responseObject.setFollowersCount(list.size());

        //cria uma lsita nova mapeando os resultados com o FollowerResponse
        var followerList = list.stream()
                .map(FollowerResponse::new).
                collect(Collectors.toList());

        //define o conteudo do objeto  de acordo com os dados da lista
        responseObject.setContent(followerList);
        return Response.ok(responseObject).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId) {
        var user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        followerRepository.deleteByFollowerAndUser(followerId, userId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
