package co.blastlab.cityhack.fb;

import co.blastlab.cityhack.fb.dao.CommentDao;
import co.blastlab.cityhack.fb.dao.WitRequestDao;
import co.blastlab.cityhack.fb.dao.WitResponseDao;
import co.blastlab.cityhack.fb.fb.Comment;
import co.blastlab.cityhack.fb.fb.Model;
import co.blastlab.cityhack.fb.fb.PageId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class HelloController {

	// generate token: https://developers.facebook.com/tools/explorer/
	private static String ACCESS_TOKEN = "EAACEdEose0cBAPpNR7W06oraiIn17QJc2opKkWUisPBOGfNxiM42UtzDq5hBgZCyUBkskMPVaBUQHGlPqB72HdxKVO7of5ZAZAAx1PQsy2IiZCOqNIIBuWZAIUZCWn5OZCoDZBnKxDGt1Df2oMJ8RMgQhvDQP8xQ34aB4ZCZCUUqhYPS7mt7iM0jYk0qGV9ucZBZBobCpD0ghJpozQZDZD";
	private static String BASE_URL = "https://graph.facebook.com/v3.0/";
	private static String COMMENTS_EDGE = "comments";
	private static String[] FIELDS = {"id", "created_time", "message", "permalink_url", "like_count"};
	private static int LIMIT = 100;

	private static String WIT_URL = "http://192.168.43.157:5000/message";

	/**
	 * Example request: http://localhost:8090/?postUrl=https://www.facebook.com/gdansk/posts/10160555313985424
	 */
	@GetMapping("/")
	public List<CommentDao> index(@RequestParam String postUrl) throws MalformedURLException {
		try {
			String pageId = findPageId(postUrl);
			String postId = findPostId(postUrl);

			String url = buildUrl(pageId, postId);

			List<CommentDao> comments = fetchComments(url);

			recognizeEmotions(comments);

			return comments.stream()
				.filter(c -> !Value.NOT_FOUND.equals(c.getValue()))
				.collect(Collectors.toList());
		} catch (HttpClientErrorException e) {
			System.out.println(e.getResponseBodyAsString());
			throw e;
		}
	}

	private void recognizeEmotions(List<CommentDao> comments) {
		comments.forEach(comment -> {
			WitRequestDao requestDao = new WitRequestDao();
			requestDao.setId(comment.getCommentId());
			requestDao.setMessage(comment.getMessage());

			RestTemplate rt = new RestTemplate();
			WitResponseDao response = rt.postForObject(WIT_URL, requestDao, WitResponseDao.class);

			comment.setConfidence(response.getConfidence());
			comment.setValue(response.getValue());
		});
	}

	private List<CommentDao> fetchComments(String url) {
		RestTemplate restTemplate = new RestTemplate();
		Model model = restTemplate.getForObject(url, Model.class);

		List<Comment> comments = new ArrayList<>(model.getData());
		model.getData().stream().map(Comment::getComments).filter(Objects::nonNull).forEach(m -> comments.addAll(m.getData()));

		return comments.stream().map(c -> new CommentDao(
			c.getId(),
			ZonedDateTime.parse(c.getCreatedTime().substring(0, 22) + ":" + c.getCreatedTime().substring(22)),
			c.getMessage(),
			c.getPermalinkUrl(),
			c.getLikeCount(),
			0, null
		)).collect(Collectors.toList());
	}

	private String findPageId(String postUrl) throws MalformedURLException {
		URL u = new URL(postUrl);
		String path = u.getPath();
		String pageName = path.substring(1, path.indexOf('/', 1));

		String url = BASE_URL + pageName + "?fields=id&access_token=" + ACCESS_TOKEN;

		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(url, PageId.class).getId();
	}

	private String findPostId(String postUrl) throws MalformedURLException {
		URL u = new URL(postUrl);
		String path = u.getPath();
		return path.substring(path.lastIndexOf('/') + 1);
	}

	private String buildUrl(String pageId, String postId) {
		StringBuilder sb = new StringBuilder(BASE_URL + pageId + "_" + postId + "/" + COMMENTS_EDGE);

		sb.append("?fields=");
		for (String field: FIELDS) {
			sb.append(field).append(",");
		}

		sb.append("comments.limit(").append(LIMIT).append(")").append("&7B");
		for (String field: FIELDS) {
			sb.append(field).append(",");
		}
		sb = new StringBuilder(sb.substring(0, sb.length() - 1))
			.append("&7D");

		sb
			.append("&limit=").append(LIMIT)
			.append("&access_token=").append(ACCESS_TOKEN);

		return sb.toString();
	}
}
