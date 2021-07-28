package com.tian;

import java.awt.Robot;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;
import com.tian.dao.BaseDao;
import com.tian.util.HttpSendRequest;
import com.tian.util.StringHelpTool;
import net.sf.json.JSONSerializer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.tian.util.JdbcUtils;

public class test {

	public static void main(String[] args) {
		System.out.println(JdbcUtils.getConnection());
		String HttpDoc = HttpSendRequest.sendRequest("http://www.boyaweb.com/?content=gat", "GET");

		HttpDoc = StringHelpTool.getIntermediateList(HttpDoc, "<div class=\"pc_body_content\">", "<!-- 电脑底部信息内容 -->",
				null, null, 0, true).get(0);

		// 得到总体车型列表
		List<String> modelList = StringHelpTool.getIntermediateList(HttpDoc, "<div class='float-l'><h4>",
				"</h4></div><div class='clear'>", null, null, 0, true);

		// 得到每个车型的详细列表
		List<String> detailedModelList = StringHelpTool.getIntermediateList(HttpDoc, "<div class='float-l'><h4>",
				"<div class='clear b_line'>", null, null, 0, true);

		// 存放总车型 ->子车型->对应的URL
		Map<String, Map<String, String>> vehicleInfo = new LinkedHashMap<String, Map<String, String>>();
		for (int i = 0; i < detailedModelList.size(); i++) {

			Map<String, String> modelMap = new LinkedHashMap<String, String>();
			// 获取子车型
			List<String> model = StringHelpTool.getIntermediateList(detailedModelList.get(i),
					"<div class='float-l auto_sort'>", "</", null, null, 0, true);
			// 获取子车型对应的URL
			List<String> urlList = StringHelpTool.getIntermediateList(detailedModelList.get(i), "<a href='", "'>",
					"http://www.boyaweb.com/", null, 0, true);
			// 将子车型和其对应的URL放到一个Map里
			for (int j = 0; j < model.size(); j++) {
				String key = model.get(j);
				String value = urlList.get(j);
				modelMap.put(key, value);
			}
			// 将整理好的车型和对应URL信息 整理到总车型信息里
			vehicleInfo.put(modelList.get(i), modelMap);
		}

		// 至此第一层数据爬取完成
		LinkedHashMap<String, List<LinkedHashMap<String, String>>> daMap = new LinkedHashMap<String, List<LinkedHashMap<String, String>>>();

		for (String Key : vehicleInfo.keySet()) {
			Map<String, String> tmep = vehicleInfo.get(Key);
			List<LinkedHashMap<String, String>> tempList = null;
			System.out.println("当前正在解析车型:" + Key + "----数量:" + tmep.size());
			int item = 0;
			// 生成对应车型UUID
			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
			BaseDao baseDao = new BaseDao();
			for (String key : tmep.keySet()) {
				String URL = StringHelpTool.urlEncodeChinese(tmep.get(key).toString());
				tempList = demo(URL.replace("\t", ""));
				// 遍历解析出的数据
				// 生成详细信息表的uid
				String uuidString = UUID.randomUUID().toString().replaceAll("-", "");
				for (int x = 0; x < tempList.size(); x++) {
					LinkedHashMap<String, String> temp = tempList.get(x);
					// 写详细车型数据
					String sql = "INSERT INTO `cardata`.`vehicledata`(`id`, " + "`uid`," + " `matchingMode`, "
							+ "`YearMode`, " + "`chipModel`," + " `chipCopy`," + " `chipGeneration`, "
							+ "`antiTheftType`, `matchPassword`," + " `pwdAcquisition`, " + "`isOpen`"
							+ ", `partLocation`," + " `unlockingTool`," + " `unlockingDirection`,"
							+ " `keyBlankNumber`, " + "`lockingPlateDifference`," + " `matchingEquipment`,"
							+ " `OBDPosition`," + " `keyMatching`, " + "`remoteControlType`," + " `remoteGeneration`,"
							+ " `remoteCopy`," + " `remoteControlMatch`, "
							+ "`mattersNeedingAttention`) VALUES (null,?,?,?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?, ?,?, ?, ?, ?,?, ?,?);";
					baseDao.update(sql, uuidString, temp.get("匹配车型"), temp.get("车型年份"), temp.get("芯片型号"),
							temp.get("芯片拷贝"), temp.get("芯片生成"), temp.get("防盗类型"), temp.get("匹配密码"), temp.get("密码获取"),
							temp.get("是否拆读"), temp.get("零件位置"), temp.get("开锁工具"), temp.get("开锁方向"), temp.get("钥匙坯号"),
							temp.get("锁片差位"), temp.get("匹配设备"), temp.get("OBD位置"), temp.get("钥匙匹配"), temp.get("遥控类型"),
							temp.get("遥控生成"), temp.get("遥控拷贝"), temp.get("遥控匹配"), temp.get("注意事项"));
				}
				// 写到detailedvehicle表
				String sql = "INSERT INTO `cardata`.`detailedvehicle`(`id`, `uid`, `vehicleName`, `vehicleDataUid`) VALUES (NULL,?,?,?);";
				baseDao.update(sql, uuid, key, uuidString);
				item++;
				if (item == tmep.size() - 1) {
					// 写到model表
					sql = "INSERT INTO `cardata`.`model`(`id`, `modelName`, `dataIVehicledUid`) VALUES (null,?,?);";
					baseDao.update(sql, Key, uuid);
					item = 0;
				}

			}
		}
	}

