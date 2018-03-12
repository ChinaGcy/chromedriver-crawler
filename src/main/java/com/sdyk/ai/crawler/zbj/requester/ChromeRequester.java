package com.sdyk.ai.crawler.zbj.requester;

import com.sdyk.ai.crawler.zbj.model.Account;
import com.sdyk.ai.crawler.zbj.model.Proxy;
import com.sdyk.ai.crawler.zbj.task.scanTask.ServiceScanTask;
import com.sdyk.ai.crawler.zbj.task.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * 调用ChromeDriver
 */
public class ChromeRequester {

	protected static ChromeRequester instance;

	private static final Logger logger = LogManager.getLogger(ChromeRequester.class.getName());

	// 开启线程数，开启几个ChromeDriver
	private int agentCount = 4;

	public String domain = "zbj.com";

	//
	private List<ChromeDriverLoginWrapper> agents = new LinkedList<>();

	/**
	 * 单例模式
	 *
	 * @return
	 */
	public static ChromeRequester getInstance() {

		if (instance == null) {

			synchronized (ChromeRequester.class) {
				if (instance == null) {
					instance = new ChromeRequester();
				}
			}
		}

		return instance;
	}

	/**
	 *
	 */
	private ChromeRequester() {


		for(int i=0; i<agentCount; i++) {

			ChromeDriverLoginWrapper agent = new ChromeDriverLoginWrapper(domain);

			try {

				// 登录
				agent.login(Account.getAccountByDomain(domain), Proxy.getValidProxy("aliyun"));

				// 执行任务
				agent.start();

				agents.add(agent);

			} catch (Exception e) {

				logger.error("Error while chrome login. ", e);
			}
		}
	}

	/**
	 *最短队列的chrome
	 * @return
	 */
	private ChromeDriverLoginWrapper getDriverWithShortestQueue() {
		int size = Integer.MAX_VALUE;
		ChromeDriverLoginWrapper agent_ = null;
		for(ChromeDriverLoginWrapper agent : agents) {
			if(agent.taskQueue.size() < size) {
				size = agent.taskQueue.size();
				agent_ = agent;
			}
		}

		return agent_;
	}

	/**
	 * 添加任务
	 * @param task
	 */
	public void distribute(Task task) {

		ChromeDriverLoginWrapper agent = this.getDriverWithShortestQueue();
		agent.taskQueue.add(task);
	}
}