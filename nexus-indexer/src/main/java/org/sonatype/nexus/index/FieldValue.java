package org.sonatype.nexus.index;

public abstract class FieldValue<T>
{
    private final Field field;

    private T value;

    public FieldValue( final Field field, final T value )
    {
        this.field = field;

        this.value = value;
    }

    public Field getField()
    {
        return field;
    }

    public T getValue()
    {
        return value;
    }

    public void setValue( T value )
    {
        this.value = value;
    }

    public Class<?> getValueType()
    {
        return getClass().getTypeParameters()[0].getClass();
    }

    protected abstract T asValue( String rawValue );

    protected abstract String asString( T value );

    public String getRawValue()
    {
        if ( value != null )
        {
            return asString( value );
        }
        else
        {
            return null;
        }
    }

    public void setRawValue( String rawValue )
    {
        this.value = asValue( rawValue );
    }

    public String toString()
    {
        return field.getFQN() + " = \"" + getRawValue() + "\"";
    }
}
