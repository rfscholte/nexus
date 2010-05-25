package org.sonatype.nexus.index;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.search.highlight.Fragmenter;

public class OneLineFragmenter
    implements Fragmenter
{
    private String text;

    private OffsetAttribute offsetAttribute;

    public void start( String originalText, TokenStream stream )
    {
        setText( originalText );

        setOffsetAttribute( stream.addAttribute( OffsetAttribute.class ) );
    }

    public boolean isNewFragment()
    {
        // text: /org/sonatype/...
        // tokens: org sonatype
        boolean result =
            isNewline( getChar( getOffsetAttribute().startOffset() - 1 ) )
                || isNewline( getChar( getOffsetAttribute().startOffset() - 2 ) );

        return result;
    }

    protected boolean isNewline( char c )
    {
        return c == '\n';
    }

    protected char getChar( int pos )
    {
        if ( ( pos < 0 ) || ( pos > ( getText().length() - 1 ) ) )
        {
            // return no newline ;)
            return ' ';
        }
        else
        {
            return getText().charAt( pos );
        }
    }

    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }

    public OffsetAttribute getOffsetAttribute()
    {
        return offsetAttribute;
    }

    public void setOffsetAttribute( OffsetAttribute offsetAttribute )
    {
        this.offsetAttribute = offsetAttribute;
    }
}
