package io.github.xahdy.socialquarkus.rest;

import io.github.xahdy.socialquarkus.domain.model.Post;
import io.github.xahdy.socialquarkus.domain.model.User;
import io.github.xahdy.socialquarkus.domain.repository.FollowerRepository;
import io.github.xahdy.socialquarkus.domain.repository.PostRepository;
import io.github.xahdy.socialquarkus.domain.repository.UserRepository;
import io.github.xahdy.socialquarkus.rest.dto.CreatePostRequest;
import io.github.xahdy.socialquarkus.rest.dto.PostResponse;
import io.github.xahdy.socialquarkus.rest.dto.ResponseError;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private UserRepository userRepository;
    private PostRepository postRepository;
    private Validator validator;
    private FollowerRepository followerRepository;

    @Inject
    //injetamos nossas dependencias de repository para que possamos fazer operações com elas.
    public PostResource(UserRepository userRepository,
                        PostRepository postRepository,
                        Validator validator,
                        FollowerRepository followerRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.validator = validator;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request) {
        //usamos o userRepository para encontrar o usuario pelo id informado na url
        User user = userRepository.findById(userId);
        //se nosso userRepository não achar nenhum user no id informado, retornamos um erro 404
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        //passamos o request para ser validado pelo validator e gravamos o retorno dentro da variável violations
        Set<ConstraintViolation<CreatePostRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            //se a lista de violations não estiver vazia, chamamos o metodo createFromValidation do nosso ResponseError
            //para saber exatamente qual violação ocorreu na hora da validação
            ResponseError responseError = ResponseError.createFromValidation(violations);

            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity(responseError).build();
        }

        //instanciamos um novo post
        Post post = new Post();
        //definimos o text desse post através do que recebemos via CreatePostRequest
        post.setText(request.getText());
        //definimos que o user desse post é o user que está fazendo a postagem.
        post.setUser(user);
        //chamamos o postRepository para que ele persista nosso objeto post
        postRepository.persist(post);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPosts(@PathParam("userId") Long userId, @HeaderParam("followerId") Long followerId) {
        //usamos o userRepository para encontrar o usuario pelo id informado na url
        User user = userRepository.findById(userId);
        //se nosso userRepository não achar nenhum user no id informado, retornamos um erro 404
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (followerId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("You forgot the header followerId").build();
        }

        User follower = userRepository.findById(followerId);

        if(follower == null){
            return Response.status(Response.Status.BAD_REQUEST).entity("Nonexistent followerId").build();
        }
        //guardar em follows se o seguidor segue o usuario como verdadeiro ou falso
        boolean follows = followerRepository.follows(follower, user);
        if (!follows) {
            return Response.status(Response.Status.FORBIDDEN).entity("You can't see these posts").build();
        }

        //fazemos uma query para buscar pelo user que informamos.
        var queryPostsByUser = postRepository.find("user", Sort.by("dateTime", Sort.Direction.Descending), user);
        //ao executar a query, guardamos todos os resultados em uma list.
        var listPostByUser = queryPostsByUser.list();

        var postResponseList = listPostByUser.stream().
                //o map mapeia um objeto do Post que foi obtido através do stream da nossa lista de posts e transforma em um PostResponse.
//                map(post -> PostResponse.fromEntity(post))
                //Como o map passa apenas um metodo (post) e o fromEntity precisa de apenas um parametro (post)
                // podemos usar o map da forma a baixo, chamado de metodo de referencia.
                        map(PostResponse::fromEntity)
                .collect(Collectors.toList());
        return Response.ok(postResponseList).build();
    }


    @DELETE
    //quando a url tiver um complemento em relação a url principal, adicionar apenas esse elemento como path do metodo
    @Path("{postId}")
    @Transactional
    public Response deletePost(@PathParam("userId") Long userId, @PathParam("postId") Long postId) {
        var postDelete = postRepository.selectPostByIdAndUserId(postId, userId);
        //se o metodo selectPostByIdAndUserId não achar nenhum post, ele vai retornar um post nulo e consequentemente vamos devolver um erro 404
        if (postDelete == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        postRepository.delete(postDelete);
        return Response.noContent().build();

    }

    @PUT
    //quando a url tiver um complemento em relação a url principal, adicionar apenas esse elemento como path do metodo
    @Path("{postId}")
    @Transactional
    public Response updatePost(@PathParam("userId") Long userId, @PathParam("postId") Long postId, CreatePostRequest request) {
        var postUpdate = postRepository.selectPostByIdAndUserId(postId, userId);
        //se o metodo selectPostByIdAndUserId não achar nenhum post, ele vai retornar um post nulo e consequentemente vamos devolver um erro 404
        if (postUpdate == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        //passamos o userRequest para ser validado pelo validator e gravamos o retorno dentro da variável violations
        Set<ConstraintViolation<CreatePostRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            //se a lista de violations não estiver vazia, chamamos o metodo createFromValidation do nosso ResponseError
            //para saber exatamente qual violação ocorreu na hora da validação
            ResponseError responseError = ResponseError.createFromValidation(violations);

            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity(responseError).build();
        }
        postUpdate.setText(request.getText());

        return Response.noContent().build();

    }
}
