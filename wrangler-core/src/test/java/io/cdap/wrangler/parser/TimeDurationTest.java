package io.cdap.wrangler.parser;

import io.cdap.wrangler.api.parser.TimeDuration;
import org.junit.Assert;
import org.junit.Test;

public class TimeDurationTest {
    @Test
    public void testTimeDurationParsing() {
        TimeDuration ns = new TimeDuration("100ns");
        Assert.assertEquals(100L, ns.getNanoseconds());
        
        TimeDuration ms = new TimeDuration("500ms");
        Assert.assertEquals(500L * 1_000_000, ms.getNanoseconds());
        
        TimeDuration s = new TimeDuration("3s");
        Assert.assertEquals(3L * 1_000_000_000, s.getNanoseconds());
        
        TimeDuration m = new TimeDuration("2m");
        Assert.assertEquals(2L * 60 * 1_000_000_000, m.getNanoseconds());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTimeDuration() {
        new TimeDuration("invalid");
    }

    @Test
    public void testUnitConversion() {
        TimeDuration duration = new TimeDuration("1s");
        Assert.assertEquals(1000.0, duration.getMilliseconds(), 0.001);
        Assert.assertEquals(1.0, duration.getSeconds(), 0.001);
        Assert.assertEquals(1.0/60, duration.getMinutes(), 0.001);
    }
}