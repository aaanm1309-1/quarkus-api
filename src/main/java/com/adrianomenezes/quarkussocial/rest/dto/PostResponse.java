package com.adrianomenezes.quarkussocial.rest.dto;

import com.adrianomenezes.quarkussocial.domain.model.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponse {

    private String postText;
    private LocalDateTime dateTime;

    public static PostResponse fromEntity(Post post) {
        var response = new PostResponse();
        response.setPostText(post.getPostText());
        response.setDateTime(post.getDateTime());

        return response;
    }
}
