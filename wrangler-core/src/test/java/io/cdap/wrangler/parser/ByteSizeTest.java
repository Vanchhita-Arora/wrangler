package io.cdap.wrangler.parser;

import io.cdap.wrangler.api.parser.ByteSize;
import org.junit.Assert;
import org.junit.Test;

public class ByteSizeTest {
    @Test
    public void testByteSizeParsing() {
        ByteSize kb = new ByteSize("10KB");
        Assert.assertEquals(10 * 1024L, kb.getBytes());
        
        ByteSize mb = new ByteSize("5MB");
        Assert.assertEquals(5 * 1024L * 1024, mb.getBytes());
        
        ByteSize gb = new ByteSize("2GB");
        Assert.assertEquals(2L * 1024 * 1024 * 1024, gb.getBytes());
        
        ByteSize decimal = new ByteSize("1.5MB");
        Assert.assertEquals((long)(1.5 * 1024 * 1024), decimal.getBytes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidByteSize() {
        new ByteSize("invalid");
    }

    @Test
    public void testUnitConversion() {
        ByteSize size = new ByteSize("1MB");
        Assert.assertEquals(1024.0, size.getKB(), 0.001);
        Assert.assertEquals(1.0, size.getMB(), 0.001);
        Assert.assertEquals(1.0/1024, size.getGB(), 0.001);
    }
}