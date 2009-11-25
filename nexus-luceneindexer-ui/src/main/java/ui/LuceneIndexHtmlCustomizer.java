/*
 * Copyright (c) 2009 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package ui;

import java.util.Map;
import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.nexus.plugins.rest.AbstractNexusIndexHtmlCustomizer;
import org.sonatype.nexus.plugins.rest.NexusIndexHtmlCustomizer;

/**
 * @author Toni Menzel
 * @since Nov 25, 2009
 */
@Component( role = NexusIndexHtmlCustomizer.class, hint = "LuceneIndexHtmlCustomizer" )
public class LuceneIndexHtmlCustomizer
    extends AbstractNexusIndexHtmlCustomizer
{

    @Override
    public String getPostHeadContribution( Map<String, Object> ctx )
    {
        String version =
            getVersionFromJarFile( "/META-INF/maven/org.sonatype.nexus/nexus-luceneindexer-ui/pom.properties" );

        return "<script src=\"static/js/nexus-luceneindexer-all.js" + ( version == null ? "" : "?" + version )
               + "\" type=\"text/javascript\" charset=\"utf-8\"></script>";
    }
}
