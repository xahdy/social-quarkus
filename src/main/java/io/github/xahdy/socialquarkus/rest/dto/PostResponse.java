package io.github.xahdy.socialquarkus.rest.dto;

import io.github.xahdy.socialquarkus.domain.model.Post;
import lombok.Data;

import javax.json.bind.annotation.JsonbDateFormat;
import java.time.LocalDateTime;

@Data
public class PostResponse {
    private String text;
    @JsonbDateFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

    public static PostResponse fromEntity(Post post){
        var response = new PostResponse();
        response.setText(post.getText());
        response.setDateTime(post.getDateTime());
        return response;
    }
}
