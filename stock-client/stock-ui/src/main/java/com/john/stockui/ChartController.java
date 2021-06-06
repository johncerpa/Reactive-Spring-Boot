package com.john.stockui;

import com.john.stockclient.StockClient;
import com.john.stockclient.StockPrice;
import com.john.stockclient.WebClientStockClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static java.lang.String.valueOf;
import static javafx.collections.FXCollections.observableArrayList;

@Component
public class ChartController {
    @FXML
    public LineChart<String, Double> chart;

    private final StockClient stockClient;

    public ChartController(StockClient stockClient) {
        this.stockClient = stockClient;
    }

    @FXML
    public void initialize() {
        // Multiple consumers symbol, symbol2
        String symbol = "SYMBOL1";
        final PriceSubscriber priceSubscriber = new PriceSubscriber(symbol);
        stockClient.pricesFor(symbol).subscribe(priceSubscriber);

        String symbol2 = "SYMBOL2";
        final PriceSubscriber priceSubscriber2 = new PriceSubscriber(symbol2);
        stockClient.pricesFor(symbol2).subscribe(priceSubscriber2);

        ObservableList<Series<String, Double>> data = observableArrayList();
        data.add(priceSubscriber.getSeries());
        data.add(priceSubscriber2.getSeries());

        chart.setData(data);
    }

    private static class PriceSubscriber implements Consumer<StockPrice> {
        private ObservableList<Data<String, Double>> seriesData = observableArrayList();

        public Series<String, Double> getSeries() {
            return series;
        }

        private final Series<String, Double> series;

        public PriceSubscriber(String symbol) {
            series = new Series<>(symbol, seriesData);
        }

        @Override
        public void accept(StockPrice stockPrice) {
            Platform.runLater(() -> {
                seriesData.add(new Data<>(valueOf(stockPrice.getTime().getSecond()), stockPrice.getPrice()));
            });
        }
    }
}
