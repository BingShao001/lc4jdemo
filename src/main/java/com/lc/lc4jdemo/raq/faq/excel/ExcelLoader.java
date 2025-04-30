package com.lc.lc4jdemo.raq.faq.excel;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.data.document.Metadata;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelLoader {

    public static List<TextSegment> loadExcelAsSegments(File file) throws IOException {
        List<TextSegment> segments = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            for (Sheet sheet : workbook) {
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) {
                        continue;
                    }
                    // 读取每一行的数据（假设第一列是主板名称，第二列是功能分类，第三列是内容
                    Cell mainCell = row.getCell(0); // 主板名称
                    Cell functionCell = row.getCell(1); // 功能分类
                    Cell functionContent = row.getCell(2); // 内容

                    if (mainCell == null && functionCell == null && functionContent == null) {
                        continue;
                    }

                    String content = "功能名称\n：" + mainCell+"-"+functionCell + "\n内容：" + functionContent;
                    Metadata metadata = Metadata.from("source", file.getName())
                            .put("sheet", sheet.getSheetName())
                            .put("row", String.valueOf(row.getRowNum()));
                    segments.add(new TextSegment(content, metadata));
                }
            }
        }

        return segments;
    }
    public static void main(String[] args){
        try {
            String path = ExcelLoader.class.getResource("/FAQ_CN.xlsx").getPath();
            File file = new File(path);
            loadExcelAsSegments(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
