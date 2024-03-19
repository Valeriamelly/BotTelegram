package util;

import model.Reporte;
import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExcelLoader {
    public List<Reporte> cargarDatosDesdeExcel(String rutaArchivo) throws IOException {
        List<Reporte> reportes = new ArrayList<>();
        FileInputStream fileInputStream = new FileInputStream(rutaArchivo);
        DataFormatter formatter = new DataFormatter();

        try (Workbook workbook = WorkbookFactory.create(fileInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if(row.getRowNum() == 0) continue;

                String id = formatter.formatCellValue(row.getCell(0));
                LocalDate fechaReporte = null;
                String fechaReporteStr = formatter.formatCellValue(row.getCell(1));
                if (!fechaReporteStr.isEmpty()) {
                    fechaReporte = LocalDate.parse(fechaReporteStr, DateTimeFormatter.ofPattern("d/M/yy"));
                }
                String tipoReporte = formatter.formatCellValue(row.getCell(2));
                String modulo = formatter.formatCellValue(row.getCell(3));
                String componente = formatter.formatCellValue(row.getCell(4));
                String accion = formatter.formatCellValue(row.getCell(5));
                String observacion = formatter.formatCellValue(row.getCell(6));
                String solucion = formatter.formatCellValue(row.getCell(7));
                String prioridad = formatter.formatCellValue(row.getCell(8));
                String norma = formatter.formatCellValue(row.getCell(9));

                reportes.add(new Reporte(id, fechaReporte, tipoReporte, modulo,componente, accion, observacion, solucion, prioridad, norma ));
            }

        }
        return reportes;

    }
}
