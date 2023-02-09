package sr.we.ui.views.dashboard;

import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.NoData;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.LegendBuilder;
import com.github.appreciated.apexcharts.config.builder.ResponsiveBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.legend.Position;
import com.github.appreciated.apexcharts.config.responsive.builder.OptionsBuilder;

public class PieChartExample extends ApexChartsBuilder {

    public PieChartExample() {
        NoData noData = new NoData();
        noData.setText("No data present at the moment");
        withChart(ChartBuilder.get().withType(Type.PIE).withHeight("400px").build())
//                .withLabels("Team A", "Team B", "Team C", "Team D", "Team E")
                .withLegend(LegendBuilder.get().withPosition(Position.RIGHT).build())
//                .withSeries(44.0, 55.0, 13.0, 43.0, 22.0)
                .withResponsive(ResponsiveBuilder.get().withBreakpoint(480.0).withOptions(OptionsBuilder.get().withLegend(LegendBuilder.get().withPosition(Position.BOTTOM).build()).build()).build()).withNoData(noData);
    }
}
