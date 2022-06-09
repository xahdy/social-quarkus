package io.github.xahdy.socialquarkus.rest;

import io.github.xahdy.socialquarkus.rest.dto.CreateUserRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserResourceTest {

    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    public void createUserTest() {
        //constrói um novo objeto DTO createUserRequest e guarda os dados em user
        var user = new CreateUserRequest();
        user.setName("Fulano");
        user.setAge(40);


        var response =
                //dado esse cenário
                given()
                        //tipo do conteudo que estou enviando.
                        .contentType(ContentType.JSON)
                        //no corpo vai ser passado o usuário definido anteriormente
                        .body(user)
                        //quando
                        .when()
                        //for enviado via post na url users
                        .post("/users")
                        //entao
                        .then()
                        //extrair a resposta
                        .extract().response();
        //assertEquals diz qual a resposta esperada do teste realizado.
        assertEquals(201, response.getStatusCode());
        //esperado que o id não seja nulo.
        assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @DisplayName("Deve retornar erro quando json n for válido")
    public void createUserValidationTest() {
        var user = new CreateUserRequest();
        user.setName(null);
        user.setAge(null);

        var response =
                //dado esse cenário
                given()
                        //tipo do conteudo que estou enviando.
                        .contentType(ContentType.JSON)
                        //no corpo vai ser passado o usuário definido anteriormente
                        .body(user)
                        //quando
                        .when()
                        //for enviado via post na url users
                        .post("/users")
                        //entao
                        .then()
                        //extrair a resposta
                        .extract().response();

        //assertEquals diz qual a resposta esperada do teste realizado.
        //a primeira parte antes da , diz o que esperamos e após a , com o que será comparado.
        assertEquals(400, response.getStatusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));
        //guardamos em uma lista e mapeamos todas as mensagem de erro esperadas.
        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));
    }
}