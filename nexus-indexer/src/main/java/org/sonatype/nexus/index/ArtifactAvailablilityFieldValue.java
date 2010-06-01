package org.sonatype.nexus.index;

public class ArtifactAvailablilityFieldValue
    extends FieldValue<ArtifactAvailablility>
{
    public ArtifactAvailablilityFieldValue( Field field, ArtifactAvailablility value )
    {
        super( field, value );
    }

    @Override
    protected String asString( ArtifactAvailablility value )
    {
        return value.toString();
    }

    @Override
    protected ArtifactAvailablility asValue( String rawValue )
    {
        return ArtifactAvailablility.fromString( rawValue );
    }
}
