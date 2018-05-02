package com.sdyk.ai.crawler;

import com.sdyk.ai.crawler.zbj.Scheduler;
import com.sdyk.ai.crawler.zbj.docker.DockerHostManager;
import com.sdyk.ai.crawler.zbj.docker.model.DockerHost;
import com.sdyk.ai.crawler.zbj.proxy.AliyunHost;
import com.sdyk.ai.crawler.zbj.proxy.ProxyManager;
import org.junit.Test;

public class SchedulerTest {

	@Test
	public void test() throws Exception {

		DockerHost host = DockerHostManager.getInstance().getHostByIp("10.0.0.62");
		DockerHostManager.getInstance().delAllDockerContainers(host);

		ProxyManager.getInstance().deleteProxyByGroup(AliyunHost.Proxy_Group_Name);

		AliyunHost.stopAndDeleteAll();

		Scheduler scheduler = new Scheduler(1);
		Thread.sleep(6000000);

	}

	@Test
	public void scheduleTest() {
		Scheduler.getInstance(1);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
