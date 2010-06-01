package org.sonatype.nexus.index;

import java.util.Collection;

/**
 * The base class for ArtifactInfoRecord adapters.
 * 
 * @author cstamas
 */
public class AbstractArtifactInfoRecordAdapter
    extends AbstractMapAdapter<Field, FieldValue<?>>
    implements ArtifactInfoRecord, ArtifactInfoRecordAdapter
{
    protected AbstractArtifactInfoRecordAdapter( ArtifactInfoRecord record )
    {
        super( record );
    }

    public ArtifactInfoRecord getArtifactInfoRecord()
    {
        return (ArtifactInfoRecord) getMap();
    }

    public String getUinfo()
    {
        return getArtifactInfoRecord().getUinfo();
    }

    public void setUinfo( String uinfo )
    {
        getArtifactInfoRecord().setUinfo( uinfo );
    }

    public String getPath()
    {
        return getArtifactInfoRecord().getPath();
    }

    public void setPath( String path )
    {
        getArtifactInfoRecord().setPath( path );
    }

    public String getSha1Checksum()
    {
        return getArtifactInfoRecord().getSha1Checksum();
    }

    public void setSha1Checksum( String sha1Checksum )
    {
        getArtifactInfoRecord().setSha1Checksum( sha1Checksum );
    }

    public long getLastModified()
    {
        return getArtifactInfoRecord().getLastModified();
    }

    public void setLastModified( long lastModified )
    {
        getArtifactInfoRecord().setLastModified( lastModified );
    }

    public long getLength()
    {
        return getArtifactInfoRecord().getLength();
    }

    public void setLength( long length )
    {
        getArtifactInfoRecord().setLength( length );
    }

    public String getRepository()
    {
        return getArtifactInfoRecord().getRepository();
    }

    public void setRepository( String repository )
    {
        getArtifactInfoRecord().setRepository( repository );
    }

    public String getIndexingContextId()
    {
        return getArtifactInfoRecord().getIndexingContextId();
    }

    public void setIndexingContextId( String indexingContextId )
    {
        getArtifactInfoRecord().setIndexingContextId( indexingContextId );
    }

    public void addFieldValue( FieldValue<?> value )
    {
        getArtifactInfoRecord().addFieldValue( value );
    }

    public void removeField( Field field )
    {
        getArtifactInfoRecord().removeField( field );
    }

    public Collection<Field> getFields()
    {
        return getArtifactInfoRecord().getFields();
    }

    public <T extends ArtifactInfoRecord> T adapt( Class<T> recordClazz )
    {
        return getArtifactInfoRecord().adapt( recordClazz );
    }
}
