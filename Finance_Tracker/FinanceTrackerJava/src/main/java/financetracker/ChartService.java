package financetracker;

import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.awt.Color;
import java.awt.Font;

/** Builds the pie, bar, and line charts used by the reports tab. */
public class ChartService {
    /** Creates a spending-by-category pie chart. */
    public JFreeChart createPieChart(Map<String, Long> totals) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        totals.forEach(dataset::setValue);
        JFreeChart chart = ChartFactory.createPieChart("Spending by Category", dataset, true, true, false);
        chart.setTitle((String) null);
        chart.setBackgroundPaint(Theme.SURFACE);
        if (chart.getTitle() != null) {
            chart.getTitle().setPaint(Theme.TEXT);
            chart.getTitle().setFont(Theme.BOLD);
        }
        PiePlot<?> plot = (PiePlot<?>) chart.getPlot(); plot.setBackgroundPaint(Theme.SURFACE); plot.setOutlineVisible(false); plot.setShadowGenerator(null);
        plot.setSectionPaint("Food", Color.decode("#818CF8"));
        plot.setSectionPaint("Travel", Color.decode("#2DD4BF"));
        plot.setSectionPaint("Rent", Color.decode("#FBBF24"));
        plot.setSectionPaint("Bills", Color.decode("#FB7185"));
        plot.setLabelGenerator(null);
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        if (chart.getLegend() != null) {
            chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 12));
            chart.getLegend().setPosition(org.jfree.chart.ui.RectangleEdge.BOTTOM);
        }
        return chart;
    }

    /** Creates a monthly income versus expense bar chart. */
    public JFreeChart createBarChart(Map<YearMonth, long[]> monthly) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        monthly.forEach((month, values) -> { dataset.addValue(values[0], "Income", month.toString()); dataset.addValue(values[1], "Expense", month.toString()); });
        JFreeChart chart = ChartFactory.createBarChart("Monthly Income vs Expense", "Month", "Amount (₹)", dataset);
        chart.setTitle((String) null);
        chart.setBackgroundPaint(Theme.SURFACE);
        if (chart.getTitle() != null) {
            chart.getTitle().setPaint(Theme.TEXT);
            chart.getTitle().setFont(Theme.BOLD);
        }
        CategoryPlot plot = (CategoryPlot) chart.getPlot(); plot.setBackgroundPaint(Theme.SURFACE); plot.setOutlineVisible(false); plot.setRangeGridlinePaint(Theme.BORDER);
        plot.getRangeAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 12)); plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11)); plot.getRenderer().setSeriesPaint(0, Theme.INCOME); plot.getRenderer().setSeriesPaint(1, Theme.EXPENSE); ((org.jfree.chart.renderer.category.BarRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter()); ((org.jfree.chart.renderer.category.BarRenderer) plot.getRenderer()).setShadowVisible(false);
        return chart;
    }

    /** Creates a line chart showing balance trend by month. */
    public JFreeChart createLineChart(Map<YearMonth, long[]> monthly) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        long balance = 0;
        for (Map.Entry<YearMonth, long[]> entry : monthly.entrySet()) {
            balance += entry.getValue()[0] - entry.getValue()[1];
            dataset.addValue(balance, "Balance", entry.getKey().toString());
        }
        JFreeChart chart = ChartFactory.createLineChart("Balance Trend", "Month", "Amount (₹)", dataset);
        chart.setTitle((String) null);
        chart.setBackgroundPaint(Theme.SURFACE);
        if (chart.getTitle() != null) {
            chart.getTitle().setPaint(Theme.TEXT);
            chart.getTitle().setFont(Theme.BOLD);
        }
        CategoryPlot plot = (CategoryPlot) chart.getPlot(); plot.setBackgroundPaint(Theme.SURFACE); plot.setOutlineVisible(false); plot.setRangeGridlinePaint(Theme.BORDER); plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11)); plot.getRenderer().setSeriesPaint(0, Theme.ACCENT);
        return chart;
    }
}
