package org.sonatype.nexus.test.launcher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.cli.StreamConsumer;

public class FileStreamConsumer
    extends AbstractLogEnabled
    implements StreamConsumer, Initializable, Disposable
{

    private File destination;

    private Writer writer;

    public void consumeLine( String line )
    {
        try
        {
            writer.append( line );
            writer.append( '\n' );
            writer.flush();
        }
        catch ( IOException e )
        {
            getLogger().error( e.getMessage(), e );
        }
    }

    public void initialize()
        throws InitializationException
    {
        try
        {
            writer = new FileWriter( destination );
        }
        catch ( IOException e )
        {
            throw new com.thoughtworks.xstream.InitializationException( e.getMessage(), e );
        }
    }

    public void dispose()
    {
        try
        {
            writer.close();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

}
