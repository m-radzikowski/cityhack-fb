package co.blastlab.cityhack.fb.fb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
public class Comment {
	private String id;

	@JsonProperty("created_time")
	private String createdTime;

	private String message;

	@JsonProperty("permalink_url")
	private String permalinkUrl;

	@JsonProperty("like_count")
	private int likeCount;

	private Model comments;
}
