package com.cn.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cn.config.Logger;
import com.cn.config.LoggerManager;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {
	private static final Logger logger = LoggerManager.getLogger(ExcelUtil.class);

	private static XSSFWorkbook workbook = new XSSFWorkbook();

	private static XSSFCellStyle headerCellStyle = workbook.createCellStyle();
	private static XSSFCellStyle headerCellStyleTitle = workbook.createCellStyle();
	private static XSSFCellStyle headerCellStyleColumn = workbook.createCellStyle();
	private static XSSFCellStyle headerCellStyle1 = workbook.createCellStyle();

	static {
		// excel样式
		XSSFFont headerFont = workbook.createFont();
		headerFont.setFontName("宋体");
		headerFont.setFontHeightInPoints((short) 12);// 设置字体大小
		// headerFont.setBold(true);
		headerCellStyle.setAlignment(HorizontalAlignment.RIGHT);
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setBorderBottom(BorderStyle.THIN); // 下边框
		headerCellStyle.setBorderLeft(BorderStyle.THIN);// 左边框
		headerCellStyle.setBorderTop(BorderStyle.THIN);// 上边框
		headerCellStyle.setBorderRight(BorderStyle.THIN);// 右边框

		XSSFFont headerFontTitle = workbook.createFont();
		headerFontTitle.setFontName("宋体");
		headerFontTitle.setFontHeightInPoints((short) 12);// 设置字体大小
		// headerFontTitle.setBold(true);
		headerCellStyleTitle.setAlignment(HorizontalAlignment.LEFT);
		headerCellStyleTitle.setFont(headerFontTitle);
		headerCellStyleTitle.setBorderBottom(BorderStyle.THIN); // 下边框
		headerCellStyleTitle.setBorderLeft(BorderStyle.THIN);// 左边框
		headerCellStyleTitle.setBorderTop(BorderStyle.THIN);// 上边框
		headerCellStyleTitle.setBorderRight(BorderStyle.THIN);// 右边框

		XSSFFont headerFontColumn = workbook.createFont();
		headerFontColumn.setFontName("宋体");
		headerFontColumn.setFontHeightInPoints((short) 12);// 设置字体大小
		// headerFontColumn.setBold(true);
		headerCellStyleColumn.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyleColumn.setFont(headerFontColumn);
		headerCellStyleColumn.setBorderBottom(BorderStyle.THIN); // 下边框
		headerCellStyleColumn.setBorderLeft(BorderStyle.THIN);// 左边框
		headerCellStyleColumn.setBorderTop(BorderStyle.THIN);// 上边框
		headerCellStyleColumn.setBorderRight(BorderStyle.THIN);// 右边框

		XSSFFont headerFont1 = workbook.createFont();
		headerFont1.setFontName("宋体");
		headerFont1.setFontHeightInPoints((short) 14);// 设置字体大小
		headerFont1.setBold(true);
		headerCellStyle1.setFont(headerFont1);
	}

	/**
	 * -Excel导出
	 * 
	 * @param sheetName sheet页名称
	 * @param title     标题
	 * @param tableHead 表头 [zhName 中文表头 enName 英文表头]
	 * @param dataList  数据
	 * @return XSSFWorkbook
	 */
	public static XSSFWorkbook exportExcel(String sheetName, String title, List<Map<String, String>> tableHead,
			List<Map<String, Object>> dataList) {
		logger.info("-->生成Excel开始<--" + " \n" + "参数==>" + "\n" + "sheetName=>" + sheetName + "\n" + "title=>" + title
				+ "\n" + "tableHead=>" + tableHead + "\n" + "dataList=>" + dataList);
		XSSFSheet sheet = workbook.createSheet(sheetName);
		sheet.setDefaultColumnWidth(40);
		int column_num = tableHead != null ? tableHead.size() : 0;
		if (column_num > 255) {
			column_num = 255;
		} else if (column_num < 2) {
			column_num = 2;
		}
		// 合并单元格-文件名
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, column_num - 1));

		// 文件行
		XSSFRow row_0 = sheet.createRow(0);
		@SuppressWarnings("deprecation")
		XSSFCell cell_fileName = row_0.createCell(0, XSSFCell.CELL_TYPE_STRING);
		cell_fileName.setCellValue(title);
		cell_fileName.setCellStyle(headerCellStyleTitle);

		XSSFRow headerrow = sheet.createRow(1);
		// 写入表头
		for (int ti = 0; ti < tableHead.size(); ti++) {
			XSSFCell headerCell = headerrow.createCell(ti);
			Map<String, String> tableHeadMap = tableHead.get(ti);
			String headValue = tableHeadMap.get("zhName") == null ? "" : tableHeadMap.get("zhName").toString();
			if (headValue == null || "".equals(headValue)) {
				headValue = tableHeadMap.get("enName") == null ? "" : tableHeadMap.get("enName").toString();
			}
			headerCell.setCellValue(headValue);
			headerCell.setCellStyle(headerCellStyleTitle);
			sheet.autoSizeColumn(30);// 设置宽度
			headerCell.setCellStyle(headerCellStyleColumn);
		}

		// 该 for 循环写入数据
		for (int li = 0; li < dataList.size(); li++) {
			XSSFRow datarow = sheet.createRow(li + 2);
			Map<String, Object> dataMap = dataList.get(li);
			for (int tj = 0; tj < tableHead.size(); tj++) {
				Map<String, String> tableHeadMap = tableHead.get(tj);
				String enName = tableHeadMap.get("enName") == null ? "" : tableHeadMap.get("enName").toString();
				String value = dataMap.get(enName) == null ? "" : dataMap.get(enName).toString();
				XSSFCell dataCell = datarow.createCell(tj);
				dataCell.setCellValue(value);
				dataCell.setCellStyle(headerCellStyleTitle);
			}
		}

		return workbook;
	}

	/**
	 * -Excel文件读取
	 * 
	 * @param file     读取Excel文件路径
	 * @param sheetNum 读取sheet页
	 * @return List<Map<String,Object>> 数据
	 */
	public static List<Map<String, Object>> readExcel(File file, int sheetNum) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		logger.info("-->读取Excel开始<--");
		logger.info("读取文件：" + file);
		try {
			if (file.length() == 0) {
				logger.info("所读文档为空");
			}
			FileInputStream fis = new FileInputStream(file);
			workbook = new XSSFWorkbook(fis);

			// 开始解析
			XSSFSheet sheet = workbook.getSheetAt(sheetNum); // 读取sheet

			int firstRowIndex = sheet.getFirstRowNum() + 1; // 第一行是标题，所以不读
			int lastRowIndex = sheet.getLastRowNum();
			logger.info("此sheet页行数如下: " + "\n" + "firstRowIndex: " + firstRowIndex + "\n" + "lastRowIndex: "
					+ lastRowIndex);

			List<String> tableHead = new ArrayList<String>();
			XSSFRow firstRow = sheet.getRow(firstRowIndex);
			int firstRowCellIndex = firstRow.getFirstCellNum();
			int lastRowCellIndex = firstRow.getLastCellNum();
			for (int frindex = firstRowCellIndex; frindex < lastRowCellIndex; frindex++) {
				XSSFCell cell = firstRow.getCell(frindex);
				tableHead.add(cell == null ? "" : cell.toString());
			}

			for (int rIndex = firstRowIndex + 1; rIndex <= lastRowIndex; rIndex++) { // 遍历行
				XSSFRow row = sheet.getRow(rIndex);
				if (row != null) {
					int firstCellIndex = row.getFirstCellNum();
					int lastCellIndex = row.getLastCellNum();
					for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) { // 遍历列
						String key = tableHead.get(cIndex);
						XSSFCell cell = row.getCell(cIndex);
						Map<String, Object> dataMap = new HashMap<>();
						if (cell != null) {
							dataMap.put(key, cell == null ? "" : cell.toString());
							dataList.add(dataMap);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.info("捕获异常==>" + e.getMessage());
			e.printStackTrace();
		}
		// logger.info("dataList==>" + dataList);
		return dataList;
	}

}
