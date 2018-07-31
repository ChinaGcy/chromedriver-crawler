package com.sdyk.ai.crawler.specific.proginn.task.scanTask;

import com.google.common.collect.ImmutableMap;
import com.sdyk.ai.crawler.Distributor;
import com.sdyk.ai.crawler.HttpTaskPoster;
import com.sdyk.ai.crawler.model.TaskTrace;
import com.sdyk.ai.crawler.specific.proginn.task.modelTask.ServiceProviderTask;
import one.rewind.io.requester.chrome.ChromeDriverDistributor;
import one.rewind.io.requester.exception.ProxyException;
import one.rewind.io.requester.task.ChromeTask;
import one.rewind.io.requester.task.ChromeTaskHolder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceScanTask extends ScanTask {

	public static long MIN_INTERVAL = 60 * 60 * 1000L;

	static {
		registerBuilder(
				ServiceScanTask.class,
				"https://www.proginn.com/page/{{page}}",
				ImmutableMap.of("page", String.class, "max_page", String.class),
				ImmutableMap.of("page", "", "max_page", "2")
		);
	}

	public ServiceScanTask(String url) throws MalformedURLException, URISyntaxException, ProxyException.Failed {

		super(url);

		this.setPriority(Priority.HIGH);

		this.setNoFetchImages();

		this.setParam("page", url.split("page/")[1]);

		this.addDoneCallback((t) -> {

			//获取当前页数
			int page = Integer.valueOf(url.split("page/")[1]);

			Document doc = getResponse().getDoc();

			Elements userList = doc.getElementsByClass("item J_user");

			for(Element element : userList){

				String uId = element.getElementsByClass("user-avatar").attr("userid");

				try {

					//设置参数
					Map<String, Object> init_map = new HashMap<>();
					init_map.put("servicer_id", uId);

					Class<? extends ChromeTask> clazz =  (Class<? extends ChromeTask>) Class.forName("com.sdyk.ai.crawler.specific.proginn.task.modelTask.ServiceProviderTask");

					//生成holder
					ChromeTaskHolder holder = ChromeTask.buildHolder(clazz, init_map);

					//提交任务
					((Distributor)ChromeDriverDistributor.getInstance()).submit(holder);

				} catch ( Exception e) {

					logger.error("error for submit ProjectTask.class", e);
				}

			}

			Elements pageList= doc.getElementsByClass("item");
			String pageLast = pageList.get(pageList.size() - 1).text();

			String maxPageSrc =  String.valueOf(((ChromeTask) t).init_map.get("max_page"));

			// 不含 max_page 参数，则表示可以一直翻页
			if( maxPageSrc.length() < 1 ){
				if( !pageLast.equals("...") ){

					int next = page + 1;

					try {

						//设置参数
						Map<String, Object> init_map = new HashMap<>();
						init_map.put("page", String.valueOf(next));
						init_map.put("max_page", "");

						Class<? extends ChromeTask> clazz =  (Class<? extends ChromeTask>) Class.forName("com.sdyk.ai.crawler.specific.proginn.task.scanTask.ServiceScanTask");

						//生成holder
						ChromeTaskHolder holder = ChromeTask.buildHolder(clazz, init_map);

						//提交任务
						((Distributor)ChromeDriverDistributor.getInstance()).submit(holder);

					} catch ( Exception e) {

						logger.error("error for submit scanTaskServiceScanTask.class", e);
					}

				}
			}
			else {
				int maxPage = Integer.valueOf(maxPageSrc);
				int current_page = Integer.valueOf(String.valueOf(((ChromeTask) t).init_map.get("page")));

				// 从当前页翻至最大页
				for(int i = current_page + 1; i <= maxPage; i++) {

					Map<String, Object> init_map = new HashMap<>();
					init_map.put("page", String.valueOf(i));
					init_map.put("max_page", "0");

					ChromeTaskHolder holder = ((ChromeTask) t).getHolder(((ChromeTask) t).getClass(), init_map);

					ChromeDriverDistributor.getInstance().submit(holder);
				}
			}

		});
	}

	@Override
	public TaskTrace getTaskTrace() {

		return new TaskTrace(this.getClass(), "all", this.getParamString("page"));
	}

}
