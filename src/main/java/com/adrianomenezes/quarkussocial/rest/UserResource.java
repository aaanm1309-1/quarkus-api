package com.adrianomenezes.quarkussocial.rest;

import com.adrianomenezes.quarkussocial.domain.model.User;
import com.adrianomenezes.quarkussocial.domain.repository.UserRepository;
import com.adrianomenezes.quarkussocial.rest.dto.CreateUserRequest;
import com.adrianomenezes.quarkussocial.rest.dto.ResponseError;
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
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserRepository userRepository;
    private Validator validator;

    @Inject
    public UserResource(UserRepository userRepository, Validator validator){
        this.userRepository = userRepository;
        this.validator = validator;
    }

    @POST
    @Transactional
    public Response createUser( CreateUserRequest userRequest ) {

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(userRequest);

        if (!violations.isEmpty()) {
            return ResponseError
                    .createFromValidation(violations)
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = new User();
        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());

        userRepository.persist(user);

        return Response.status(Response.Status.CREATED)
                .entity(user)
                .build();

    }

    @GET
    public Response listAllUser( ) {
        PanacheQuery<User> query = userRepository.findAll();
        return Response.ok(query.list()).build();
    }

    @GET
    @Path("{id}")
    public Response listUser( @PathParam("id") Long id) {
        User user = userRepository.findById(id);
        if (user != null) {
            return Response.ok(user).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response atualizaUsers(@PathParam("id") Long id, CreateUserRequest userRequest ) {
        User user = userRepository.findById(id);
        if (user != null) {
            user.setAge(userRequest.getAge());
            user.setName(userRequest.getName());
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }


    @DELETE
    @Path("{id}")
    @Transactional
    public Response deletaUsers(@PathParam("id") Long id ) {
        User user = userRepository.findById(id);
        if (user != null) {
            userRepository.delete(user);
            return Response.noContent().build();
//            return Response.ok(user).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();

    }


}
