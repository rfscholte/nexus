package org.sonatype.nexus.index;

import java.util.Collection;
import java.util.Map;

/**
 * Pulling out ArtifactInfo, clearing up. TBD. This gonna be extensible "map-like" class with fields.
 * 
 * @author cstamas
 */
public interface ArtifactInfoRecord
    extends Map<Field, FieldValue<?>>
{
    /**
     * Returns the item unique info (kinda primary key).
     * 
     * @return
     */
    String getUinfo();

    /**
     * Set's the item unique info (kinda primary key).
     * 
     * @param uinfo
     */
    void setUinfo( String uinfo );

    /**
     * Returns the full path of indexed item.
     * 
     * @return
     */
    String getPath();

    /**
     * Sets the full path of indexed item.
     * 
     * @param path
     */
    void setPath( String path );

    /**
     * Returns the SHA1 checksum of item.
     * 
     * @return
     */
    String getSha1Checksum();

    /**
     * Sets the SHA1 checksum of item.
     * 
     * @param sha1Checksum
     */
    void setSha1Checksum( String sha1Checksum );

    /**
     * Returns the lastModified timestamp.
     * 
     * @return
     */
    long getLastModified();

    /**
     * Sets the lastModified timestamp.
     * 
     * @param lastModified
     */
    void setLastModified( long lastModified );

    /**
     * Returns the length (in bytes) of the item.
     * 
     * @return
     */
    long getLength();

    /**
     * Sets the length of the item (in bytes).
     * 
     * @param length
     */
    void setLength( long length );

    /**
     * Returns the repository ID where this item belongs.
     * 
     * @return
     */
    String getRepository();

    /**
     * Sets the repository ID where this items belongs.
     * 
     * @param repository
     */
    void setRepository( String repository );

    /**
     * Returns the indexing context ID where this item belongs.
     * 
     * @return
     */
    String getIndexingContextId();

    /**
     * Sets the indexing context ID where this item belongs.
     * 
     * @param indexingContextId
     */
    void setIndexingContextId( String indexingContextId );

    /**
     * Adds a field value to this record.
     * 
     * @param value
     */
    void addFieldValue( FieldValue<?> value );

    /**
     * Removed a field from this record.
     * 
     * @param field
     */
    void removeField( Field field );

    /**
     * Returns existing fields in this ArtifactInfoRecord as unmodifiable collection.
     * 
     * @return
     */
    Collection<Field> getFields();

    /**
     * Returns the requested adapted record, if possible. The instance if probably cached, but not threadsafe!
     * 
     * @param <T>
     * @param recordClazz
     * @return
     */
    <T extends ArtifactInfoRecord> T adapt( Class<T> recordClazz );
}
