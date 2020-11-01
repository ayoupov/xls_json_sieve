package org.panaggelica.xls_json_sieve.controllers;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.panaggelica.xls_json_sieve.ODHLoader;
import org.panaggelica.xls_json_sieve.model.MatchResponse;
import org.panaggelica.xls_json_sieve.model.ODHModel;
import org.panaggelica.xls_json_sieve.model.XLSModel;
import org.panaggelica.xls_json_sieve.model.XLSObjectDescriptor;
import org.panaggelica.xls_json_sieve.processors.SieveProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/")
@Slf4j
public class MainController {

    int START_ROW = 8;
    int OBJ_NAME_CELL = 1;
    int FROM_CELL = 2;
    int TO_CELL = 3;
    int DISTRICT_CELL = 4;

    @Autowired
    private SieveProcessor sieve;

    @SneakyThrows
    @PostMapping(path = "/xls", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MatchResponse> match(@RequestParam("file") MultipartFile file) throws IOException {
        final Workbook sheets = WorkbookFactory.create(file.getInputStream());
        final Sheet sheet = sheets.getSheetAt(0);
        int i = START_ROW;
        List<XLSObjectDescriptor> descriptors = new ArrayList<>();
        XLSObjectDescriptor descriptor;
        XLSModel model = new XLSModel();
        while ((descriptor = getNext(sheet, i)) != null) {
//            log.info("descriptor: {}", descriptor);
            descriptors.add(descriptor);
            i++;
        }
        model.setObjects(descriptors);
        model.filter();
        model.list();
        final ODHModel oModel = ODHLoader.getModel();
        final int total = descriptors.size();
        List<MatchResponse> response = sieve.process(oModel, model);

        final int left = model.getObjects().size();
        log.info("{} ({}) entries left unmatched", left, left * 1f / total);
        return response;
    }

    private XLSObjectDescriptor getNext(Sheet sheet, int i) {
        final Row row = sheet.getRow(i);
        if (row == null)
            return null;

        final String objName = row.getCell(OBJ_NAME_CELL).getStringCellValue();
        final String from = row.getCell(FROM_CELL).getStringCellValue();
        final String to = row.getCell(TO_CELL).getStringCellValue();
        final String district = row.getCell(DISTRICT_CELL).getStringCellValue();

        final XLSObjectDescriptor descriptor =
                new XLSObjectDescriptor(
                        objName,
                        from,
                        to,
                        district);
        return descriptor;
    }
}
