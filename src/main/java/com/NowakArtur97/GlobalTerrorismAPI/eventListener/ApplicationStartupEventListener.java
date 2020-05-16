package com.NowakArtur97.GlobalTerrorismAPI.eventListener;

import com.NowakArtur97.GlobalTerrorismAPI.enums.XlsxColumnType;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.GroupRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ApplicationStartupEventListener {

    private final static String PATH_TO_FILE = "data/globalterrorismdb_0919dist-mini.xlsx";

    private Map<String, GroupNode> groupsWithTargets = new HashMap<>();

    private final TargetService targetService;

    private final EventService eventService;

    private final GroupRepository groupRepository;

    @EventListener
    public void onApplicationStartup(ContextRefreshedEvent event) {

        if (targetService.isDatabaseEmpty()) {

            Sheet sheet = loadSheetFromFile();

            insertDataToDatabase(sheet);
        }
    }

    private Sheet loadSheetFromFile() {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(PATH_TO_FILE);

        Workbook workbook = StreamingReader.builder().rowCacheSize(10).bufferSize(4096).open(inputStream);

        return workbook.getSheetAt(0);
    }

    private void insertDataToDatabase(Sheet sheet) {

        for (Row row : sheet) {

            String targetName = getCellValueFromRowOnIndex(row, XlsxColumnType.TARGET.getIndex());

            TargetNode target = saveTarget(targetName);

            EventNode eventNode = createEvent(row, target);

            String groupName = getCellValueFromRowOnIndex(row, XlsxColumnType.GROUP.getIndex());

            manageGroup(groupName, eventNode);
        }
    }

    private void manageGroup(String groupName, EventNode eventNode) {

//        log.info("GROUP: " + groupName);
        groupRepository.save(new GroupNode(groupName));
    }

    private EventNode createEvent(Row row, TargetNode target) {

        String cellValue;

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.YEAR_OF_EVENT.getIndex());
        int yearOfEvent = isNumeric(cellValue) ? parseInt(cellValue) : 1900;

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.MONTH_OF_EVENT.getIndex());
        int monthOfEvent = isNumeric(cellValue) ? parseInt(cellValue) : 1;

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.DAY_OF_EVENT.getIndex());
        int dayOfEvent = isNumeric(cellValue) ? parseInt(cellValue) : 1;

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.EVENT_SUMMARY.getIndex());
        String eventSummary = !cellValue.isEmpty() ? cellValue : "";

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.MOTIVE.getIndex());
        String motive = !cellValue.isEmpty() ? cellValue : "";

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.WAS_EVENT_PART_OF_MULTIPLE_INCIDENTS.getIndex());
        boolean isPartOfMultipleIncidents = parseBoolean(cellValue);

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.WAS_EVENT_SUCCESS.getIndex());
        boolean isSuccessful = parseBoolean(cellValue);

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.WAS_EVENT_SUICIDE.getIndex());
        boolean isSuicide = parseBoolean(cellValue);

        EventNode eventNode = saveEvent(yearOfEvent, monthOfEvent, dayOfEvent, eventSummary, isPartOfMultipleIncidents, isSuccessful,
                isSuicide, motive, target);

        return eventNode;
    }

    private TargetNode saveTarget(String targetName) {

        TargetNode target = new TargetNode(targetName);

        return targetService.save(target);
    }

    private EventNode saveEvent(int yearOfEvent, int monthOfEvent, int dayOfEvent, String eventSummary,
                                boolean isPartOfMultipleIncidents, boolean isSuccessful, boolean isSuicide, String motive,
                                TargetNode target) {

        Date date = getEventDate(yearOfEvent, monthOfEvent, dayOfEvent);

        EventNode eventNode = EventNode.builder().date(date).summary(eventSummary)
                .isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
                .isSuicide(isSuicide).motive(motive).target(target).build();

        return eventService.save(eventNode);
    }

    private Date getEventDate(int yearOfEvent, int monthOfEvent, int dayOfEvent) {

        monthOfEvent = isMonthCorrect(monthOfEvent) ? monthOfEvent - 1 : 0;
        dayOfEvent = isDayCorrect(dayOfEvent) ? dayOfEvent : 1;

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, yearOfEvent);
        cal.set(Calendar.MONTH, monthOfEvent);
        cal.set(Calendar.DAY_OF_MONTH, dayOfEvent);

        return cal.getTime();
    }

    private String getCellValueFromRowOnIndex(Row row, int index) {

        Cell cell = row.getCell(index, MissingCellPolicy.CREATE_NULL_AS_BLANK);

        String value = "";

        switch (cell.getCellType()) {

            case NUMERIC:
                Double doubleValue = cell.getNumericCellValue();
                value = doubleValue.toString();
                break;

            case STRING:
                value = cell.getStringCellValue();
                break;

            case FORMULA:
                value = cell.getCellFormula();
                break;

            case BOOLEAN:
                boolean booleanValue = cell.getBooleanCellValue();
                value = "" + booleanValue;
                break;

            case ERROR:
                byte byteValue = cell.getErrorCellValue();
                value = "" + byteValue;
                break;

            case BLANK:
            case _NONE:
            default:
                break;
        }

        return value;
    }

    private boolean isMonthCorrect(int monthOfEvent) {

        return monthOfEvent > 0 && monthOfEvent <= 12;
    }

    private boolean isDayCorrect(int dayOfEvent) {

        return dayOfEvent > 0 && dayOfEvent <= 31;
    }

    private boolean isNumeric(String number) {

        return NumberUtils.isParsable(number);
    }

    private int parseInt(String stringToParse) {

        return (int) Double.parseDouble(stringToParse);
    }

    private boolean parseBoolean(String stringToParse) {

        return "1".equals(stringToParse) || "1.0".equals(stringToParse);
    }
}
