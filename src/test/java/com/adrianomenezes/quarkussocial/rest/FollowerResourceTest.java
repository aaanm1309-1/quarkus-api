package com.adrianomenezes.quarkussocial.rest;

import com.adrianomenezes.quarkussocial.domain.model.Follower;
import com.adrianomenezes.quarkussocial.domain.model.User;
import com.adrianomenezes.quarkussocial.domain.repository.FollowerRepository;
import com.adrianomenezes.quarkussocial.domain.repository.UserRepository;
import com.adrianomenezes.quarkussocial.rest.dto.CreateFollowerRequest;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    Long userId;
    Long followerId;
    Long unfollowerId;

    @BeforeEach
    @Transactional
    void setUp(){
        var user = new User();
        user.setAge(25);
        user.setName("Fulaninho");

        var follower = new User();
        follower.setAge(25);
        follower.setName("Seguidor");

        userRepository.persist(user);
        userId = user.getId();

        userRepository.persist(follower);
        followerId = follower.getId();

        Follower newFollower = new Follower();
        newFollower.setUser(user);
        newFollower.setFollower(follower);

        followerRepository.persist(newFollower);


    }

    @Test
    @DisplayName("Should return 409 when follower is equal to User id")
    @Order(1)
    public void sameUserAsFollowerTest(){
        var body = new CreateFollowerRequest();
        body.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
            .when()
                .put()
            .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("You can't follow yourself"));

    }

    @Test
    @DisplayName("Should return 404 when User doesn't exist")
    @Order(2)
    public void userNotFoundTest(){
        var body = new CreateFollowerRequest();
        body.setFollowerId(userId);

        var inexistentUser = 9999L;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", inexistentUser)
        .when()
                .put()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .body(Matchers.is("User not found"))
        ;

    }

    @Test
    @DisplayName("Should return 404 when Follower doesn't exist")
    @Order(3)
    public void followerNotFoundTest(){
        var inexistentUser = 9999L;

        var body = new CreateFollowerRequest();
        body.setFollowerId(inexistentUser);


        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
        .when()
                .put()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .body(Matchers.is("Follower not found"))
        ;

    }

    @Test
    @DisplayName("Should return 200 when Follower follow a user")
    @Order(4)
    public void followerUserTest(){

        var body = new CreateFollowerRequest();
        body.setFollowerId(followerId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
        .when()
                .put()
        .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode())
        ;

    }

    @Test
    @DisplayName("Should return 404 on List Followers when User doesn't exist")
    @Order(5)
    public void userNotFoundListingFollowersTest(){

        var inexistentUser = 9999L;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUser)
        .when()
                .get()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
        ;

    }

    @Test
    @DisplayName("Should return 200 on List Followers")
    @Order(6)
    public void userListingFollowersTest(){
//        given()
//                .contentType(ContentType.JSON)
//                .pathParam("userId", userId)
//        .when()
//                .get()
//        .then()
//                .statusCode(Response.Status.OK.getStatusCode())
//                .body("size()", Matchers.greaterThanOrEqualTo(1));
//        ;

        var response =
            given()
                    .contentType(ContentType.JSON)
                    .pathParam("userId", userId)
            .when()
                    .get()
            .then()
                    .extract().response()
            ;

        var followersCount =  response.jsonPath().get("followersCount");
        var followersContent =  response.jsonPath().getList("content");
        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, followersCount);
        assertEquals(1, followersContent.size());

    }



    @Test
    @DisplayName("Should return 404 when Unfollowing a user and User doesn't exist ")
    @Order(7)
    public void userNotFoundWhenUnfollowingAUserTest(){
        var inexistentUser = 9999L;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUser)
                .queryParam("followerId", userId)
        .when()
                .delete()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .body(Matchers.is("User not found"))
        ;

    }

    @Test
    @DisplayName("Should return 404 when Unfollowing a user and Follower doesn't exist ")
    @Order(8)
    public void followerNotFoundWhenUnfollowingAUserTest(){

        var inexistentFollower = 9999L;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .queryParam("followerId", inexistentFollower)
        .when()
                .delete()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .body(Matchers.is("Follower not found"))
        ;

    }

    @Test
    @DisplayName("Should return 409 when Unfollowing a user and Follower is the same")
    @Order(9)
    public void followerAndUserAreTheSameWhenUnfollowingAUserTest(){

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .queryParam("followerId", userId)
        .when()
                .delete()
        .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("You can't unfollow yourself"))
        ;

    }

    @Test
    @DisplayName("Should return 204 when Unfollowing a user")
    @Order(10)
    public void unfollowAUserTest(){

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .queryParam("followerId", followerId)
        .when()
                .delete()
        .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode())
        ;

    }

}