package com.sdyk.ai.crawler.specific.proLagou.task.scanTask;

import com.google.common.collect.ImmutableMap;
import com.sdyk.ai.crawler.model.TaskTrace;
import com.sdyk.ai.crawler.specific.proLagou.task.modelTask.ProjectTask;
import com.sdyk.ai.crawler.task.Task;
import com.sdyk.ai.crawler.util.URLUtil;
import one.rewind.io.requester.exception.AccountException;
import one.rewind.io.requester.exception.ProxyException;
import org.jsoup.nodes.Document;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectScanTask extends com.sdyk.ai.crawler.task.ScanTask {

	static {
		// init_map_class
		init_map_class = ImmutableMap.of("page", String.class);
		// init_map_defaults
		init_map_defaults = ImmutableMap.of("q", "ip");
		// url_template
		url_template = "https://pro.lagou.com/project/{{page}}";
	}

    public ProjectScanTask(String url) throws MalformedURLException, URISyntaxException, ProxyException.Failed {

		super(url);

        this.setPriority(Priority.HIGH);

        this.addDoneCallback((t) -> {

	        int page = 0;
	        Pattern pattern_url = Pattern.compile("project/(?<page>.+?)");
	        Matcher matcher_url = pattern_url.matcher(url);
	        if (matcher_url.find()) {
		        page = Integer.parseInt(matcher_url.group("page"));
	        }

            Document doc = getResponse().getDoc();

            String s = doc.select("#project_list > ul").toString();

            Pattern pattern = Pattern.compile("https://pro.lagou.com/project/(?<ProjectId>.+?).html");
            Matcher matcher = pattern.matcher(s);

            while (matcher.find()) {

            	String project_id = matcher.group("ProjectId");

	            try {
		            URLUtil.PostTask(ProjectTask.class,
				            null,
				            ImmutableMap.of("project_id", project_id),
				            null,
				            null,
				            null,
				            null,
				            null);

	            } catch (Exception e) {
		            logger.error("error for URLUtil.PostTask ProjectTask.class", e);
	            }

            }

            String pagePath = "#pager > div > span:nth-child(9)";
            if(pageTurning(pagePath, page)){
                int nextPage = page+1;

	            try {
		            URLUtil.PostTask(ProjectScanTask.class,
				            null,
				            ImmutableMap.of("page", String.valueOf(nextPage)),
				            null,
				            null,
				            null,
				            null,
				            null);

	            } catch (Exception e) {
		            logger.error("error for URLUtil.PostTask ProjectTask.class", e);
	            }
            }

        });
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

        Document doc = getResponse().getDoc();
        int nextPage = 0;
        try{
            String mexPage = doc.select(path).text();
            nextPage = Integer.valueOf(mexPage);
        }catch (Exception e){
            return false;
        }

        return nextPage>page;
    }

    @Override
    public TaskTrace getTaskTrace() {
        return null;
    }
}
