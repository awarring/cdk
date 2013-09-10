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

import java.util.Collection;
import java.util.Collections;

import com.cloudera.cdk.morphline.api.Command;
import com.cloudera.cdk.morphline.api.CommandBuilder;
import com.cloudera.cdk.morphline.api.MorphlineContext;
import com.cloudera.cdk.morphline.api.Record;
import com.cloudera.cdk.morphline.base.AbstractCommand;
import com.cloudera.cdk.morphline.base.Fields;
import com.typesafe.config.Config;

/**
 * A command that adds the result of {@link System#currentTimeMillis()} to a given output field.
 * 
 * Typically, a <tt>convertTimestamp</tt> command is subsequently used to convert this timestamp to
 * an application specific output format.
 */
public final class AddCurrentTimeBuilder implements CommandBuilder {

  public static final String FIELD_NAME = "field";
  public static final String PRESERVE_EXISTING_NAME = "preserveExisting";
  
  @Override
  public Collection<String> getNames() {
    return Collections.singletonList("addCurrentTime");
  }

  @Override
  public Command build(Config config, Command parent, Command child, MorphlineContext context) {
    return new AddCurrentTime(config, parent, child, context);
  }
  
  
  ///////////////////////////////////////////////////////////////////////////////
  // Nested classes:
  ///////////////////////////////////////////////////////////////////////////////
  private static final class AddCurrentTime extends AbstractCommand {
    
    private String fieldName;
    private boolean preserveExisting;

    public AddCurrentTime(Config config, Command parent, Command child, MorphlineContext context) { 
      super(config, parent, child, context);
      this.fieldName = getConfigs().getString(config, FIELD_NAME, Fields.TIMESTAMP);
      this.preserveExisting = getConfigs().getBoolean(config, PRESERVE_EXISTING_NAME, true);
      validateArguments();
    }

    @Override
    protected boolean doProcess(Record record) {      
      if (preserveExisting && record.getFields().containsKey(fieldName)) {
        // we must preserve the existing timestamp
      } else {
        record.replaceValues(fieldName, System.currentTimeMillis());
      }
      
      // pass record to next command in chain:
      return super.doProcess(record);
    }

  }

}
