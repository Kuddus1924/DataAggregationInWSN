package general.function;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    public static void print (ArrayList<Double> array,String name) {

        List<Double> tmp = new ArrayList<>();
        List<Double> tmp1 = new ArrayList<>();
        for(int i = 0;i < 8; i++)
        {
            tmp.add(0.1+(i*0.05));
            tmp1.add(7.0);
        }

        XYChart chart = new XYChartBuilder().width(800).height(600).title("Staticstic").xAxisTitle("вероятность захавта узла в кластере").yAxisTitle("M[]").build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setHasAnnotations(false);
        chart.getStyler().setPlotGridLinesVisible(false);

        chart.addSeries(name, tmp, array);
        //chart.addSeries("Моделируемое", tmp, tmp1);
        List<XYChart> charts = new ArrayList<XYChart>();
        charts.add(chart);
        new SwingWrapper<XYChart>(charts).displayChartMatrix();

    }
}
