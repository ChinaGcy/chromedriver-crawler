package com.sdyk.ai.crawler.specific.zbj.task.test;

import com.sdyk.ai.crawler.model.witkey.Project;
import org.junit.Test;
import one.rewind.db.Refacter;

public class ProjectTaskTest {

	@Test
	public void projectTest() throws Exception {


	}

	@Test
	public void dummyTest() throws Exception {

		Refacter.dropTable(Project.class);
		Refacter.createTable(Project.class);

		Project p = new Project("http://www.baidu.com/NumberExtractTest");
		p.insert();
	}
}
