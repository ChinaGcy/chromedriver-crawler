package com.sdyk.ai.crawler.requester.test;

import com.sdyk.ai.crawler.Requester;
import com.sdyk.ai.crawler.proxy.AliyunHost;
import com.sdyk.ai.crawler.proxy.ProxyManager;
import com.sdyk.ai.crawler.proxy.exception.NoAvailableProxyException;
import com.sdyk.ai.crawler.proxy.model.ProxyImpl;
import com.sdyk.ai.crawler.specific.zbj.task.Task;

import one.rewind.io.requester.chrome.ChromeDriverAgent;
import one.rewind.io.requester.proxy.Proxy;
import org.junit.Before;
import org.junit.Test;

public class ProxyFailedTest {


	@Before
	public void buildAliyunHostProxy() throws InterruptedException {
		/*for(AliyunHost host : AliyunHost.getAll()) {
			host.stopAndDelete();
		}*/
		AliyunHost.batchBuild(4);
	}

	/**
	 * 测试代理更换
	 */
	@Test
	public void test() throws Exception {

		ProxyImpl proxy = ProxyManager.getInstance().getValidProxy(AliyunHost.Proxy_Group_Name);

		proxy.setFailedCallback(()->{

			if(proxy.source == ProxyImpl.Source.ALIYUN_HOST) {

				AliyunHost aliyunHost = null;

				try {

					aliyunHost = AliyunHost.getByHost(proxy.host);
					aliyunHost.stop();

				} catch (Exception e) {
					Requester.logger.error("AliyunHost:{} Error, ", proxy.host, e);
				}

				// 2个Faild 1个Busy 1个Free
				if(ProxyManager.getInstance().getValidProxyNum() < 2) {
					try {
						AliyunHost.batchBuild(2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

		ChromeDriverAgent agent = new ChromeDriverAgent(proxy);
		Task task = new Task("https://www.baidu.com/s?wd=ip");

		task.setBuildDom();

		agent.addProxyFailedCallback((t,a) -> {

			try {

				agent.proxy.failed();

				ProxyImpl p1 = ProxyManager.getInstance().getValidProxy(AliyunHost.Proxy_Group_Name);

				//
				p1.setFailedCallback(()->{

					if(p1.source == ProxyImpl.Source.ALIYUN_HOST) {

						AliyunHost aliyunHost = null;

						try {

							aliyunHost = AliyunHost.getByHost(p1.host);
							aliyunHost.stop();

						} catch (Exception e) {
							Requester.logger.error("AliyunHost:{} Error, ", p1.host, e);
						}

						if(ProxyManager.getInstance().getValidProxyNum() < 2) {
							try {
								AliyunHost.batchBuild(2);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				});

				agent.changeProxy(p1);
			}
			catch (NoAvailableProxyException e) {
				System.err.println("No Available Proxy, exit.");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});

		/*ChromeDriverRequester requester = ChromeDriverRequester.getInstance();
		requester.addAgent(agent);
		agent.start();

		for (int i = 0; i<10; i++) {
			requester.submit(task);
		}*/

		Thread.sleep(1000000);
	}

	/**
	 * 当代理不可用,更换代理，代理不够，从阿里云申请新的代理服务器放入数据库中使用
	 */
	@Test
	public void getAliyunTest() throws Exception {

		ProxyImpl p = ProxyManager.getInstance().getValidProxy(AliyunHost.Proxy_Group_Name);

		ChromeDriverAgent agent = new ChromeDriverAgent(p);
		Task task = new Task("https://www.baidu.com/s?wd=ip");

		agent.addProxyFailedCallback((t,a) -> {

			try {
				agent.proxy.status = Proxy.Status.INVALID;
				agent.proxy.update();

				int i = ProxyManager.getInstance().getValidProxyNum();

				if (i < 2) {
					AliyunHost.batchBuild(1);
				}

				ProxyImpl p1 = ProxyManager.getInstance().getValidProxy(AliyunHost.Proxy_Group_Name);
				agent.changeProxy(p1);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});

		/*ChromeDriverRequester requester = ChromeDriverRequester.getInstance();
		requester.addAgent(agent);
		agent.start();

		for (int i = 0; i<6; i++) {
			requester.submit(task);*/
		}

/*
		Thread.sleep(10000000);
*/
	}


