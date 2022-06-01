package io.github.xahdy.socialquarkus.rest;

import io.github.xahdy.socialquarkus.domain.model.User;
import io.github.xahdy.socialquarkus.domain.repository.UserRepository;
import io.github.xahdy.socialquarkus.rest.dto.CreateUserRequest;
import io.github.xahdy.socialquarkus.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    //propriedades para usar os parametros passados no construtor
    private UserRepository repository;
    private Validator validator;

    //construtor
    @Inject
    public UserResource(UserRepository repository, Validator validator){
        this.repository = repository;
        this.validator = validator;
    }

    @POST
    @Transactional
    //metodo createUser que é do tipo Response e recebe um CreateUserRequest como userRequest.
    public Response createUser(CreateUserRequest userRequest) {
        //passamos o userRequest para ser validado pelo validator e gravamos o retorno dentro da variável violations
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);
        if(!violations.isEmpty()){
            //se a lista de violations não estiver vazia, chamamos o metodo createFromValidation do nosso ResponseError
            //para saber exatamente qual violação ocorreu na hora da validação
            ResponseError responseError = ResponseError.createFromValidation(violations);

            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity(responseError).build();
        }

        User user = new User();
        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());

        repository.persist(user);
        return Response.status(Response.Status.CREATED.getStatusCode())
                .entity(user)
                .build();

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
            return Response.noContent().build();
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
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
