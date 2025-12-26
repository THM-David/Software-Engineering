package de.thm.se.backend.util;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DatabaseExporter {

    private static final String EXPORT_DIR = "exports";
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * Exportiert alle Tabellen der Datenbank in eine XLSX-Datei mit mehreren Sheets.
     *
     * Jede Tabelle wird als separates Arbeitsblatt in der Excel-Datei angelegt.
     * Die erste Zeile jedes Sheets enthält die Spaltennamen als Header.
     *
     * @return Path zur erstellten XLSX-Datei
     * @throws SQLException wenn Datenbankfehler auftreten
     * @throws IOException wenn Dateisystemfehler auftreten
     */
    public Path exportToExcel() throws SQLException, IOException {
        // Export-Verzeichnis erstellen
        Path exportDir = Paths.get(EXPORT_DIR);
        Files.createDirectories(exportDir);

        // Dateiname mit Zeitstempel
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path excelFile = exportDir.resolve("Datenbankexport_" + timestamp + ".xlsx");

        Connection conn = null;
        Workbook workbook = null;

        try {
            conn = DatabaseConnection.connect();
            workbook = new XSSFWorkbook();

            // Liste aller Tabellen
            String[] tables = {
                    "STUDIERENDE",
                    "STUDIENGANG",
                    "PRUEFUNGSORDNUNG",
                    "SEMESTERZEIT",
                    "SEMESTER",
                    "WISSENSCHAFTLICHE_ARBEIT",
                    "ZEITDATEN",
                    "BETREUER",
                    "NOTENBESTANDTEIL",
                    "SWS_BERECHNUNG",
                    "EXTERNE_DATENQUELLE",
                    "IMPORTVORGANG",
                    "FACHBEREICH"
            };

            // Jede Tabelle als separates Sheet exportieren
            for (String tableName : tables) {
                exportTableToSheet(conn, workbook, tableName);
            }

            // Excel-Datei speichern
            try (FileOutputStream fileOut = new FileOutputStream(excelFile.toFile())) {
                workbook.write(fileOut);
            }

            System.out.println("Export erfolgreich abgeschlossen: " + excelFile.toAbsolutePath());
            return excelFile;

        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    System.err.println("Fehler beim Schließen der Excel-Datei: " + e.getMessage());
                }
            }
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Exportiert eine Tabelle in ein Excel-Sheet.
     *
     * @param conn Datenbankverbindung
     * @param workbook Excel-Workbook
     * @param tableName Name der zu exportierenden Tabelle
     * @throws SQLException wenn Datenbankfehler auftreten
     */
    private void exportTableToSheet(Connection conn, Workbook workbook, String tableName)
            throws SQLException {

        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Sheet erstellen (Name auf 31 Zeichen begrenzt für Excel)
            String sheetName = tableName.length() > 31 ? tableName.substring(0, 31) : tableName;
            Sheet sheet = workbook.createSheet(sheetName);

            // Daten abfragen
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Styles erstellen
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);

            // Header-Zeile erstellen
            Row headerRow = sheet.createRow(0);
            for (int i = 1; i <= columnCount; i++) {
                Cell cell = headerRow.createCell(i - 1);
                cell.setCellValue(metaData.getColumnName(i));
                cell.setCellStyle(headerStyle);
            }

            // Datenzeilen erstellen
            int rowNum = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);

                for (int i = 1; i <= columnCount; i++) {
                    Cell cell = row.createCell(i - 1);

                    // Typ-gerechte Behandlung der Werte
                    int columnType = metaData.getColumnType(i);
                    switch (columnType) {
                        case Types.INTEGER:
                        case Types.BIGINT:
                        case Types.SMALLINT:
                        case Types.TINYINT:
                            int intValue = rs.getInt(i);
                            if (!rs.wasNull()) {
                                cell.setCellValue(intValue);
                            }
                            break;

                        case Types.REAL:
                        case Types.FLOAT:
                        case Types.DOUBLE:
                        case Types.DECIMAL:
                        case Types.NUMERIC:
                            double doubleValue = rs.getDouble(i);
                            if (!rs.wasNull()) {
                                cell.setCellValue(doubleValue);
                            }
                            break;

                        case Types.DATE:
                            Date dateValue = rs.getDate(i);
                            if (dateValue != null) {
                                cell.setCellValue(dateValue);
                                cell.setCellStyle(dateStyle);
                            }
                            break;

                        case Types.TIMESTAMP:
                            Timestamp timestampValue = rs.getTimestamp(i);
                            if (timestampValue != null) {
                                cell.setCellValue(timestampValue);
                                cell.setCellStyle(dateStyle);
                            }
                            break;

                        default:
                            String stringValue = rs.getString(i);
                            if (stringValue != null) {
                                cell.setCellValue(stringValue);
                            }
                            break;
                    }
                }
            }

            // Spaltenbreiten automatisch anpassen
            for (int i = 0; i < columnCount; i++) {
                sheet.autoSizeColumn(i);
                // Maximalbreite setzen (verhindert zu breite Spalten)
                if (sheet.getColumnWidth(i) > 15000) {
                    sheet.setColumnWidth(i, 15000);
                }
            }

            System.out.println("Tabelle " + tableName + " exportiert: " + (rowNum - 1) + " Datensätze");

        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closeStatement(stmt);
        }
    }

    /**
     * Erstellt einen Style für Header-Zellen.
     *
     * @param workbook Excel-Workbook
     * @return CellStyle für Header
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * Erstellt einen Style für Datums-Zellen.
     *
     * @param workbook Excel-Workbook
     * @return CellStyle für Datumsfelder
     */
    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd.MM.yyyy"));
        return style;
    }

    /**
     * Liefert Statistiken über die exportierte Excel-Datei.
     *
     * @param excelFile Pfad zur Excel-Datei
     * @return Statistik-String
     * @throws IOException wenn Dateisystemfehler auftreten
     */
    public String getExportStatistics(Path excelFile) throws IOException {
        StringBuilder stats = new StringBuilder();
        stats.append("Export-Statistik:").append(LINE_SEPARATOR);
        stats.append("Datei: ").append(excelFile.toAbsolutePath()).append(LINE_SEPARATOR);

        if (Files.exists(excelFile)) {
            long size = Files.size(excelFile);
            stats.append("Größe: ").append(formatFileSize(size)).append(LINE_SEPARATOR);
        }

        return stats.toString();
    }

    /**
     * Formatiert eine Dateigröße in menschenlesbares Format.
     *
     * @param size Größe in Bytes
     * @return Formatierte Größe
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " Bytes";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        }
    }

    /**
     * Beispiel-Main-Methode zum Testen des Exports.
     */
    public static void main(String[] args) {
        DatabaseExporter exporter = new DatabaseExporter();

        try {
            // Excel-Export durchführen
            Path excelFile = exporter.exportToExcel();

            // Statistiken ausgeben
            System.out.println(LINE_SEPARATOR + exporter.getExportStatistics(excelFile));

        } catch (SQLException e) {
            System.err.println("Datenbankfehler beim Export: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Dateisystemfehler beim Export: " + e.getMessage());
            e.printStackTrace();
        }
    }
}