/*
 * Sonatype Nexus (TM) Open Source Version.
 * Copyright (c) 2008 Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://nexus.sonatype.org/dev/attributions.html
 * This program is licensed to you under Version 3 only of the GNU General Public License as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License Version 3 for more details.
 * You should have received a copy of the GNU General Public License Version 3 along with this program.
 * If not, see http://www.gnu.org/licenses/.
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc.
 * "Sonatype" and "Sonatype Nexus" are trademarks of Sonatype, Inc.
 */

Sonatype.Events.addListener( 'nexusNavigationInit', function( nexusPanel ) {
  nexusPanel.add(
          {
            title: 'Artifact Search',
            id: 'st-nexus-search',
            items: [
              {
                xtype: 'trigger',
                id: 'quick-search--field',
                triggerClass: 'x-form-search-trigger',
                repoPanel: this,
                width: 140,
                listeners: {
                  'specialkey': {
                    fn: function(f, e){
                      if(e.getKey() == e.ENTER){
                        this.onTriggerClick();
                      }
                    }
                  }
                },
                onTriggerClick: function(a,b,c){
                  var v = this.getRawValue();
                  if ( v.length > 0 ) {
                    var panel = Sonatype.view.mainTabPanel.addOrShowTab(
                        'nexus-search', Sonatype.repoServer.SearchPanel, { title: 'Search' } );
                    panel.startQuickSearch( v );
                  }
                }
              },
              {
                title: 'Advanced Search',
                tabCode: Sonatype.repoServer.SearchPanel,
                tabId: 'nexus-search',
                tabTitle: 'Search'
              }
            ]
          }
        );
});