package org.sonatype.nexus.test;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class ProgressListener
    extends TestListenerAdapter
{
    @Override
    public void onTestStart( ITestResult result )
    {
        super.onTestStart( result );

        System.out.println( "\n----- Running: " + result.getTestClass().getName() + "." + result.getName()
            + "() -----\n" );
    }
}
