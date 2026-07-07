package com.clinica.controlador;

import com.clinica.dao.ConsultasDAO;
import com.clinica.dao.impl.ConsultasDAOImpl;
import com.clinica.modelo.Usuario;
import com.clinica.util.Roles;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ExportarReporteServlet", urlPatterns = {"/ExportarReporteServlet"})
public class ExportarReporteServlet extends HttpServlet {

    private final ConsultasDAO consultasDAO = new ConsultasDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession();
        Usuario user = (Usuario) sesion.getAttribute("usuarioLogueado");
        if (user == null) { response.sendRedirect("index.jsp"); return; }
        if (user.getIdRol() != Roles.ADMINISTRADOR) { response.sendRedirect("DashboardServlet"); return; }

        String formato = request.getParameter("formato");
        List<Map<String, Object>> reporte = consultasDAO.obtenerReporteEspecialidades();

        if ("csv".equalsIgnoreCase(formato)) {
            exportarCsv(response, reporte);
        } else if ("excel".equalsIgnoreCase(formato)) {
            exportarExcel(response, reporte);
        } else if ("pdf".equalsIgnoreCase(formato)) {
            exportarPdf(response, reporte);
        } else {
            response.sendRedirect("ReportesServlet");
        }
    }

    private double eficiencia(Map<String, Object> r) {
        int total = (Integer) r.get("total");
        int atendidas = (Integer) r.get("atendidas");
        return total > 0 ? (atendidas * 100.0) / total : 0.0;
    }

    // ---------- CSV ----------
    private void exportarCsv(HttpServletResponse response, List<Map<String, Object>> reporte) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"reporte_especialidades.csv\"");
        PrintWriter out = response.getWriter();
        out.write('\uFEFF'); // BOM para que Excel respete las tildes
        out.println("Especialidad,Total citas,Atendidas,Pendientes,Eficiencia (%)");
        for (Map<String, Object> r : reporte) {
            out.printf("%s,%d,%d,%d,%.0f%n",
                    r.get("especialidad"), r.get("total"), r.get("atendidas"),
                    r.get("pendientes"), eficiencia(r));
        }
        out.flush();
    }

    // ---------- EXCEL ----------
    private void exportarExcel(HttpServletResponse response, List<Map<String, Object>> reporte) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"reporte_especialidades.xlsx\"");

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Reporte");

            org.apache.poi.ss.usermodel.Font negrita = wb.createFont();
            negrita.setBold(true);
            negrita.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            CellStyle estiloHead = wb.createCellStyle();
            estiloHead.setFont(negrita);
            estiloHead.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.SEA_GREEN.getIndex());
            estiloHead.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

            String[] cabeceras = {"Especialidad", "Total citas", "Atendidas", "Pendientes", "Eficiencia (%)"};
            Row head = sheet.createRow(0);
            for (int i = 0; i < cabeceras.length; i++) {
                Cell c = head.createCell(i);
                c.setCellValue(cabeceras[i]);
                c.setCellStyle(estiloHead);
            }

            int fila = 1;
            for (Map<String, Object> r : reporte) {
                Row row = sheet.createRow(fila++);
                row.createCell(0).setCellValue(String.valueOf(r.get("especialidad")));
                row.createCell(1).setCellValue((Integer) r.get("total"));
                row.createCell(2).setCellValue((Integer) r.get("atendidas"));
                row.createCell(3).setCellValue((Integer) r.get("pendientes"));
                row.createCell(4).setCellValue(Math.round(eficiencia(r)));
            }
            for (int i = 0; i < cabeceras.length; i++) sheet.autoSizeColumn(i);

            wb.write(response.getOutputStream());
        }
    }

    // ---------- PDF ----------
    private void exportarPdf(HttpServletResponse response, List<Map<String, Object>> reporte) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"reporte_especialidades.pdf\"");

        Document doc = new Document(PageSize.A4, 40, 40, 50, 40);
        try (OutputStream os = response.getOutputStream()) {
            PdfWriter.getInstance(doc, os);
            doc.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(15, 110, 86));
            Font sub = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.DARK_GRAY);

            Paragraph h1 = new Paragraph("Clínica Universitaria UPN", titulo);
            doc.add(h1);
            Paragraph h2 = new Paragraph("Reporte de rendimiento por especialidad", sub);
            h2.setSpacingAfter(16);
            doc.add(h2);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3.2f, 1.5f, 1.5f, 1.5f, 1.7f});

            Font fh = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            String[] cabeceras = {"Especialidad", "Total citas", "Atendidas", "Pendientes", "Eficiencia (%)"};
            for (String cab : cabeceras) {
                PdfPCell cell = new PdfPCell(new Phrase(cab, fh));
                cell.setBackgroundColor(new Color(15, 110, 86));
                cell.setPadding(6);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            Font fc = FontFactory.getFont(FontFactory.HELVETICA, 10);
            for (Map<String, Object> r : reporte) {
                table.addCell(celda(String.valueOf(r.get("especialidad")), fc, Element.ALIGN_LEFT));
                table.addCell(celda(String.valueOf(r.get("total")), fc, Element.ALIGN_CENTER));
                table.addCell(celda(String.valueOf(r.get("atendidas")), fc, Element.ALIGN_CENTER));
                table.addCell(celda(String.valueOf(r.get("pendientes")), fc, Element.ALIGN_CENTER));
                table.addCell(celda(String.format("%.0f%%", eficiencia(r)), fc, Element.ALIGN_CENTER));
            }

            doc.add(table);
            doc.close();
        }
    }

    private PdfPCell celda(String texto, Font f, int alineacion) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, f));
        cell.setPadding(5);
        cell.setHorizontalAlignment(alineacion);
        return cell;
    }
}

