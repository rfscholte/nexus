package org.sonatype.nexus.index;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A @link {@link Map} adapter, used as base class for ArtifactInfoRecordAdapters. This class is actually not abstract,
 * but is marked as to make it clear it should not be used directly.
 * 
 * @author cstamas
 * @param <K>
 * @param <V>
 */
public abstract class AbstractMapAdapter<K, V>
    implements Map<K, V>
{
    private final Map<K, V> map;

    protected AbstractMapAdapter( Map<K, V> map )
    {
        this.map = map;
    }

    protected Map<K, V> getMap()
    {
        return map;
    }

    public void clear()
    {
        getMap().clear();
    }

    public boolean containsKey( Object key )
    {
        return getMap().containsKey( key );
    }

    public boolean containsValue( Object value )
    {
        return getMap().containsValue( value );
    }

    public Set<java.util.Map.Entry<K, V>> entrySet()
    {
        return getMap().entrySet();
    }

    public V get( Object key )
    {
        return getMap().get( key );
    }

    public boolean isEmpty()
    {
        return getMap().isEmpty();
    }

    public Set<K> keySet()
    {
        return getMap().keySet();
    }

    public V put( K key, V value )
    {
        return getMap().put( key, value );
    }

    public void putAll( Map<? extends K, ? extends V> m )
    {
        getMap().putAll( m );
    }

    public V remove( Object key )
    {
        return getMap().remove( key );
    }

    public int size()
    {
        return getMap().size();
    }

    public Collection<V> values()
    {
        return getMap().values();
    }
}
