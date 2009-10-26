package org.sonatype.nexus.integrationtests.nexus166;

import javax.swing.JOptionPane;

import org.sonatype.nexus.test.utils.NexusStatusUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class StartStopLeak
{

    @BeforeClass
    public static void start()
        throws Exception
    {
        //JOptionPane.showConfirmDialog( null, "Start" );
        NexusStatusUtil.start();
    }

    @AfterClass
    public static void stop()
        throws Exception
    {
        //JOptionPane.showConfirmDialog( null, "Stop" );
        NexusStatusUtil.stop();
        System.gc();
        JOptionPane.showConfirmDialog( null, "Stoped" );
    }

    @Test
    public void test()
    {
        System.out.println( "In use: " + ( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() )
            / 1024 / 1024 );
    }
}
