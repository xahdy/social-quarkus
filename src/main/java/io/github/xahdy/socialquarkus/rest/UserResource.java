package io.github.xahdy.socialquarkus.rest;

import io.github.xahdy.socialquarkus.domain.model.User;
import io.github.xahdy.socialquarkus.domain.repository.UserRepository;
import io.github.xahdy.socialquarkus.rest.dto.CreateUserRequest;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/users")
public class UserResource {

    private UserRepository repository;

    @Inject
    public UserResource(UserRepository repository){
        this.repository = repository;
    }

    @POST
    @Transactional
    //metodo createUser que é do tipo Response e recebe um CreateUserRequest como userRequest.
    public Response createUser(CreateUserRequest userRequest) {
        User user = new User();
        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());

        repository.persist(user);
        return Response.ok(user).build();

    }

    @GET
    public Response listAllUsers() {
        //armazenamos o repository.findAll() dentro de uma variável chama de query para podermos acessar depois
        //depois de escrever User.findAll() usar ctrl alt v para criar a variável mais rapido
        PanacheQuery<User> query = repository.findAll();
        return Response.ok(query.list()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        //vamos passar o usuario encontrado pelo id para a variavel userSelected que é do tipo User
        User userSelected = repository.findById(id);
        if (userSelected != null) {
            repository.delete(userSelected);
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData) {
        //vamos passar o usuario encontrado pelo id para a variavel userSelected que é do tipo User
        User userSelected = repository.findById(id);
        if (userSelected != null) {
            userSelected.setName(userData.getName());
            userSelected.setAge(userData.getAge());
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
