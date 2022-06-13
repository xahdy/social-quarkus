package io.github.xahdy.socialquarkus.rest;

import io.github.xahdy.socialquarkus.domain.model.Follower;
import io.github.xahdy.socialquarkus.domain.model.User;
import io.github.xahdy.socialquarkus.domain.repository.FollowerRepository;
import io.github.xahdy.socialquarkus.domain.repository.UserRepository;
import io.github.xahdy.socialquarkus.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
@TestMethodOrder(MethodOrderer.class)
class FollowerResourceTest {

    @Inject
    FollowerRepository followerRepository;
    @Inject
    UserRepository userRepository;
    Long userId;
    Long UserFollowerId;

    @BeforeEach
    @Transactional
    void setUp() {
        //usuario padrão do teste
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");

        userRepository.persist(user);
        userId = user.getId();

        //usuario que é seguidor
        var UserFollower = new User();
        user.setAge(30);
        user.setName("Beltrano");

        userRepository.persist(UserFollower);
        UserFollowerId = UserFollower.getId();

        //cria um follower
        var followerEntity = new Follower();
        followerEntity.setFollower(UserFollower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
    }

    @Test
    @DisplayName("Deve retornar 409 se o followerid for igual ao userId")
    @Order(1)
    public void sameUserAsFollowerTest() {

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(409)
                .body(Matchers.is("You can't follow yourself"));
    }

    @Test
    @DisplayName("Deve retornar 404 se o userId não existir ao dar put")
    @Order(2)
    public void userNotFoundWhenPutTest() {

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        var nonexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", nonexistentUserId)
                .when()
                .put()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve seguir um usuario e retornar 204 nocontent")
    @Order(3)
    public void followUserTest() {

        var body = new FollowerRequest();
        body.setFollowerId(UserFollowerId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Deve retornar 404 se o userId não existir ao dar get")
    @Order(4)
    public void userNotFoundWhenGetTest() {
        var nonexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", nonexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve retornar 200 e lsitar os seguidores")
    @Order(5)
    public void listFollowersTest() {

        var response = given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .when()
                .get()
                .then()
                .extract().response();

        var followersCount = response.jsonPath().get("followersCount");
        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, followersCount);
    }

    @Test
    @DisplayName("Deve retornar 404 se o userId não existir ao dar delete")
    @Order(6)
    public void userNotFoundWhenDeleteTest() {
        var nonexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", nonexistentUserId)
                .queryParam("followerId", UserFollowerId)
                .when()
                .delete()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve retornar 200 ao deixar de seguir um usuario")
    @Order(6)
    public void unfollowUserTest() {

        given()
                .pathParam("userId", userId)
                .queryParam("followerId", UserFollowerId)
                .when()
                .delete()
                .then()
                .statusCode(204);
    }
}