package com.sdyk.ai.crawler.specific.itijuzi.task;

import com.google.common.collect.ImmutableMap;
import com.sdyk.ai.crawler.model.TaskTrace;
import com.sdyk.ai.crawler.specific.itijuzi.action.MouseSuspensionAction;
import com.sdyk.ai.crawler.specific.mihuashi.action.MihuashiLoginAction;
import com.sdyk.ai.crawler.specific.proLagou.task.scanTask.ProjectScanTask;
import com.sdyk.ai.crawler.task.ScanTask;
import com.sdyk.ai.crawler.task.Task;
import one.rewind.io.requester.chrome.ChromeDriverDistributor;
import one.rewind.io.requester.chrome.action.ClickAction;
import one.rewind.io.requester.chrome.action.SetValueAction;
import one.rewind.io.requester.exception.ProxyException;
import one.rewind.io.requester.task.ChromeTask;
import one.rewind.io.requester.task.ChromeTaskHolder;
import one.rewind.txt.DateFormatUtil;
import one.rewind.util.FileUtil;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyListScanTask extends ScanTask {

	public static long MIN_INTERVAL = 1000L;

	static {
		registerBuilder(
				CompanyListScanTask.class,
				"http://radar.itjuzi.com/company/{{page}}",
				ImmutableMap.of("page", String.class),
				ImmutableMap.of("page", "1")
		);
	}

	public CompanyListScanTask(String url) throws MalformedURLException, URISyntaxException, ProxyException.Failed {

		super(url.replaceAll("/\\d+$", ""));

		String page = url.split("company/")[1];

		this.setPriority(Priority.HIGH);

		this.setBuildDom();

		this.addAction(new MouseSuspensionAction("body > div.company-main > div.filter-box > ul > li:nth-child(3)"));

		// 选择地点
		for( int i = 1; i<4; i++ ){

			this.addAction(
					new ClickAction("body > div.company-main > div.filter-box > ul > li:nth-child(3) > ul > li:nth-child("+i+")",
					2000
					)
			);
		}

		/*this.addAction(new MouseSuspensionAction("body > div.company-main > div.filter-box > ul > li:nth-child(4)"));

		//选择融资轮次
		for(int j =1 ; j < 4 ; j++){
			this.addAction(new
					ClickAction("body > div.company-main > div.filter-box > ul > li:nth-child(4) > ul > li:nth-child("+j+")",
					5000
			));
		}*/

		//填写跳转页数
		this.addAction(new SetValueAction(
				"#goto_page_num", page, 2000
		));

		//点击跳转按钮
		this.addAction(new ClickAction("#goto_page_btn",8000));

		this.addDoneCallback((t) -> {

			Document doc = t.getResponse().getDoc();
			proc(doc, Integer.valueOf(page));

		});

	}

	public void proc(Document doc, Integer page) throws Exception {

		int next = page + 1;

		Elements company_list = doc.select("a.logo-box");

		System.err.println(getResponse().getVar("json"));

		for( int i = 2; i<company_list.size(); i++ ){

			String url = company_list.get(i).attr("href").split("company/")[1];
			System.err.println(url);

			/*Map<String, Object> init_map = new HashMap<>();

			init_map.put("id", url);

			Class clazz = Class.forName("com.sdyk.ai.crawler.specific.itijuzi.task.CompanyTask");

			ChromeTaskHolder holder = ChromeTask.buildHolder(clazz, init_map );

			ChromeDriverDistributor.getInstance().submit(holder);*/

		}

		if( company_list.size() > 0 ){

			FileUtil.appendLineToFile(DateFormatUtil.dff.print(System.currentTimeMillis()) + "\t"+ "当前页 ：" + page  ,
					"page.txt");

			if( next <= 2168 ){

				Map<String, Object> init_map = new HashMap<>();
				init_map.put("page", String.valueOf(page ++));

				Class clazz = Class.forName("com.sdyk.ai.crawler.specific.itijuzi.task.CompanyListScanTask");

				ChromeTaskHolder holder = ChromeTask.buildHolder(clazz, init_map );

				ChromeDriverDistributor.getInstance().submit(holder);

			}

		}

	}

	/**
	 * 判断是否为最大页数
	 *
	 * @param path
	 * @param page
	 * @return
	 */
	@Override
	public boolean pageTurning(String path, int page) {
		return false;
	}

	/**
	 * 获取ScanTask 标识
	 *
	 * @return
	 */
	@Override
	public TaskTrace getTaskTrace() {
		return null;
	}

	public static void registerBuilder(Class<? extends ChromeTask> clazz, String url_template, Map<String, Class> init_map_class, Map<String, Object> init_map_defaults){
		ChromeTask.registerBuilder( clazz, url_template, init_map_class, init_map_defaults );
	}

}
