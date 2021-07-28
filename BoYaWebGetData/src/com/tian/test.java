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

		HttpDoc = StringHelpTool.getIntermediateList(HttpDoc, "<div class=\"pc_body_content\">", "<!-- ���Եײ���Ϣ���� -->",
				null, null, 0, true).get(0);

		// �õ����峵���б�
		List<String> modelList = StringHelpTool.getIntermediateList(HttpDoc, "<div class='float-l'><h4>",
				"</h4></div><div class='clear'>", null, null, 0, true);

		// �õ�ÿ�����͵���ϸ�б�
		List<String> detailedModelList = StringHelpTool.getIntermediateList(HttpDoc, "<div class='float-l'><h4>",
				"<div class='clear b_line'>", null, null, 0, true);

		// ����ܳ��� ->�ӳ���->��Ӧ��URL
		Map<String, Map<String, String>> vehicleInfo = new LinkedHashMap<String, Map<String, String>>();
		for (int i = 0; i < detailedModelList.size(); i++) {

			Map<String, String> modelMap = new LinkedHashMap<String, String>();
			// ��ȡ�ӳ���
			List<String> model = StringHelpTool.getIntermediateList(detailedModelList.get(i),
					"<div class='float-l auto_sort'>", "</", null, null, 0, true);
			// ��ȡ�ӳ��Ͷ�Ӧ��URL
			List<String> urlList = StringHelpTool.getIntermediateList(detailedModelList.get(i), "<a href='", "'>",
					"http://www.boyaweb.com/", null, 0, true);
			// ���ӳ��ͺ����Ӧ��URL�ŵ�һ��Map��
			for (int j = 0; j < model.size(); j++) {
				String key = model.get(j);
				String value = urlList.get(j);
				modelMap.put(key, value);
			}
			// ������õĳ��ͺͶ�ӦURL��Ϣ �����ܳ�����Ϣ��
			vehicleInfo.put(modelList.get(i), modelMap);
		}

		// ���˵�һ��������ȡ���
		LinkedHashMap<String, List<LinkedHashMap<String, String>>> daMap = new LinkedHashMap<String, List<LinkedHashMap<String, String>>>();

		for (String Key : vehicleInfo.keySet()) {
			Map<String, String> tmep = vehicleInfo.get(Key);
			List<LinkedHashMap<String, String>> tempList = null;
			System.out.println("��ǰ���ڽ�������:" + Key + "----����:" + tmep.size());
			int item = 0;
			// ���ɶ�Ӧ����UUID
			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
			BaseDao baseDao = new BaseDao();
			for (String key : tmep.keySet()) {
				String URL = StringHelpTool.urlEncodeChinese(tmep.get(key).toString());
				tempList = demo(URL.replace("\t", ""));
				// ����������������
				// ������ϸ��Ϣ���uid
				String uuidString = UUID.randomUUID().toString().replaceAll("-", "");
				for (int x = 0; x < tempList.size(); x++) {
					LinkedHashMap<String, String> temp = tempList.get(x);
					// д��ϸ��������
					String sql = "INSERT INTO `cardata`.`vehicledata`(`id`, " + "`uid`," + " `matchingMode`, "
							+ "`YearMode`, " + "`chipModel`," + " `chipCopy`," + " `chipGeneration`, "
							+ "`antiTheftType`, `matchPassword`," + " `pwdAcquisition`, " + "`isOpen`"
							+ ", `partLocation`," + " `unlockingTool`," + " `unlockingDirection`,"
							+ " `keyBlankNumber`, " + "`lockingPlateDifference`," + " `matchingEquipment`,"
							+ " `OBDPosition`," + " `keyMatching`, " + "`remoteControlType`," + " `remoteGeneration`,"
							+ " `remoteCopy`," + " `remoteControlMatch`, "
							+ "`mattersNeedingAttention`) VALUES (null,?,?,?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?, ?,?, ?, ?, ?,?, ?,?);";
					baseDao.update(sql, uuidString, temp.get("ƥ�䳵��"), temp.get("�������"), temp.get("оƬ�ͺ�"),
							temp.get("оƬ����"), temp.get("оƬ����"), temp.get("��������"), temp.get("ƥ������"), temp.get("�����ȡ"),
							temp.get("�Ƿ���"), temp.get("���λ��"), temp.get("��������"), temp.get("��������"), temp.get("Կ������"),
							temp.get("��Ƭ��λ"), temp.get("ƥ���豸"), temp.get("OBDλ��"), temp.get("Կ��ƥ��"), temp.get("ң������"),
							temp.get("ң������"), temp.get("ң�ؿ���"), temp.get("ң��ƥ��"), temp.get("ע������"));
				}
				// д��detailedvehicle��
				String sql = "INSERT INTO `cardata`.`detailedvehicle`(`id`, `uid`, `vehicleName`, `vehicleDataUid`) VALUES (NULL,?,?,?);";
				baseDao.update(sql, uuid, key, uuidString);
				item++;
				if (item == tmep.size() - 1) {
					// д��model��
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

		// ƥ�䳵��
		String matchingModel = StringHelpTool.getIntermediateString(HttpDoc, "<th>ƥ�䳵��</th>", "</tr>", null, null, 0,
				true);
		// ƥ�䳵���б�
		List<String> matchingModelList = StringHelpTool.getIntermediateList(matchingModel, "<td>", "</td>", null, null,
				0, true);
		/*
		 * System.out.println("-------------------------ƥ�䳵��-------------------------");
		 * System.out.println(matchingModelList);
		 */

		// �������
		String YearModel = StringHelpTool.getIntermediateString(HttpDoc, "<th>�������</th>", "</tr>", null, null, 0, true);
		// ��������б�
		List<String> YearModelList = StringHelpTool.getIntermediateList(YearModel, "<td>", "</td>", null, null, 0,
				true);
		/*
		 * System.out.println("-------------------------�������-------------------------");
		 * System.out.println(YearModelList);
		 */

		// оƬ�ͺ�
		String chipModel = StringHelpTool.getIntermediateString(HttpDoc, "<th>оƬ�ͺ�</th>", "</tr>", null, null, 0, true);
		// оƬ�ͺ��б�
		List<String> chipModelList = StringHelpTool.getIntermediateList(chipModel, "<td>", "</td>", null, null, 0,
				true);
		/*
		 * System.out.println("-------------------------оƬ�ͺ�-------------------------");
		 * System.out.println(chipModelList);
		 */

		// оƬ����
		String chipCopy = StringHelpTool.getIntermediateString(HttpDoc, "<th>оƬ����</th>", "</tr>", null, null, 0, true);
		// оƬ�����б�
		List<String> chipCopyList = StringHelpTool.getIntermediateList(chipCopy, "<td>", "</td>", null, null, 0, true);
		/*
		 * System.out.println("-------------------------оƬ����-------------------------");
		 * System.out.println(chipCopyList);
		 */
		// оƬ����
		String chipGeneration = StringHelpTool.getIntermediateString(HttpDoc, "<th>оƬ����</th>", "</tr>", null, null, 0,
				true);
		// оƬ�����б�
		List<String> chipGenerationList = StringHelpTool.getIntermediateList(chipGeneration, "<td>", "</td>", null,
				null, 0, true);
		/*
		 * System.out.println("-------------------------оƬ����-------------------------");
		 * System.out.println(chipGenerationList);
		 */

		// ��������
		String antiTheftType = StringHelpTool.getIntermediateString(HttpDoc, "<th>��������</th>", "</tr>", null, null, 0,
				true);
		// ���������б�
		List<String> antiTheftTypeList = StringHelpTool.getIntermediateList(antiTheftType, "<td>", "</td>", null, null,
				0, true);
		/*
		 * System.out.println("-------------------------��������-------------------------");
		 * System.out.println(antiTheftTypeList);
		 */

		// ƥ������
		String MatchPassword = StringHelpTool.getIntermediateString(HttpDoc, "<th>ƥ������</th>", "</tr>", null, null, 0,
				true);
		// ƥ�������б�
		List<String> MatchPasswordList = StringHelpTool.getIntermediateList(MatchPassword, "<td>", "</td>", null, null,
				0, true);
		/*
		 * System.out.println("-------------------------ƥ������-------------------------");
		 * System.out.println(MatchPasswordList);
		 */

		// �����ȡ
		String pwdAcquisition = StringHelpTool.getIntermediateString(HttpDoc, "<th>�����ȡ</th>", "</tr>", null, null, 0,
				true);
		// �����ȡ�б�
		List<String> pwdAcquisitionList = StringHelpTool.getIntermediateList(pwdAcquisition, "<td>", "</td>", null,
				null, 0, true);
		/*
		 * System.out.println("-------------------------�����ȡ-------------------------");
		 * System.out.println(pwdAcquisitionList);
		 */

		// �Ƿ���
		String isOpen = StringHelpTool.getIntermediateString(HttpDoc, "<th>�Ƿ���</th>", "</tr>", null, null, 0, true);
		// �Ƿ����б�
		List<String> isOpenList = StringHelpTool.getIntermediateList(isOpen, "<td>", "</td>", null, null, 0, true);
		/*
		 * System.out.println("-------------------------�Ƿ���-------------------------");
		 * System.out.println(isOpenList);
		 */

		// ���λ��
		String partLocation = StringHelpTool.getIntermediateString(HttpDoc, "<th>���λ��</th>", "</tr>", null, null, 0,
				true);
		// ���λ���б�
		List<String> partLocationList = StringHelpTool.getIntermediateList(partLocation, "<td>", "</td>", null, null, 0,
				true);
		/*
		 * System.out.println("-------------------------���λ��-------------------------");
		 * System.out.println(partLocationList);
		 */

		// ��Ƭ����
		String chipType = StringHelpTool.getIntermediateString(HttpDoc, "<th>��Ƭ����</th>", "</tr>", null, null, 0, true);
		// ��Ƭ�����б�
		List<String> chipTypeList = StringHelpTool.getIntermediateList(chipType, "<td>", "</td>", null, null, 0, true);
		/*
		 * System.out.println("-------------------------��Ƭ����-------------------------");
		 * System.out.println(chipTypeList);
		 */

		// ��������
		String unlockingTool = StringHelpTool.getIntermediateString(HttpDoc, "<th>��������</th>", "</tr>", null, null, 0,
				true);
		// ���������б�
		List<String> unlockingToolList = StringHelpTool.getIntermediateList(unlockingTool, "<td>", "</td>", null, null,
				0, true);
		/*
		 * System.out.println("-------------------------��������-------------------------");
		 * System.out.println(unlockingToolList);
		 */

		// ��������
		String unlockingDirection = StringHelpTool.getIntermediateString(HttpDoc, "<th>��������</th>", "</tr>", null, null,
				0, true);
		// ���������б�
		List<String> unlockingDirectionList = StringHelpTool.getIntermediateList(unlockingDirection, "<td>", "</td>",
				null, null, 0, true);
		/*
		 * System.out.println("-------------------------��������-------------------------");
		 * System.out.println(unlockingDirectionList);
		 */

		// Կ������
		String keyBlankNumber = StringHelpTool.getIntermediateString(HttpDoc, "<th>Կ������</th>", "</tr>", null, null, 0,
				true);
		// Կ�������б�
		List<String> keyBlankNumberList = StringHelpTool.getIntermediateList(keyBlankNumber, "<td>", "</td>", null,
				null, 0, true);
		/*
		 * System.out.println("-------------------------Կ������-------------------------");
		 * System.out.println(keyBlankNumberList);
		 */

		// ��Ƭ��λ
		String lockingPlateDifference = StringHelpTool.getIntermediateString(HttpDoc, "<th>��Ƭ��λ</th>", "</tr>", null,
				null, 0, true);
		// ��Ƭ��λ�б�
		List<String> lockingPlateDifferenceList = StringHelpTool.getIntermediateList(lockingPlateDifference, "<td>",
				"</td>", null, null, 0, true);
		/*
		 * System.out.println("-------------------------��Ƭ��λ-------------------------");
		 * System.out.println(lockingPlateDifferenceList);
		 */

		// ƥ���豸
		String matchingEquipment = StringHelpTool.getIntermediateString(HttpDoc, "<th>ƥ���豸</th>", "</tr>", null, null,
				0, true);
		// ƥ���豸�б�
		List<String> matchingEquipmentList = StringHelpTool.getIntermediateList(matchingEquipment, "<td>", "</td>",
				null, null, 0, true);
		/*
		 * System.out.println("-------------------------ƥ���豸-------------------------");
		 * System.out.println(matchingEquipmentList);
		 */

		// OBDλ��
		String OBDPosition = StringHelpTool.getIntermediateString(HttpDoc, "<th>OBDλ��</th>", "</tr>", null, null, 0,
				true);
		// OBDλ���б�
		List<String> OBDPositionList = StringHelpTool.getIntermediateList(OBDPosition, "<td>", "</td>", null, null, 0,
				true);
		/*
		 * System.out.println("-------------------------OBDλ��-------------------------")
		 * ; System.out.println(OBDPositionList);
		 */

		// Կ��ƥ��
		String keyMatching = StringHelpTool.getIntermediateString(HttpDoc, "<th>Կ��ƥ��</th>", "</tr>", null, null, 0,
				true);
		// Կ��ƥ���б�
		List<String> keyMatchingList = StringHelpTool.getIntermediateList(keyMatching, "<td>", "</td>", null, null, 0,
				true);
		/*
		 * System.out.println("-------------------------Կ��ƥ��-------------------------");
		 * System.out.println(keyMatchingList);
		 */

		// ң������
		String remoteControlType = StringHelpTool.getIntermediateString(HttpDoc, "<th>ң������</th>", "</tr>", null, null,
				0, true);
		// ң�������б�
		List<String> remoteControlTypeList = StringHelpTool.getIntermediateList(remoteControlType, "<td>", "</td>",
				null, null, 0, true);
		/*
		 * System.out.println("-------------------------ң������-------------------------");
		 * System.out.println(remoteControlTypeList);
		 */

		// ң������
		String remoteGeneration = StringHelpTool.getIntermediateString(HttpDoc, "<th>ң������</th>", "</tr>", null, null, 0,
				true);
		// ң�������б�
		List<String> remoteGenerationList = StringHelpTool.getIntermediateList(remoteGeneration, "<td>", "</td>", null,
				null, 0, true);
		/*
		 * System.out.println("-------------------------ң������-------------------------");
		 * System.out.println(remoteGenerationList);
		 */

		// ң�ؿ���
		String remoteCopy = StringHelpTool.getIntermediateString(HttpDoc, "<th>ң�ؿ���</th>", "</tr>", null, null, 0,
				true);
		// ң�ؿ����б�
		List<String> remoteCopyList = StringHelpTool.getIntermediateList(remoteCopy, "<td>", "</td>", null, null, 0,
				true);
		/*
		 * System.out.println("-------------------------ң�ؿ���-------------------------");
		 * System.out.println(remoteCopyList);
		 */
		// ң��ƥ��
		String remoteControlMatch = StringHelpTool.getIntermediateString(HttpDoc, "<th>ң��ƥ��</th>", "</tr>", null, null,
				0, true);
		// ң��ƥ���б�
		List<String> remoteControlMatchList = StringHelpTool.getIntermediateList(remoteControlMatch, "<td>", "</td>",
				null, null, 0, true);
		/*
		 * System.out.println("-------------------------ң��ƥ��-------------------------");
		 * System.out.println(remoteControlMatchList);
		 */

		// ע������
		String mattersNeedingAttention = StringHelpTool.getIntermediateString(HttpDoc, "<th>ע������</th>", "</tr>", null,
				null, 0, true);
		// ע�������б�
		List<String> mattersNeedingAttentionList = StringHelpTool.getIntermediateList(mattersNeedingAttention, "<td>",
				"</td>", null, null, 0, true);
		/*
		 * System.out.println("-------------------------ע������-------------------------");
		 * System.out.println(mattersNeedingAttentionList);
		 */

		for (int i = 0; i < matchingModelList.size(); i++) {
			LinkedHashMap<String, String> dataMap = new LinkedHashMap<String, String>();
			dataMap.put("ƥ�䳵��", matchingModelList.get(i));
			dataMap.put("�������", YearModelList.get(i));
			dataMap.put("оƬ�ͺ�", chipModelList.get(i));
			dataMap.put("оƬ����", chipCopyList.get(i));
			dataMap.put("оƬ����", chipGenerationList.get(i));
			dataMap.put("��������", antiTheftTypeList.get(i));
			dataMap.put("ƥ������", MatchPasswordList.get(i));
			dataMap.put("�����ȡ", pwdAcquisitionList.get(i));
			dataMap.put("�Ƿ���", isOpenList.get(i));
			dataMap.put("���λ��", partLocationList.get(i));
			dataMap.put("��������", unlockingToolList.get(i));
			dataMap.put("��������", unlockingDirectionList.get(i));
			dataMap.put("Կ������", keyBlankNumberList.get(i));
			dataMap.put("��Ƭ��λ", lockingPlateDifferenceList.get(i));
			dataMap.put("ƥ���豸", matchingEquipmentList.get(i));
			dataMap.put("OBDλ��", OBDPositionList.get(i));
			dataMap.put("Կ��ƥ��", keyMatchingList.get(i));
			dataMap.put("ң������", remoteControlTypeList.get(i));
			dataMap.put("ң������", remoteGenerationList.get(i));
			dataMap.put("ң�ؿ���", remoteCopyList.get(i));
			dataMap.put("ң��ƥ��", remoteControlMatchList.get(i));
			dataMap.put("ע������", mattersNeedingAttentionList.get(i));
			dataMapList.add(i, dataMap);
		}
		// System.out.println(dataMapList.size());
		return dataMapList;
	}
}
