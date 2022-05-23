package io.github.xahdy.socialquarkus.rest;

import io.github.xahdy.socialquarkus.domain.model.User;
import io.github.xahdy.socialquarkus.rest.dto.CreateUserRequest;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/users")
public class UserResource {

    @POST
    @Transactional
    //metodo createUser que é do tipo Response e recebe um CreateUserRequest como userRequest.
    public Response createUser(CreateUserRequest userRequest) {
        User user = new User();
        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());

        user.persist();
        return Response.ok(user).build();

    }

    @GET
    public Response listAllUsers() {
        //armazenamos o User.findAll() dentro de uma variável chama de query para podermos acessar depois
        //depois de escrever User.findAll() usar ctrl alt v para criar a variável mais rapido
        PanacheQuery<PanacheEntityBase> query = User.findAll();
        return Response.ok(query.list()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        //vamos passar o usuario encontrado pelo id para a variavel userSelected que é do tipo User
        User userSelected = User.findById(id);
        if (userSelected != null) {
            userSelected.delete();
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData) {
        //vamos passar o usuario encontrado pelo id para a variavel userSelected que é do tipo User
        User userSelected = User.findById(id);
        if (userSelected != null) {
            userSelected.setName(userData.getName());
            userSelected.setAge(userData.getAge());
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
