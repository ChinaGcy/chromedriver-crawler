package com.sdyk.ai.crawler.specific.zbj.task.scanTask;

import com.google.common.collect.ImmutableMap;
import com.sdyk.ai.crawler.HttpTaskPoster;
import com.sdyk.ai.crawler.model.TaskTrace;
import com.sdyk.ai.crawler.specific.zbj.task.modelTask.ServiceProviderRatingTask;
import com.sdyk.ai.crawler.specific.zbj.task.modelTask.ServiceProviderTask;
import com.sdyk.ai.crawler.specific.zbj.task.modelTask.TendererOrderTask;
import one.rewind.io.requester.chrome.ChromeDriverDistributor;
import one.rewind.io.requester.exception.ProxyException;
import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 服务商列表
 * 1. 找到url
 * 2. 翻页
 */
public class ServiceScanTask extends ScanTask {

	static {
		/*// init_map_class
		init_map_class = ImmutableMap.of("channel", String.class,"page", String.class);
		// init_map_defaults
		init_map_defaults = ImmutableMap.of("channel", "all", "page", "0");
		// url_template
		url_template = "https://www.zbj.com/{{channel}}/pk{page}.html";*/
		registerBuilder(
				ServiceScanTask.class,
				"https://www.zbj.com/{{channel}}/pk{page}.html",
				ImmutableMap.of("channel", String.class,"page", String.class),
				ImmutableMap.of("channel", "all", "page", "0")
		);
	}

	/*public static ServiceScanTask generateTask(String channel, int page) {

		String url = "https://www.zbj.com/" + channel + "/pk" + page + ".html";

		try {
			ServiceScanTask t = new ServiceScanTask(url, channel, page);
			return t;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return null;
	}*/

	/**
	 *
	 * @param url
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	public ServiceScanTask(String url) throws MalformedURLException, URISyntaxException, ProxyException.Failed {

		super(url);
		this.setPriority(Priority.HIGH);

		this.addDoneCallback((t) -> {

			String channel = null;
			int page = 0;
			Pattern pattern_url = Pattern.compile("https://www.zbj.com/(?<channel>.+?)\\/pk(?<page>.+?).html");
			Matcher matcher_url = pattern_url.matcher(url);
			if (matcher_url.find()) {
				channel = matcher_url.group("channel");
				page = Integer.parseInt(matcher_url.group("page"));
			}

			try {

				String src = getResponse().getText();

				Document document = getResponse().getDoc();

				Pattern pattern = Pattern.compile("//shop.zbj.com/\\d+/");
				Matcher matcher = pattern.matcher(src);

				List<String> list = new ArrayList<>();

				while (matcher.find()) {

					String new_url = "https:" + matcher.group();

					if (!list.contains(new_url)) {

						list.add(new_url);

						ChromeDriverDistributor.getInstance().submit(
								this.getHolder(
										ServiceProviderTask.class,
										ImmutableMap.of("user_id", new_url)));
					}
				}

				// 当前页数
				int i = (page - 1) / 40 + 1;

				// #utopia_widget_18 > div.pagination > ul > li:nth-child(1)
				// 翻页
				if (pageTurning("#utopia_widget_18 > div.pagination > ul > li", i)) {

					/*HttpTaskPoster.getInstance().submit(this.getClass(),
							ImmutableMap.of("channel", channel,"page", String.valueOf(page+40)));*/
					ChromeDriverDistributor.getInstance().submit(
							this.getHolder(
									this.getClass(),
									ImmutableMap.of("channel", channel,"page", String.valueOf(page+40))));
				}
				//logger.info("Task driverCount: {}", tasks.size());

			} catch (Exception e) {
				logger.error(e);
			}
		});
	}
	@Override
	public TaskTrace getTaskTrace() {
		return new TaskTrace(this.getClass(), this.getParamString("channel"), this.getParamString("page"));
	}

}
