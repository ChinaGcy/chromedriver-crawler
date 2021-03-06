package com.sdyk.ai.crawler;

import com.sdyk.ai.crawler.route.*;
import com.sdyk.ai.crawler.specific.zbj.task.modelTask.GetProjectContactTask;
import one.rewind.io.requester.chrome.ChromeDriverDistributor;
import one.rewind.io.requester.route.ChromeTaskRoute;
import one.rewind.io.server.MsgTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;

import java.security.Provider;

import static spark.Spark.*;

public class ServiceWrapper {

	public static final Logger logger = LogManager.getLogger(ServiceWrapper.class.getName());

	private static ServiceWrapper instance;

	public static ServiceWrapper getInstance() {

		synchronized (ServiceWrapper.class) {
			if (instance == null) {
				instance = new ServiceWrapper();
			}
		}

		return instance;
	}

	private int port = 80;

	/**
	 * 定义路由路径和请求方法
	 */
	public ServiceWrapper() {

		port(port);

		// TODO 在Scheduler中执行更好
		ChromeDriverDistributor.instance = new Distributor();

		//
		ChromeTaskRoute.setFilter((class_name, user) -> {

			if (class_name.equals(GetProjectContactTask.class.getName()) && user == null) {

				throw new Exception("Can not get Phone, username is null");
			}

		});


		before("/*", (q, a) -> logger.info("Received api call"));


		get("/projects/:page", ProjectRoute.getProjects, new ModelMsgTransformer());

		// 需求详情
		get("/project/:id", ProjectRoute.getProjectById, new ModelMsgTransformer());

		// 下载文件
		get("/binarys/:id", BinaryRoute.getBinaryForId, new ModelMsgTransformer());

		// 雇主列表
		get("/tenderers/:page", TendererRoute.getTenderers, new ModelMsgTransformer());

		// 单个雇主信息
		path("/tenderer", () -> {

			// 雇主详情
			get("/:id", TendererRoute.getTendererById, new ModelMsgTransformer());

			// 雇主评价列表
			get("/:id/rating/:page", TendererRatingRoute.getTendererRatings, new ModelMsgTransformer());

			// TODO 雇主的历史项目信息

		});

		// 服务商列表
		get("/service_suppliers/:page", ServiceSupplierRoute.getServiceSuppliers, new ModelMsgTransformer());

		// 服务商信息
		path("/service_supplier", () -> {

			get("/:id", ServiceSupplierRoute.getServiceById, new MsgTransformer());

			// 服务商案例列表
			get("/:id/cases/:page", CaseRoute.getCases, new ModelMsgTransformer());

			// 服务商案例
			get("/case/:id", CaseRoute.getCaseById, new ModelMsgTransformer());

			// 服务商服务
			get("/:id/works/:page", WorkRoute.getWorks, new ModelMsgTransformer());

			get("/work/:id", WorkRoute.getWorkById, new ModelMsgTransformer());

			// 服务商评价
			get("/:id/rating/:page", ServiceSupplierRatingRoute.getServiceSupplierRatings, new ModelMsgTransformer());

		});

		// 系统信息
		path("/system", () -> {

			// 队列信息
			get("/queue", SystemRoute.getQueueSize, new ModelMsgTransformer());

			// 任务执行统计
			get("/taskQueueStat", SystemRoute.getTaskStat, new ModelMsgTransformer());

			// Agent-domains 统计
			get("/agentInformation", SystemRoute.getAgentInformation, new ModelMsgTransformer());

			// domain-Agents 统计
			get("/domainInformation", SystemRoute.getDomainInformation, new ModelMsgTransformer());

			// proxy-domains 统计
			get("/proxyInformation", SystemRoute.getProxyInformation, new ModelMsgTransformer());

			// agent-taskNum 统计
			get("/agentTaskNum", SystemRoute.getAgentTaskNum, new ModelMsgTransformer());
		});

		// 查询地区
		get("/location/parse", LocationParserRoute.parseLocation, new MsgTransformer());

		// 适用于跨域调用
		after((request, response) -> {

			response.header("Access-Control-Allow-Origin", "*");
			response.header("Access-Control-Allow-Methods", "POST, OPTIONS");
			response.header("Access-Control-Allow-Headers", "X-Custom-Header");
			response.header("Access-Control-Max-Age", "1000");

		});

	}

	public static void main(String[] args) {
		ServiceWrapper.getInstance();
		((Distributor) ChromeDriverDistributor.getInstance()).buildHttpApiServer();
	}
}
