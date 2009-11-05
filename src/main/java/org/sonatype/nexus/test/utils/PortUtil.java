package org.sonatype.nexus.test.utils;

import java.io.IOException;
import java.net.ServerSocket;

public class PortUtil
{
    public static Integer getRandomPort()
        throws IOException
    {
        ServerSocket ss = new ServerSocket( 0 );
        try
        {
            return ss.getLocalPort();
        }
        finally
        {
            try
            {
                ss.close();
            }
            catch ( IOException e )
            {
                // no problem
            }
        }
    }
}
