package io.cdap.wrangler.executor;

import io.cdap.wrangler.TestingRig;
import io.cdap.wrangler.api.RecipePipeline;
import io.cdap.wrangler.api.Row;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AggregateStatsTest {
    @Test
    public void testAggregateStats() throws Exception {
        List<Row> rows = new ArrayList<>();
        rows.add(new Row("data_size", "10KB").add("response_time", "100ms"));
        rows.add(new Row("data_size", "5MB").add("response_time", "500ms"));
        rows.add(new Row("data_size", "1MB").add("response_time", "1s"));

        String[] recipe = new String[] {
            // Added quotes around column names for consistency
            "aggregate-stats :data_size :response_time total_size_mb total_time_sec MB s total"
        };

        RecipePipeline pipeline = TestingRig.execute(recipe, rows);
        List<Row> results = pipeline.rows();
        
        Assert.assertEquals(1, results.size());
        Row result = results.get(0);
        
        // More precise calculation:
        // 10KB = 10240 bytes
        // 5MB = 5242880 bytes
        // 1MB = 1048576 bytes
        // Total = 10240 + 5242880 + 1048576 = 6301696 bytes
        // 6301696 / (1024*1024) = 6.010009766 MB
        Assert.assertEquals(6.010009766, (double) result.getValue("total_size_mb"), 0.000000001);
        
        // 100ms = 0.1s
        // 500ms = 0.5s
        // 1s = 1.0s
        // Total = 1.6s
        Assert.assertEquals(1.6, (double) result.getValue("total_time_sec"), 0.000000001);
    }

    @Test
    public void testAverageAggregation() throws Exception {
        List<Row> rows = new ArrayList<>();
        rows.add(new Row("size", "10KB").add("time", "100ms"));
        rows.add(new Row("size", "20KB").add("time", "200ms"));

        String[] recipe = new String[] {
            // Explicitly added "average" at the end
            "aggregate-stats :size :time avg_size avg_time KB ms average"
        };

        RecipePipeline pipeline = TestingRig.execute(recipe, rows);
        List<Row> results = pipeline.rows();
        Assert.assertEquals(1, results.size());
        Row result = results.get(0);
        
        // 10KB + 20KB = 30KB / 2 = 15KB
        Assert.assertEquals(15.0, (double) result.getValue("avg_size"), 0.000000001);
        
        // 100ms + 200ms = 300ms / 2 = 150ms
        Assert.assertEquals(150.0, (double) result.getValue("avg_time"), 0.000000001);
    }

    @Test(expected = Exception.class)
    public void testInvalidByteSize() throws Exception {
        List<Row> rows = new ArrayList<>();
        rows.add(new Row("size", "10XB").add("time", "100ms")); // Invalid byte size
        
        String[] recipe = new String[] {
            "aggregate-stats :size :time total_size total_time MB s"
        };
        
        TestingRig.execute(recipe, rows);
    }

    @Test(expected = Exception.class)
    public void testInvalidTimeDuration() throws Exception {
        List<Row> rows = new ArrayList<>();
        rows.add(new Row("size", "10KB").add("time", "100xs")); // Invalid time unit
        
        String[] recipe = new String[] {
            "aggregate-stats :size :time total_size total_time MB s"
        };
        
        TestingRig.execute(recipe, rows);
    }
}