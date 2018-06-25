package co.blastlab.cityhack.fb;

import co.blastlab.cityhack.fb.model.Comment;
import co.blastlab.cityhack.fb.model.Model;
import co.blastlab.cityhack.fb.model.PageId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class HelloController {

	private static String ACCESS_TOKEN = "EAACEdEose0cBANcltlWeZCfuRoBd5kd19FEO0t5ZA114KAbLJlm7vlPLnGNZAeQK9ZAs60oDkjI9tgm4YUEYxZBfA2pdluJTWE6DBuOz7d0J2EgWUIY0fjGD4ekD0oXvtYDtw3QbuFkPitibFYJ8SrPwEQQaDzR32YggU2EOR1lkBYdV8kEtRKcURDXoUPOoZD";
	private static String BASE_URL = "https://graph.facebook.com/v3.0/";
	private static String COMMENTS_EDGE = "comments";
	private static String[] FIELDS = {"id", "created_time", "message", "permalink_url", "like_count"};
	private static int LIMIT = 100;

	/**
	 * Example request: http://localhost:8080/?postUrl=https://www.facebook.com/gdansk/posts/10160555313985424
	 */
	@GetMapping("/")
	public List<Comment> index(@RequestParam String postUrl) throws MalformedURLException {
		String pageId = findPageId(postUrl);
		String postId = findPostId(postUrl);

		String url = buildUrl(pageId, postId);

		RestTemplate restTemplate = new RestTemplate();
		Model model = restTemplate.getForObject(url, Model.class);

		List<Comment> comments = new ArrayList<>(model.getData());
		model.getData().stream().map(Comment::getComments).filter(Objects::nonNull).forEach(m -> comments.addAll(m.getData()));
		comments.forEach(comment -> comment.setComments(null));

		return comments;
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
