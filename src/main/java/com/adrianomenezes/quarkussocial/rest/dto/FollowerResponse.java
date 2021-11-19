package com.adrianomenezes.quarkussocial.rest.dto;

import com.adrianomenezes.quarkussocial.domain.model.Follower;
import com.adrianomenezes.quarkussocial.domain.model.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowerResponse {

    private Long id;
    private String name;

    public FollowerResponse() {
    }

    public FollowerResponse(Follower follower) {
//        this(follower.getFollower().getId(),follower.getFollower().getName());
        this(follower.getId(),follower.getFollower().getName());
    }

    public FollowerResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
