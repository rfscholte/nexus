package org.apache.maven.mercury.repository.local.map;

/**
 *
 *
 * @author Oleg Gusakov
 * @version $Id: StorageException.java 729223 2008-12-24 04:38:02Z ogusakov $
 *
 */
public class StorageException
    extends Exception
{

    /**
     * 
     */
    public StorageException()
    {
    }

    /**
     * @param message
     */
    public StorageException( String message )
    {
        super( message );
    }

    /**
     * @param cause
     */
    public StorageException( Throwable cause )
    {
        super( cause );
    }

    /**
     * @param message
     * @param cause
     */
    public StorageException( String message, Throwable cause )
    {
        super( message, cause );
    }

}
