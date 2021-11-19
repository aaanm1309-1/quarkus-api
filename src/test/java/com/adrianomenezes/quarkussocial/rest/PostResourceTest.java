package com.adrianomenezes.quarkussocial.rest;

import com.adrianomenezes.quarkussocial.domain.model.Follower;
import com.adrianomenezes.quarkussocial.domain.model.Post;
import com.adrianomenezes.quarkussocial.domain.model.User;
import com.adrianomenezes.quarkussocial.domain.repository.FollowerRepository;
import com.adrianomenezes.quarkussocial.domain.repository.PostRepository;
import com.adrianomenezes.quarkussocial.domain.repository.UserRepository;
import com.adrianomenezes.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    @Inject
    PostRepository postRepository;
    Long userId;
    Long followerId;
    Long unfollowerId;


    @BeforeEach
    @Transactional
    public void setUp(){
        var user = new User();
        user.setAge(25);
        user.setName("Fulaninho");

        var follower = new User();
        follower.setAge(25);
        follower.setName("Seguidor");

        var unfollower = new User();
        follower.setAge(25);
        follower.setName("Nao Seguidor");

        userRepository.persist(user);
        userId = user.getId();

        userRepository.persist(follower);
        followerId = follower.getId();


        userRepository.persist(unfollower);
        unfollowerId = unfollower.getId();


        Follower newFollower = new Follower();
        newFollower.setUser(user);
        newFollower.setFollower(follower);

        followerRepository.persist(newFollower);

        Post post = new Post();
        post.setPostText("Qualquer Post");
        post.setUser(user);

        postRepository.persist(post);

    }


    @Test
    @DisplayName("Should create a post for a user")
    @Transactional
    @Order(1)
    public void createPostTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setPostText("Some text");

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", userId)
        .when()
                .put()
        .then()
                .statusCode(201);

    }


    @Test
    @DisplayName("Should return 404 when tryinfg to make a post for an inexistent user")
    @Order(2)
    public void postForAnInexistentUserTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setPostText("Some text");

        var inexistentUser = 9999;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", inexistentUser)
        .when()
                .put()
        .then()
                .statusCode(404);

    }


    @Test
    @DisplayName("Should return 404 when user doesnt exist")
    @Order(3)
    public void listPostUserNotFoundTest(){

        var inexistentUser = 9999;
        var inexistentFollower = 9999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUser)
                .header("followerId", inexistentFollower)
        .when()
                .get()
        .then()
                .statusCode(404);

    }

//    @Test
//    @DisplayName("Should return 400 when followerId headear is not present")
//    @Order(4)
//    public void listPostFollowerHeaderNotSentTest(){
//
//        var inexistentFollower = 9999;
//
//        given()
//                .contentType(ContentType.JSON)
//                .pathParam("userId", userId)
//                .header("followerId","")
//        .when()
//                .get()
//        .then()
//                .statusCode(400)
//                .body(Matchers.is("You forgot the header followerId"));
//
//    }

    @Test
    @DisplayName("Should return 404 when Follower doesnt exist ")
    @Order(5)
    public void listPostFollowerNotFoundTest(){

        var inexistentFollower = 9999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .header("followerId", inexistentFollower)
        .when()
                .get()
        .then()
                .statusCode(404)
                .body(Matchers.is("Follower not found"));

    }

    @Test
    @DisplayName("Should return 403 when Follower doesnt follow the user ")
    @Order(6)
    public void listPostFollowerDontFollowTheUserTest(){
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .header("followerId", unfollowerId)
        .when()
                .get()
        .then()
                .statusCode(403);

    }

    @Test
    @DisplayName("Should return 403 when Follower and user are the same ")
    @Order(7)
    public void listPostFollowerAndUserAreTheSameTest(){
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .header("followerId", userId)
        .when()
                .get()
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Should return 200 with some posts ")
    @Order(8)
    public void listPostTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setPostText("Some text");

        var inexistentUser = 9999;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", userId)
                .header("followerId", followerId)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("size()", Matchers.greaterThanOrEqualTo(1));

    }

}