	public static List<LinkedHashMap<String, String>> demo(String Url) {
		String URL = new String(Url);
		String HttpDoc = HttpSendRequest.sendRequest(URL, "GET");
		if (HttpDoc == null || HttpDoc.equals("")) {
			try {
				Robot r = new Robot();
				System.out.println(URL);
				System.out.println(HttpDoc);
				r.delay(5000);
				demo(URL.replace("\t", ""));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		List<LinkedHashMap<String, String>> dataMapList = new ArrayList<LinkedHashMap<String, String>>();

		// 匹配车型
		String matchingModel = StringHelpTool.getIntermediateString(HttpDoc, "<th>匹配车型</th>", "</tr>", null, null, 0,
				true);
		// 匹配车型列表
		List<String> matchingModelList = StringHelpTool.getIntermediateList(matchingModel, "<td>", "</td>", null, null,
				0, true);
		/*
		 * System.out.println("-------------------------匹配车型-------------------------");
		 * System.out.println(matchingModelList);
		 */

		// 车型年份
		String YearModel = StringHelpTool.getIntermediateString(HttpDoc, "<th>车型年份</th>", "</tr>", null, null, 0, true);
		// 车型年份列表
		List<String> YearModelList = StringHelpTool.getIntermediateList(YearModel, "<td>", "</td>", null, null, 0,
				true);
		/*
		 * System.out.println("-------------------------车型年份-------------------------");
		 * System.out.println(YearModelList);
		 */

		// 芯片型号
		String chipModel = StringHelpTool.getIntermediateString(HttpDoc, "<th>芯片型号</th>", "</tr>", null, null, 0, true);
		// 芯片型号列表
		List<String> chipModelList = StringHelpTool.getIntermediateList(chipModel, "<td>", "</td>", null, null, 0,
				true);
		/*
		 * System.out.println("-------------------------芯片型号-------------------------");
		 * System.out.println(chipModelList);
		 */

		// 芯片拷贝
		String chipCopy = StringHelpTool.getIntermediateString(HttpDoc, "<th>芯片拷贝</th>", "</tr>", null, null, 0, true);
		// 芯片拷贝列表
		List<String> chipCopyList = StringHelpTool.getIntermediateList(chipCopy, "<td>", "</td>", null, null, 0, true);
		/*
		 * System.out.println("-------------------------芯片拷贝-------------------------");
		 * System.out.println(chipCopyList);
		 */
		// 芯片生成
		String chipGeneration = StringHelpTool.getIntermediateString(HttpDoc, "<th>芯片生成</th>", "</tr>", null, null, 0,
				true);
		// 芯片生成列表
		List<String> chipGenerationList = StringHelpTool.getIntermediateList(chipGeneration, "<td>", "</td>", null,
				null, 0, true);
		/*
		 * System.out.println("-------------------------芯片生成-------------------------");
		 * System.out.println(chipGenerationList);
		 */

		// 防盗类型
		String antiTheftType = StringHelpTool.getIntermediateString(HttpDoc, "<th>防盗类型</th>", "</tr>", null, null, 0,
				true);
		// 防盗类型列表
		List<String> antiTheftTypeList = StringHelpTool.getIntermediateList(antiTheftType, "<td>", "</td>", null, null,
				0, true);
		/*
		 * System.out.println("-------------------------防盗类型-------------------------");
		 * System.out.println(antiTheftTypeList);
		 */

		// 匹配密码
		String MatchPassword = StringHelpTool.getIntermediateString(HttpDoc, "<th>匹配密码</th>", "</tr>", null, null, 0,
				true);
		// 匹配密码列表
		List<String> MatchPasswordList = StringHelpTool.getIntermediateList(MatchPassword, "<td>", "</td>", null, null,
				0, true);
		/*
		 * System.out.println("-------------------------匹配密码-------------------------");
		 * System.out.println(MatchPasswordList);
		 */

		// 密码获取
		String pwdAcquisition = StringHelpTool.getIntermediateString(HttpDoc, "<th>密码获取</th>", "</tr>", null, null, 0,
				true);
		// 密码获取列表
		List<String> pwdAcquisitionList = StringHelpTool.getIntermediateList(pwdAcquisition, "<td>", "</td>", null,
				null, 0, true);
		/*
		 * System.out.println("-------------------------密码获取-------------------------");
		 * System.out.println(pwdAcquisitionList);
		 */

		// 是否拆读
		String isOpen = StringHelpTool.getIntermediateString(HttpDoc, "<th>是否拆读</th>", "</tr>", null, null, 0, true);
		// 是否拆读列表
		List<String> isOpenList = StringHelpTool.getIntermediateList(isOpen, "<td>", "</td>", null, null, 0, true);
		/*
		 * System.out.println("-------------------------是否拆读-------------------------");
		 * System.out.println(isOpenList);
		 */

		// 零件位置
		String partLocation = StringHelpTool.getIntermediateString(HttpDoc, "<th>零件位置</th>", "</tr>", null, null, 0,
				true);
		// 零件位置列表
		List<String> partLocationList = StringHelpTool.getIntermediateList(partLocation, "<td>", "</td>", null, null, 0,
				true);
		/*
		 * System.out.println("-------------------------零件位置-------------------------");
		 * System.out.println(partLocationList);
		 */

		// 码片类型
		String chipType = StringHelpTool.getIntermediateString(HttpDoc, "<th>码片类型</th>", "</tr>", null, null, 0, true);
		// 码片类型列表
		List<String> chipTypeList = StringHelpTool.getIntermediateList(chipType, "<td>", "</td>", null, null, 0, true);
		/*
		 * System.out.println("-------------------------码片类型-------------------------");
		 * System.out.println(chipTypeList);
		 */

		// 开锁工具
		String unlockingTool = StringHelpTool.getIntermediateString(HttpDoc, "<th>开锁工具</th>", "</tr>", null, null, 0,
				true);
		// 开锁工具列表
		List<String> unlockingToolList = StringHelpTool.getIntermediateList(unlockingTool, "<td>", "</td>", null, null,
				0, true);
		/*
		 * System.out.println("-------------------------开锁工具-------------------------");
		 * System.out.println(unlockingToolList);
		 */

		// 开锁方向
		String unlockingDirection = StringHelpTool.getIntermediateString(HttpDoc, "<th>开锁方向</th>", "</tr>", null, null,
				0, true);
		// 开锁方向列表
		List<String> unlockingDirectionList = StringHelpTool.getIntermediateList(unlockingDirection, "<td>", "</td>",
				null, null, 0, true);
		/*
		 * System.out.println("-------------------------开锁方向-------------------------");
		 * System.out.println(unlockingDirectionList);
		 */

		// 钥匙坯号
		String keyBlankNumber = StringHelpTool.getIntermediateString(HttpDoc, "<th>钥匙坯号</th>", "</tr>", null, null, 0,
				true);
		// 钥匙坯号列表
		List<String> keyBlankNumberList = StringHelpTool.getIntermediateList(keyBlankNumber, "<td>", "</td>", null,
				null, 0, true);
		/*
		 * System.out.println("-------------------------钥匙坯号-------------------------");
		 * System.out.println(keyBlankNumberList);
		 */

		// 锁片差位
		String lockingPlateDifference = StringHelpTool.getIntermediateString(HttpDoc, "<th>锁片差位</th>", "</tr>", null,
				null, 0, true);
		// 锁片差位列表
		List<String> lockingPlateDifferenceList = StringHelpTool.getIntermediateList(lockingPlateDifference, "<td>",
				"</td>", null, null, 0, true);
		/*
		 * System.out.println("-------------------------锁片差位-------------------------");
		 * System.out.println(lockingPlateDifferenceList);
		 */

		// 匹配设备
		String matchingEquipment = StringHelpTool.getIntermediateString(HttpDoc, "<th>匹配设备</th>", "</tr>", null, null,
				0, true);
		// 匹配设备列表
		List<String> matchingEquipmentList = StringHelpTool.getIntermediateList(matchingEquipment, "<td>", "</td>",
				null, null, 0, true);
		/*
		 * System.out.println("-------------------------匹配设备-------------------------");
		 * System.out.println(matchingEquipmentList);
		 */

		// OBD位置
		String OBDPosition = StringHelpTool.getIntermediateString(HttpDoc, "<th>OBD位置</th>", "</tr>", null, null, 0,
				true);
		// OBD位置列表
		List<String> OBDPositionList = StringHelpTool.getIntermediateList(OBDPosition, "<td>", "</td>", null, null, 0,
				true);
		/*
		 * System.out.println("-------------------------OBD位置-------------------------")
		 * ; System.out.println(OBDPositionList);
		 */

		// 钥匙匹配
		String keyMatching = StringHelpTool.getIntermediateString(HttpDoc, "<th>钥匙匹配</th>", "</tr>", null, null, 0,
				true);
		// 钥匙匹配列表
		List<String> keyMatchingList = StringHelpTool.getIntermediateList(keyMatching, "<td>", "</td>", null, null, 0,
				true);
		/*
		 * System.out.println("-------------------------钥匙匹配-------------------------");
		 * System.out.println(keyMatchingList);
		 */

		// 遥控类型
		String remoteControlType = StringHelpTool.getIntermediateString(HttpDoc, "<th>遥控类型</th>", "</tr>", null, null,
				0, true);
		// 遥控类型列表
		List<String> remoteControlTypeList = StringHelpTool.getIntermediateList(remoteControlType, "<td>", "</td>",
				null, null, 0, true);
		/*
		 * System.out.println("-------------------------遥控类型-------------------------");
		 * System.out.println(remoteControlTypeList);
		 */

		// 遥控生成
		String remoteGeneration = StringHelpTool.getIntermediateString(HttpDoc, "<th>遥控生成</th>", "</tr>", null, null, 0,
				true);
		// 遥控生成列表
		List<String> remoteGenerationList = StringHelpTool.getIntermediateList(remoteGeneration, "<td>", "</td>", null,
				null, 0, true);
		/*
		 * System.out.println("-------------------------遥控生成-------------------------");
		 * System.out.println(remoteGenerationList);
		 */

		// 遥控拷贝
		String remoteCopy = StringHelpTool.getIntermediateString(HttpDoc, "<th>遥控拷贝</th>", "</tr>", null, null, 0,
				true);
		// 遥控拷贝列表
		List<String> remoteCopyList = StringHelpTool.getIntermediateList(remoteCopy, "<td>", "</td>", null, null, 0,
				true);
		/*
		 * System.out.println("-------------------------遥控拷贝-------------------------");
		 * System.out.println(remoteCopyList);
		 */
		// 遥控匹配
		String remoteControlMatch = StringHelpTool.getIntermediateString(HttpDoc, "<th>遥控匹配</th>", "</tr>", null, null,
				0, true);
		// 遥控匹配列表
		List<String> remoteControlMatchList = StringHelpTool.getIntermediateList(remoteControlMatch, "<td>", "</td>",
				null, null, 0, true);
		/*
		 * System.out.println("-------------------------遥控匹配-------------------------");
		 * System.out.println(remoteControlMatchList);
		 */

		// 注意事项
		String mattersNeedingAttention = StringHelpTool.getIntermediateString(HttpDoc, "<th>注意事项</th>", "</tr>", null,
				null, 0, true);
		// 注意事项列表
		List<String> mattersNeedingAttentionList = StringHelpTool.getIntermediateList(mattersNeedingAttention, "<td>",
				"</td>", null, null, 0, true);
		/*
		 * System.out.println("-------------------------注意事项-------------------------");
		 * System.out.println(mattersNeedingAttentionList);
		 */

		for (int i = 0; i < matchingModelList.size(); i++) {
			LinkedHashMap<String, String> dataMap = new LinkedHashMap<String, String>();
			dataMap.put("匹配车型", matchingModelList.get(i));
			dataMap.put("车型年份", YearModelList.get(i));
			dataMap.put("芯片型号", chipModelList.get(i));
			dataMap.put("芯片拷贝", chipCopyList.get(i));
			dataMap.put("芯片生成", chipGenerationList.get(i));
			dataMap.put("防盗类型", antiTheftTypeList.get(i));
			dataMap.put("匹配密码", MatchPasswordList.get(i));
			dataMap.put("密码获取", pwdAcquisitionList.get(i));
			dataMap.put("是否拆读", isOpenList.get(i));
			dataMap.put("零件位置", partLocationList.get(i));
			dataMap.put("开锁工具", unlockingToolList.get(i));
			dataMap.put("开锁方向", unlockingDirectionList.get(i));
			dataMap.put("钥匙坯号", keyBlankNumberList.get(i));
			dataMap.put("锁片差位", lockingPlateDifferenceList.get(i));
			dataMap.put("匹配设备", matchingEquipmentList.get(i));
			dataMap.put("OBD位置", OBDPositionList.get(i));
			dataMap.put("钥匙匹配", keyMatchingList.get(i));
			dataMap.put("遥控类型", remoteControlTypeList.get(i));
			dataMap.put("遥控生成", remoteGenerationList.get(i));
			dataMap.put("遥控拷贝", remoteCopyList.get(i));
			dataMap.put("遥控匹配", remoteControlMatchList.get(i));
			dataMap.put("注意事项", mattersNeedingAttentionList.get(i));
			dataMapList.add(i, dataMap);
		}
		// System.out.println(dataMapList.size());
		return dataMapList;
	}
}
