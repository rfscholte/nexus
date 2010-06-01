package org.sonatype.nexus.index;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * The default implementation of "map like" dummy data holder. This class is central, but holds no logic whatsoever, it
 * is just a storage to be filled up by IndexCreators.
 * 
 * @author cstamas
 */
public class DefaultArtifactInfoRecord
    extends HashMap<Field, FieldValue<?>>
    implements ArtifactInfoRecord
{
    private static final long serialVersionUID = -7832979553424436651L;

    private static final HashMap<Class<? extends ArtifactInfoRecord>, ArtifactInfoRecordAdapterFactory> adapterFactories =
        new HashMap<Class<? extends ArtifactInfoRecord>, ArtifactInfoRecordAdapterFactory>();

    private final HashMap<Class<? extends ArtifactInfoRecord>, ArtifactInfoRecord> adapterInstances =
        new HashMap<Class<? extends ArtifactInfoRecord>, ArtifactInfoRecord>();

    public String getUinfo()
    {
        return (String) get( NEXUS.UINFO ).getValue();
    }

    public void setUinfo( String uinfo )
    {
        addFieldValue( new StringFieldValue( NEXUS.UINFO, uinfo ) );
    }

    public String getPath()
    {
        return (String) get( NEXUS.PATH ).getValue();
    }

    public void setPath( String path )
    {
        addFieldValue( new StringFieldValue( NEXUS.PATH, path ) );
    }

    public String getSha1Checksum()
    {
        return (String) get( NEXUS.SHA1 ).getValue();
    }

    public void setSha1Checksum( String sha1Checksum )
    {
        addFieldValue( new StringFieldValue( NEXUS.SHA1, sha1Checksum ) );
    }

    public long getLastModified()
    {
        return (Long) get( NEXUS.LAST_MODIFIED ).getValue();
    }

    public void setLastModified( long lastModified )
    {
        addFieldValue( new LongFieldValue( NEXUS.LAST_MODIFIED, lastModified ) );
    }

    public long getLength()
    {
        return (Long) get( NEXUS.LENGTH ).getValue();
    }

    public void setLength( long length )
    {
        addFieldValue( new LongFieldValue( NEXUS.LENGTH, length ) );
    }

    public String getRepository()
    {
        return (String) get( NEXUS.REPOSITORY_ID ).getValue();
    }

    public void setRepository( String repository )
    {
        addFieldValue( new StringFieldValue( NEXUS.REPOSITORY_ID, repository ) );
    }

    public String getIndexingContextId()
    {
        return (String) get( NEXUS.INDEXING_CONTEXT_ID ).getValue();
    }

    public void setIndexingContextId( String indexingContextId )
    {
        addFieldValue( new StringFieldValue( NEXUS.INDEXING_CONTEXT_ID, indexingContextId ) );
    }

    public void addFieldValue( FieldValue<?> value )
    {
        put( value.getField(), value );
    }

    public void removeField( Field field )
    {
        remove( field );
    }

    public Collection<Field> getFields()
    {
        return Collections.unmodifiableSet( keySet() );
    }

    public static <T extends ArtifactInfoRecord> void registerArtifactInfoRecordAdapterFactory(
                                                                                                Class<T> recordClazz,
                                                                                                ArtifactInfoRecordAdapterFactory factory )
    {
        if ( recordClazz == null )
        {
            throw new IllegalArgumentException( "recordClazz for registered adapter factory cannot be null!" );
        }

        if ( factory == null )
        {
            throw new IllegalArgumentException( "instance for registered adapter factory cannot be null!" );
        }

        adapterFactories.put( recordClazz, factory );
    }

    public <T extends ArtifactInfoRecord> T adapt( Class<T> recordClazz )
    {
        if ( !adapterInstances.containsKey( recordClazz ) )
        {
            // look for adapterFactory
            ArtifactInfoRecordAdapterFactory factory = adapterFactories.get( recordClazz );

            if ( factory != null )
            {
                ArtifactInfoRecordAdapter adapter = factory.createAdapter( this );

                if ( adapter != null )
                {
                    adapterInstances.put( recordClazz, adapter );
                }
            }

        }

        return recordClazz.cast( adapterInstances.get( recordClazz ) );
    }
}
