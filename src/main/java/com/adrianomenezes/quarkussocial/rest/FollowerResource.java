package com.adrianomenezes.quarkussocial.rest;

import com.adrianomenezes.quarkussocial.domain.model.Follower;
import com.adrianomenezes.quarkussocial.domain.model.Post;
import com.adrianomenezes.quarkussocial.domain.model.User;
import com.adrianomenezes.quarkussocial.domain.repository.FollowerRepository;
import com.adrianomenezes.quarkussocial.domain.repository.PostRepository;
import com.adrianomenezes.quarkussocial.domain.repository.UserRepository;
import com.adrianomenezes.quarkussocial.rest.dto.*;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private PostRepository postRepository;
    private FollowerRepository followerRepository;
    private Validator validator;
    private UserRepository userRepository;

    @Inject
    public FollowerResource(
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
    public Response followUser(
            @PathParam("userId") Long userId,
            CreateFollowerRequest followRequest){

        Set<ConstraintViolation<CreateFollowerRequest>> violations =
                validator.validate(followRequest);

        if (!violations.isEmpty()) {
            return ResponseError
                    .createFromValidation(violations)
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }


        if (userId.equals(followRequest.getFollowerId())) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity("You can't follow yourself").build();
        }


        User user = userRepository.findById(userId);
        if (user == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("User not found")
                    .build();
        }

        User follower = userRepository.findById(followRequest.getFollowerId());
        if (follower == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Follower not found")
                    .build();
        }

        if (!followerRepository.follows(follower, user)){

            Follower newFollower = new Follower();
            newFollower.setUser(user);
            newFollower.setFollower(follower);

            followerRepository.persist(newFollower);

        }

        return Response.status(
                Response.Status.NO_CONTENT)
                .build();
    }

    @GET
    public Response listFollower(@PathParam("userId") Long userId){
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<Follower> followers =
                followerRepository
                        .findByUser(userId);

        FollowerPerUserResponse responseObject = new FollowerPerUserResponse();

        responseObject.setFollowersCount(followers.size());

        var followerResponseList =
                            followers
                                    .stream()
                                    .map(FollowerResponse::new)
                                    .collect(Collectors.toList());


        responseObject.setContent(followerResponseList);

        return Response.ok(responseObject).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(
            @PathParam("userId") Long userId,
            @QueryParam("followerId") Long followerId){

        if (userId.equals(followerId)) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity("You can't unfollow yourself")
                    .build();
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("User not found")
                    .build();
        }

        User follower = userRepository.findById(followerId);
        if (follower == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Follower not found")
                    .build();
        }
        if (!followerRepository.follows(follower, user)){
            return Response.status(Response.Status.NOT_FOUND).build();
        }


        followerRepository.deleteByFollowerAndUser(userId, followerId);
//        var followerFound = followerRepository.findByUserAndFollower(user, follower);
//
//        followerRepository.delete(followerFound);
//        return Response.status(
//                        Response.Status.OK).entity(followerFound)
//                .build();
        return Response.status(
                        Response.Status.NO_CONTENT)
                .build();
    }
}
