package com.sdyk.ai.crawler.specific.zbj.task.test;


import org.junit.Test;

public class ProjectScanTaskTest {

	@Test
	public void testProjectScanTest() {

		/*// 并发量
		StatManager statManager = StatManager.getInstance();

		try {
			ChromeDriverAgent agent = (new ChromeDriverLoginWrapper("zbj.com")).login(null, null);
			Queue<Task> taskQueue = new LinkedList<>();
			taskQueue.add(ProjectScanTask.generateTask("t-dhsjzbj", 1, null));

			while (!taskQueue.isEmpty()) {
				Task t = taskQueue.poll();
				if (t != null) {
					try {
						statManager.count();
						agent.fetch(t);
						for (Task t_ : t.postProc(agent.getDriver())) {
							taskQueue.add(t_);
							*//*agent.fetch(t_);*//*
						}

					} catch (Exception e) {
						e.printStackTrace();
						taskQueue.add(t);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	@Test
	public void pageTest() {



	}
}
