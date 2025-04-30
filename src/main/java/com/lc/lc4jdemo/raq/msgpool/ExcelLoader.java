package com.lc.lc4jdemo.raq.msgpool;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
                    //第一行是数据的标题
                    if (row.getRowNum() == 0) {
                        continue;
                    }
                    Cell messageType = row.getCell(0); // 消息类型
                    Cell message = row.getCell(1); // message

                    if (messageType == null && message == null) {
                        continue;
                    }

                    String content = "消息类型\n：" + messageType + "\n消息内容：" + message;
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
