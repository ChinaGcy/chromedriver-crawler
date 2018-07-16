package com.sdyk.ai.crawler.model.witkey;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sdyk.ai.crawler.model.Model;
import com.sdyk.ai.crawler.util.Range;
import one.rewind.db.DBName;

import java.util.Date;

/**
 * 服务
 */
@DBName(value = "sdyk_raw")
@DatabaseTable(tableName = "cases")
public class Case extends Model {

	// 服务商id
	@DatabaseField(dataType = DataType.STRING, width = 32)
	public String user_id;

	// 名称
	@DatabaseField(dataType = DataType.STRING, width = 64)
	public String title;

	// 类型描述
	@DatabaseField(dataType = DataType.STRING, width = 128)
	public String category;

	// 标签
	@DatabaseField(dataType = DataType.STRING, width = 512)
	public String tags;
	// 描述
	@DatabaseField(dataType = DataType.STRING, columnDefinition = "TEXT")
	public String content;

	// 周期
	// TODO 应该以天数为单位，float类型
	// 1天 = 8小时
	@DatabaseField(dataType = DataType.STRING, width = 16)
	public String time_limit;

	// 响应时间
	@DatabaseField(dataType = DataType.STRING, width = 32)
	public String response_time;

	// 服务态度
	@DatabaseField(dataType = DataType.DOUBLE)
	public double service_attitude;

	// 工作速度
	@DatabaseField(dataType = DataType.DOUBLE)
	public double work_speed;

	// 完成质量
	@DatabaseField(dataType = DataType.DOUBLE)
	public double complete_quality;

	// 购买人数
	@DatabaseField(dataType = DataType.INTEGER)
	public int purchase_num;

	// 预算上限
	@DatabaseField(dataType = DataType.DOUBLE)
	public transient double budget_ub;

	// 预算下限
	@DatabaseField(dataType = DataType.DOUBLE)
	public transient double budget_lb;

	public Range budget;

	// 客户评分
	@DatabaseField(dataType = DataType.FLOAT)
	public float rating;

	// 客户评价数
	@DatabaseField(dataType = DataType.INTEGER)
	public int rate_num;

	public Case(){}

	public Case(String url) {
		super(url);
	}

	public void fullfill() {
		this.budget = new Range(this.budget_lb, this.budget_ub, true);
	}

	/**
	 *
	 * @return
	 */
	public boolean insert() {

		this.fullfill();

		return super.insert();
	}
}