package org.sonatype.nexus.index;

/**
 * Ontology of Nexus. This is still Maven2 specific. Ideally, Nexus Ontology should contain three things only: path,
 * sha1 and last_modified. And Indexer should index _everything_, and Maven should be just "topping" extending these
 * informations. This would enable us to search and easily detect maven metadata files too, or to support non-maven
 * repository indexing, like P2 is.
 * 
 * @author cstamas
 */
public interface NEXUS
{
    public static final String NEXUS_NAMESPACE = "urn:nexus#";

    // UINFO: Artifact Unique Info (groupId, artifactId, version, classifier, extension (or packaging))
    public static final Field UINFO = new Field( null, NEXUS_NAMESPACE, "uinfo", "Artifact Unique Info" );

    // DELETED: Deleted field marker (will contain UINFO if document is deleted from index)
    public static final Field DELETED = new Field( null, NEXUS_NAMESPACE, "del", "Deleted field marker" );

    // ==

    // PATH: The hierarchical path (in the repository!) of the indexed item
    public static final Field PATH = new Field( null, NEXUS_NAMESPACE, "path", "Artifact Path" );

    // SHA1: Item SHA1 checksum.
    public static final Field SHA1 = new Field( null, NEXUS_NAMESPACE, "sha1", "SHA1 checksum" );

    // LAST_MODIFIED: Item Last Modified Timestamp (UTC millis)
    public static final Field LAST_MODIFIED =
        new Field( null, NEXUS_NAMESPACE, "lastModified", "Last Modified Timestamp" );

    // LENGTH: Item File Length (in bytes)
    public static final Field LENGTH = new Field( null, NEXUS_NAMESPACE, "length", "Length" );

    // REPOSITORY: Item's originating repository ID
    public static final Field REPOSITORY_ID = new Field( null, NEXUS_NAMESPACE, "repositoryId", "Repository ID" );

    // CONTEXT: Item's originating indexing context ID
    public static final Field INDEXING_CONTEXT_ID =
        new Field( null, NEXUS_NAMESPACE, "indexingContextId", "IndexingContext ID" );
}
