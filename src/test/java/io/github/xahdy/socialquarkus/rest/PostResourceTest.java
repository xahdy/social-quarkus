package io.github.xahdy.socialquarkus.rest;

import io.github.xahdy.socialquarkus.domain.model.Follower;
import io.github.xahdy.socialquarkus.domain.model.Post;
import io.github.xahdy.socialquarkus.domain.model.User;
import io.github.xahdy.socialquarkus.domain.repository.FollowerRepository;
import io.github.xahdy.socialquarkus.domain.repository.PostRepository;
import io.github.xahdy.socialquarkus.domain.repository.UserRepository;
import io.github.xahdy.socialquarkus.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;


import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;


@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    //Injetar o userRepository para podermos persistir um user antes dos testes.
    @Inject
    UserRepository userRepository;
    //FollowerRepository para persistir um follower
    @Inject
    FollowerRepository followerRepository;
    //PostRepository para persistir um post
    @Inject
    PostRepository postRepository;
    //variável que vai receber o id do usuario
    Long userId;
    //Indica que esse metodo vai rodar antes de cada teste

    Long userNotFollowerId;

    Long UserFollowerId;
    Long postId;

    @BeforeEach
    @Transactional
    public void setUP() {
        //usuario padrão do teste
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");

        userRepository.persist(user);
        userId = user.getId();

        //criando um post
        Post post = new Post();
        post.setText("Algum texto");
        post.setUser(user);
        postRepository.persist(post);
        postId = post.getId();

        //usuario que não é seguidor
        var userNotFollower = new User();
        user.setAge(30);
        user.setName("Ciclano");

        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        //usuario que é seguidor
        var UserFollower = new User();
        user.setAge(30);
        user.setName("Beltrano");

        userRepository.persist(UserFollower);
        UserFollowerId = UserFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(UserFollower);

        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("deve criar um post")
    @Order(1)
    public void createPostTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("algum texto");

        //define um userId para usar no teste
        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                //passa o userId por pathParam
                .pathParam("userId", userId)
                .when()
                .post()
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("deve retornar 404 por não encontrar o usuario")
    @Order(2)
    public void nonExistentUserTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("algum texto");

        var nonExistentUserId = 999;

        //define um userId para usar no teste
        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                //passa o userId por pathParam
                .pathParam("userId", nonExistentUserId)
                .when()
                .post()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("deve retornar 404 por não encontrar o user")
    @Order(3)
    public void listPostUserNotFoundTest() {

        var nonExistentUserId = 999;
        given()
                .pathParam("userId", nonExistentUserId)
                .when()
                .get()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("deve retornar 400 quando não encontrar o followerId header")
    @Order(4)
    public void listPostFollowerHeaderNotSendTest() {

        given()
                .pathParam("userId", userId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("You forgot the header followerId"));
    }

    @Test
    @DisplayName("deve retornar 400 quando não encontrar o seguidor")
    @Order(5)
    public void listPostFollowerNotFoundTest() {
        var nonExistentUserId = 999;

        given()
                .pathParam("userId", userId)
                .header("followerId", nonExistentUserId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("Nonexistent followerId"));
    }

    @Test
    @DisplayName("deve retornar 403 quando não for um seguidor")
    @Order(6)
    public void listPostNotAFollowerTest() {

        given()
                .pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
                .when()
                .get()
                .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these posts"));
    }

    @Test
    @DisplayName("deve retornar os posts")
    @Order(7)
    public void listPostsTest() {

        given()
                .pathParam("userId", userId)
                .header("followerId", UserFollowerId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }

    @Test
    @DisplayName("deve retornar 204 ao deletar um post")
    @Order(8)
    public void deletePostTest(){
        given()
                .pathParam("userId", userId)
                .pathParam("postId", postId)
                .when()
                .delete("/{postId}")
                .then()
                .statusCode(204);

    }
    @Test
    @DisplayName("deve retornar 404 se o post for null")
    @Order(9)
    public void deletePostNullTest(){
        var nonExistentPostId = 999;
        given()
                .pathParam("userId", userId)
                .pathParam("postId", nonExistentPostId)
                .when()
                .delete("/{postId}")
                .then()
                .statusCode(404);

    }
}