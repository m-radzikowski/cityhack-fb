package co.blastlab.cityhack.fb.fb;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class Comment {
	private String id;
	private ZonedDateTime createdTime;
	private String message;
	private String permalinkUrl;
	private int likeCount;

	private Model comments;
}
