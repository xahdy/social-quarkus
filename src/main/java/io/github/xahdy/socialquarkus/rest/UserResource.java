package io.github.xahdy.socialquarkus.rest;

import io.github.xahdy.socialquarkus.rest.dto.CreateUserRequest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/users")
public class UserResource {

    @POST
    public Response createUser(CreateUserRequest userRequest){
        return Response.ok(userRequest).build();

    }

    @GET
    public  Response listAllUsers(){
        return Response.ok().build();
    }
}
