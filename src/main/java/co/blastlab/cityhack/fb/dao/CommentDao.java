package co.blastlab.cityhack.fb.dao;

import co.blastlab.cityhack.fb.Value;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDao {

	private String commentId;
	private ZonedDateTime createdTime;
	private String message;
	private String permalinkUrl;
	private int likeCount;

	private double confidence;
	private Value value;
}
