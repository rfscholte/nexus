package org.sonatype.nexus.integrationtests.nexus1961;

import org.restlet.data.MediaType;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.sonatype.nexus.index.treeview.TreeNode;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.integrationtests.RequestFacade;
import org.sonatype.nexus.rest.indextreeview.IndexBrowserTreeNode;
import org.sonatype.nexus.rest.indextreeview.IndexBrowserTreeViewResponseDTO;
import org.sonatype.nexus.test.utils.RepositoryMessageUtil;
import org.sonatype.nexus.test.utils.XStreamFactory;
import org.sonatype.plexus.rest.representation.XStreamRepresentation;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class Nexus1961IndexContentIT
    extends AbstractNexusIntegrationTest
{

    @BeforeClass
    public static void init()
        throws Exception
    {
        cleanWorkDir();
    }

    @Override
    protected void runOnce()
        throws Exception
    {
        super.runOnce();

        RepositoryMessageUtil.updateIndexes( REPO_TEST_HARNESS_REPO );
    }

    @Test
    public void getIndexContent()
        throws Exception
    {
        String serviceURI = "service/local/repositories/" + REPO_TEST_HARNESS_REPO + "/index_content/";

        Response response = RequestFacade.doGetRequest( serviceURI );
        String responseText = response.getEntity().getText();
        Status status = response.getStatus();
        AssertJUnit.assertTrue( responseText + status, status.isSuccess() );

        XStreamRepresentation re =
            new XStreamRepresentation( XStreamFactory.getXmlXStream(), responseText, MediaType.APPLICATION_XML );
        IndexBrowserTreeViewResponseDTO resourceResponse =
            (IndexBrowserTreeViewResponseDTO) re.getPayload( new IndexBrowserTreeViewResponseDTO() );

        IndexBrowserTreeNode content = resourceResponse.getData();
        for ( TreeNode contentListResource : content.getChildren() )
        {
            AssertJUnit.assertEquals( "nexus1961", contentListResource.getGroupId() );
        }

    }
}
