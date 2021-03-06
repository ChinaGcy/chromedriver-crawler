package com.sdyk.ai.crawler.docker.test;

import com.sdyk.ai.crawler.Scheduler;
import com.sdyk.ai.crawler.docker.model.DockerHostImpl;
import com.sdyk.ai.crawler.proxy.AliyunHost;
import com.sdyk.ai.crawler.specific.zbj.task.Task;
import one.rewind.db.Refacter;
import one.rewind.io.requester.account.Account;
import one.rewind.io.requester.chrome.ChromeDriverAgent;
import one.rewind.io.requester.exception.ChromeDriverException;
import one.rewind.io.ssh.SshManager;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.sdyk.ai.crawler.docker.DockerHostManager;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class DockerTest {

	@Test
	public void dockerChromedriver() throws Exception {

		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		/*ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");*/

		ChromeDriver agent = new ChromeDriver();

		WebDriver driver = new RemoteWebDriver(new URL("http://10.0.0.62:4444/wd/hub"),capabilities);

		driver.manage().window().maximize();
		driver.get("http://zbj.com");

		String s = driver.findElement(By.cssSelector("#headerTopWarpV1 > div > ul > li:nth-child(3)")).getText();

		System.err.println(s);

		Thread.sleep(3000);
		driver.close();
	}

	@Test
	public void createDockerDB() throws Exception {
		Refacter.createTable(DockerHostImpl.class);
	}


	@Test
	public void dockerRun() throws Exception {

		DockerHostManager dockerHostManager = DockerHostManager.getInstance();
		// 连接docker 创建容器
		// dockerHostManager.createDockerContainers("10.0.0.62", 5);

		// 删除所有容器
		dockerHostManager.delAllDockerContainers(dockerHostManager.getHostByIp("10.0.0.62"));
	}


	@Test
	public void crawlerTest() throws Exception {

		List<AliyunHost> aliyunHosts = AliyunHost.getAll();

		// 删除阿里云服务器
		AliyunHost.stopAndDelete(aliyunHosts);

		// 执行登录操作
		Scheduler scheduler = new Scheduler() {
			/**
			 * @param account
			 * @return
			 * @throws MalformedURLException
			 * @throws URISyntaxException
			 */

			/**
			 * @param agent
			 * @param account
			 * @return
			 * @throws MalformedURLException
			 * @throws URISyntaxException
			 */
			public void getLoginTask(ChromeDriverAgent agent, Account account) throws MalformedURLException, URISyntaxException, ChromeDriverException.IllegalStatusException, InterruptedException {

			}

			/**
			 * @param backtrace
			 * @return
			 */
			public void getTask(boolean backtrace) {

			}

			/**
			 * 获取历史数据
			 */
			public void getHistoricalData() {

			}

			/**
			 * 监控调度
			 */
			public void monitoring() {

			}
		};

		Thread.sleep(300000);

	}

	@Test
	public void delAllContainers() throws Exception {

		DockerHostManager.getInstance().delAllDockerContainers();
	}

	@Test
	public void xdotoolTest() throws Exception {

		SshManager.Host host = new SshManager.Host("10.0.0.62", 22, "root", "sdyk315pr");

		host.connect();

		String out = host.exec("docker exec ChromeContainer-10.0.0.62-1 xdotool mousemove 1000,700");

		System.err.println(out);
	}

}
