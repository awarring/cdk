/*
 * Copyright 2013 Cloudera Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cloudera.cdk.morphline.stdlib;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;

import com.cloudera.cdk.morphline.api.Command;
import com.cloudera.cdk.morphline.api.CommandBuilder;
import com.cloudera.cdk.morphline.api.MorphlineContext;
import com.cloudera.cdk.morphline.api.Record;
import com.cloudera.cdk.morphline.base.AbstractCommand;
import com.typesafe.config.Config;

/**
 * A command that extracts subcomponents from the URIs contained in the given input field and
 * appends them to output fields with the given prefix, namely scheme, authority, host, port, path,
 * query, fragment, schemeSpecificPart, userInfo.
 */
public final class ExtractURIComponentsBuilder implements CommandBuilder {

  @Override
  public Collection<String> getNames() {
    return Collections.singletonList("extractURIComponents");
  }

  @Override
  public Command build(Config config, Command parent, Command child, MorphlineContext context) {
    return new ExtractURIComponents(config, parent, child, context);
  }
  
  
  ///////////////////////////////////////////////////////////////////////////////
  // Nested classes:
  ///////////////////////////////////////////////////////////////////////////////
  private static final class ExtractURIComponents extends AbstractCommand {

    private final String inputFieldName;
    private final String outputFieldPrefix;
    
    public ExtractURIComponents(Config config, Command parent, Command child, MorphlineContext context) {
      super(config, parent, child, context);      
      this.inputFieldName = getConfigs().getString(config, "inputField");
      this.outputFieldPrefix = getConfigs().getString(config, "outputFieldPrefix", "");
      validateArguments();
    }
        
    @Override
    protected boolean doProcess(Record record) {
      for (Object uriObj : record.get(inputFieldName)) {
        URI uri;
        try {
          uri = new URI(uriObj.toString());
        } catch (URISyntaxException e) {
          continue;
        }
        addValue(record, "scheme", uri.getScheme());
        addValue(record, "authority", uri.getAuthority());
        addValue(record, "path", uri.getPath());
        addValue(record, "query", uri.getQuery());
        addValue(record, "fragment", uri.getFragment());
        addValue(record, "host", uri.getHost());
        addValue(record, "port", uri.getPort());
        addValue(record, "schemeSpecificPart", uri.getSchemeSpecificPart());
        addValue(record, "userInfo", uri.getUserInfo());
      }
      
      // pass record to next command in chain:
      return super.doProcess(record);
    }
    
    private void addValue(Record record, String name, Object value) {
      if (value != null) {
        record.put(outputFieldPrefix + name, value);
      }
    }
  }
  
}
