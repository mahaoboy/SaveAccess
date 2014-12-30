package ut.com.winagile.statistics;

import org.junit.Test;
import com.winagile.statistics.MyPluginComponent;
import com.winagile.statistics.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}