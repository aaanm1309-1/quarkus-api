package com.adrianomenezes.quarkussocial.rest.dto;


import lombok.Data;


import javax.validation.constraints.NotNull;

@Data
public class CreateFollowerRequest {

    @NotNull(message = "Follower Id is required")
    private Long followerId;


}
