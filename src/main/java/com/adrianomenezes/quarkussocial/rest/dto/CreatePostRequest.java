package com.adrianomenezes.quarkussocial.rest.dto;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreatePostRequest {

    @NotBlank(message = "Post text is required")
    private String postText;


}
