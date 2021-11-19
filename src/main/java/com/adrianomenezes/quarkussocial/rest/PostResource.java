package com.adrianomenezes.quarkussocial.rest;

import com.adrianomenezes.quarkussocial.domain.model.Post;
import com.adrianomenezes.quarkussocial.domain.model.User;
import com.adrianomenezes.quarkussocial.domain.repository.FollowerRepository;
import com.adrianomenezes.quarkussocial.domain.repository.PostRepository;
import com.adrianomenezes.quarkussocial.domain.repository.UserRepository;
import com.adrianomenezes.quarkussocial.rest.dto.CreatePostRequest;
import com.adrianomenezes.quarkussocial.rest.dto.CreateUserRequest;
import com.adrianomenezes.quarkussocial.rest.dto.PostResponse;
import com.adrianomenezes.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PostResource {

    private PostRepository postRepository;
    private FollowerRepository followerRepository;
    private Validator validator;
    private UserRepository userRepository;

    @Inject
    public PostResource(
            UserRepository userRepository,
            PostRepository postRepository,
            FollowerRepository followerRepository,
            Validator validator){
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
        this.validator = validator;
    }


    @PUT
    @Transactional
    public Response savePost(
            @PathParam("userId") Long userId,
            CreatePostRequest postRequest){

        Set<ConstraintViolation<CreatePostRequest>> violations =
                validator.validate(postRequest);

        if (!violations.isEmpty()) {
            return ResponseError
                    .createFromValidation(violations)
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setPostText(postRequest.getPostText());
//        post.setDateTime(LocalDateTime.now());
        post.setUser(user);

        postRepository.persist(post);

        return Response.status(
                Response.Status.CREATED)
                .entity(post).build();
    }

    @GET
    public Response listPosts(
            @PathParam("userId") Long userId,
            @HeaderParam("followerId") Long followerId){
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (followerId == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("You forgot the header followerId")
                    .build();
        }


        User follower = userRepository.findById(followerId);
        if (follower == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Follower not found").build();
        }

        boolean follows = followerRepository.follows(follower, user);

        if (follows) {
            PanacheQuery<Post> query =
                    postRepository
                            .find("user",
                                    Sort.by("dateTime",
                                            Sort.Direction.Descending),
                                    user);
//                postRepository.find(
//                        "select post from Post where User = :user",user);

            var list = query
                    .list()
                    .stream()
//                .map(post -> PostResponse.fromEntity(post))
                    .map(PostResponse::fromEntity)
                    .collect(Collectors.toList());

            return Response.ok().entity(list).build();

        }

        return Response.status(Response.Status.FORBIDDEN).entity("You cant see these posts.").build();


    }



}
