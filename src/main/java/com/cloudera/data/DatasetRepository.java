/**
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
package com.cloudera.data;

import java.io.IOException;

/**
 * <p>
 * A logical repository (storage system) of {@link Dataset}s.
 * </p>
 * <p>
 * Implementations of {@code DatasetRepository} are storage systems that contain
 * zero or more {@link Dataset}s. A repository acts as a factory, as well as a
 * registry, of datasets. Users can {@link #create(String, DatasetDescriptor)} a
 * new {@link Dataset} with a name and schema, or retrieve a handle to an
 * existing dataset, by name, by way of the {@link #get(String)} method. While
 * not expressly forbidden, most repositories are expected to support only a
 * single concrete {@link Dataset} implementation.
 * </p>
 * <p>
 * No guarantees are made as to the durability, reliability, or availability of
 * the underlying storage. That is, a {@code DatasetRepository} could be on
 * disk, in memory, or some combination. See the implementation class for
 * details about the guarantees it provides.
 * </p>
 * 
 * @see Dataset
 * @see DatasetDescriptor
 */
public interface DatasetRepository {

  /**
   * Get the latest version of a named {@link Dataset}. If no dataset with the
   * provided {@code name} exists, an {@link IOException} is thrown.
   * 
   * @param name
   *          The name of the dataset.
   * @throws IOException
   */
  Dataset get(String name) throws IOException;

  /**
   * Create a {@link Dataset} with the supplied {@code descriptor}. Depending on
   * the underlying dataset storage, some schemas types or configurations may
   * not be supported. If an illegal schema is supplied, an exception will be
   * thrown by the implementing class. It is illegal to create a more than one
   * dataset with a given name. If a duplicate name is provided, an exception is
   * thrown.
   * 
   * @param name
   *          The fully qualified dataset name
   * @param descriptor
   *          A descriptor that describes the schema and other properties of the
   *          dataset
   * @return The newly created dataset
   * @throws IOException
   */
  Dataset create(String name, DatasetDescriptor descriptor) throws IOException;

}
