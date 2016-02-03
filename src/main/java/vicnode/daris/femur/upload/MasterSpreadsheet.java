package vicnode.daris.femur.upload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import arc.utils.ObjectUtil;

public class MasterSpreadsheet
        implements Iterable<MasterSpreadsheet.SubjectRecord> {

    private List<SubjectRecord> _records;

    public MasterSpreadsheet(File file) throws Throwable {
        _records = new ArrayList<SubjectRecord>();
        HSSFWorkbook workbook = null;
        HSSFSheet sheet = null;
        try (InputStream is = new BufferedInputStream(
                new FileInputStream(file))) {
            try {
                workbook = new HSSFWorkbook(is);
                sheet = workbook.getSheetAt(0);
                for (Row row : sheet) {
                    SubjectRecord record = SubjectRecord.create((HSSFRow) row);
                    if (record != null) {
                        _records.add(record);
                    }
                }
            } finally {
                if (workbook != null) {
                    workbook.close();
                }
            }
        }
    }

    public SubjectRecord getRecord(String specimenNo) {
        for (SubjectRecord record : _records) {
            String s = record.specimenNoString();
            if (ObjectUtil.equals(s, specimenNo)
                    || s.startsWith(specimenNo + "/")
                    || s.indexOf("/" + specimenNo + "/") > 0
                    || s.endsWith("/" + specimenNo)) {
                return record;
            }
        }
        return null;
    }

    public SubjectRecord getRecord(int specimenNo) {
        return getRecord(Integer.toString(specimenNo));
    }

    public SubjectRecord getRecordAt(int index) {
        return _records.get(index);
    }

    public int getNumberOfRecords() {
        return _records.size();
    }

    public static class SubjectRecord {
        public static SubjectRecord create(HSSFRow row) throws Throwable {
            HSSFCell cell = row.getCell(0);
            if (cell != null) {
                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    return new SubjectRecord(row);
                } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                    String scv = cell.getStringCellValue();
                    if (scv != null) {
                        scv = scv.trim();
                        if (!scv.equals("") && !scv.startsWith("Specimen")
                                && scv.matches("^\\d+[/]?\\d+$")) {
                            return new SubjectRecord(row);
                        }
                    }
                }
            }
            return null;
        }

        public final int[] specimenNo;
        public final String vifmCaseNo;
        public final Float age;
        public final String sex;
        public final Float height;
        public final Float weight;
        public final String specimenType;
        public final boolean blood;
        public final boolean hardGroundSections;
        public final boolean microradiographs;
        public final boolean twoInchGlassPlates;
        public final boolean mountedSections;
        public final boolean plainRadiographOfPelvis;
        public final boolean clinicalCT;
        public final boolean pQCT;
        public final boolean microCT;
        public final boolean midShaftPorosityAndCrossSectionalGeometryData;
        public final boolean autopsyReportOrMedicalQuestionnaire;
        public final Integer columnS;// the last column. Ask Rita.

        private SubjectRecord(HSSFRow row) throws Throwable {
            specimenNo = readSpecimenNo(row, 0);
            vifmCaseNo = readVifmCaseNo(row, 1);
            age = readFloatCellValue(row, 2);
            sex = readSex(row, 3);
            height = readFloatCellValue(row, 4);
            weight = readFloatCellValue(row, 5);
            String st = readStringCellValue(row, 6);
            if (st != null) {
                if (st.startsWith("OA")) {
                    st = "OA" + st.substring(2).toLowerCase();
                } else {
                    st = st.toLowerCase();
                }
            }
            specimenType = st;
            blood = readBooleanCellValue(row, 7, false);
            hardGroundSections = readBooleanCellValue(row, 8, false);
            microradiographs = readBooleanCellValue(row, 9, false);
            twoInchGlassPlates = readBooleanCellValue(row, 10, false);
            mountedSections = readBooleanCellValue(row, 11, false);
            plainRadiographOfPelvis = readBooleanCellValue(row, 12, false);
            clinicalCT = readBooleanCellValue(row, 13, false);
            pQCT = readBooleanCellValue(row, 14, false);
            microCT = readBooleanCellValue(row, 15, false);
            midShaftPorosityAndCrossSectionalGeometryData = readBooleanCellValue(
                    row, 16, false);
            autopsyReportOrMedicalQuestionnaire = readBooleanCellValue(row, 17,
                    false);
            columnS = readIntegerCellValue(row, 18);
        }

        public String specimenNoString() {
            if (specimenNo == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < specimenNo.length; i++) {
                if (i > 0) {
                    sb.append("/");
                }
                sb.append(specimenNo[i]);
            }
            return sb.toString();
        }

        public Set<String> tags() {
            Set<String> tags = new TreeSet<String>();
            if (specimenType != null) {
                if (specimenType.equalsIgnoreCase("2cm mid-shaft block")) {
                    tags.add("2cm mid-shaft block");
                } else if (specimenType.equalsIgnoreCase("proximal femur")) {
                    tags.add("proximal femur");
                } else if (specimenType
                        .equalsIgnoreCase("OA post-op femoral heads")) {
                    tags.add("OA post-op femoral heads");
                } else if (specimenType.equalsIgnoreCase("Short Stack CT")) {
                    tags.add("whole femur");
                    tags.add("short stack CT");
                }
            }
            if (clinicalCT) {
                tags.add("Clinical CT");
            }
            if (microCT) {
                tags.add("microCT");
                tags.add("short stack CT");
            }
            if (pQCT) {
                tags.add("pQCT");
            }
            if (microradiographs) {
                tags.add("microradiography");
            }
            return tags;
        }

        private static int[] readSpecimenNo(HSSFRow row, int colIndex)
                throws Throwable {
            HSSFCell cell = row.getCell(colIndex);
            if (cell == null) {
                return null;
            }
            int[] specimenNo;
            int cellType = cell.getCellType();
            if (cellType == Cell.CELL_TYPE_STRING) {
                String sv = cell.getStringCellValue();
                String[] ns = sv.split("/");
                specimenNo = new int[ns.length];
                for (int i = 0; i < ns.length; i++) {
                    specimenNo[i] = Integer.parseInt(ns[i]);
                }
            } else if (cellType == Cell.CELL_TYPE_NUMERIC) {
                specimenNo = new int[] { (int) cell.getNumericCellValue() };
            } else {
                throw new Exception(
                        "Failed to read Specimen Number. Invalid cell type: "
                                + cellType);
            }
            return specimenNo;
        }

        private static String readVifmCaseNo(HSSFRow row, int colIndex)
                throws Throwable {
            HSSFCell cell = row.getCell(colIndex);
            if (cell == null) {
                return null;
            }
            int cellType = cell.getCellType();
            if (cellType == Cell.CELL_TYPE_STRING) {
                return cell.getStringCellValue();
            } else if (cellType == Cell.CELL_TYPE_NUMERIC) {
                return Integer.toString((int) cell.getNumericCellValue());
            } else {
                throw new Exception(
                        "Failed to VIFM Case Number. Invalid cell type: "
                                + cellType);
            }
        }

        private static String readSex(HSSFRow row, int colIndex)
                throws Throwable {
            HSSFCell cell = row.getCell(colIndex);
            if (cell == null) {
                return null;
            }
            int cellType = cell.getCellType();
            if (cellType == Cell.CELL_TYPE_STRING) {
                String v = cell.getStringCellValue();
                if (v == null) {
                    return "unknown";
                }
                v = v.trim();
                if (v.equals("?")) {
                    return "unknown";
                } else {
                    if (v.endsWith("?")) {
                        v = v.substring(0, v.length() - 1);
                    }
                    if (v.equalsIgnoreCase("M") || v.equalsIgnoreCase("MALE")) {
                        return "male";
                    } else if (v.equalsIgnoreCase("F")
                            || v.equalsIgnoreCase("FEMALE")) {
                        return "female";
                    } else {
                        throw new Exception("Unknown sex value: " + v);
                    }
                }
            } else {
                throw new Exception(
                        "Failed to read Sex. Invalid cell type: " + cellType);
            }
        }

        private static boolean readBooleanCellValue(HSSFRow row, int colIndex,
                boolean defaultValue) {
            HSSFCell cell = row.getCell(colIndex);
            if (cell == null) {
                return defaultValue;
            }
            int cellType = cell.getCellType();
            if (cellType == Cell.CELL_TYPE_BOOLEAN) {
                return cell.getBooleanCellValue();
            } else if (cellType == Cell.CELL_TYPE_STRING) {
                String sv = cell.getStringCellValue();
                if (sv == null) {
                    return defaultValue;
                }
                return sv.equalsIgnoreCase("True") || sv.equalsIgnoreCase("T")
                        || sv.equalsIgnoreCase("Yes")
                        || sv.equalsIgnoreCase("Y");
            } else {
                return defaultValue;
            }
        }

        private static Float readFloatCellValue(HSSFRow row, int colIndex) {
            HSSFCell cell = row.getCell(colIndex);
            if (cell == null) {
                return null;
            }
            int cellType = cell.getCellType();
            if (cellType == Cell.CELL_TYPE_NUMERIC) {
                return (float) cell.getNumericCellValue();
            } else {
                return null;
            }
        }

        private static String readStringCellValue(HSSFRow row, int colIndex) {
            HSSFCell cell = row.getCell(colIndex);
            if (cell == null) {
                return null;
            }
            int cellType = cell.getCellType();
            if (cellType == Cell.CELL_TYPE_STRING) {
                return cell.getStringCellValue();
            } else {
                return null;
            }
        }

        private static Integer readIntegerCellValue(HSSFRow row, int colIndex) {
            HSSFCell cell = row.getCell(colIndex);
            if (cell == null) {
                return null;
            }
            int cellType = cell.getCellType();
            if (cellType == Cell.CELL_TYPE_NUMERIC) {
                return (int) cell.getNumericCellValue();
            } else {
                return null;
            }
        }
    }

    @Override
    public Iterator<SubjectRecord> iterator() {
        return _records.iterator();
    }
}
