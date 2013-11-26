/*
 * Copyright 2013 Cloudera.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cloudera.cdk.data;

import javax.annotation.concurrent.Immutable;

/**
 * <p>
 * A logical repository (storage system) of {@link RandomAccessDataset}s.
 * </p>
 * <p>
 * {@code RandomAccessDatasetRepository}s are {@link DatasetRepository}s that
 * return the {@link Dataset} sub-interface {@link RandomAccessDataset} from the
 * load, create, and update methods. See {@link DatasetRepository} for more
 * details.
 * </p>
 * <p>
 * Implementations of {@link DatasetRepository} are immutable.
 * </p>
 * 
 * @see RandomAccessDataset
 * @See DatasetRepository
 * @see DatasetDescriptor
 */
@Immutable
public interface RandomAccessDatasetRepository extends DatasetRepository {

  @Override
  <E> RandomAccessDataset<E> load(String name);

  @Override
  <E> RandomAccessDataset<E> create(String name, DatasetDescriptor descriptor);

  @Override
  <E> RandomAccessDataset<E> update(String name, DatasetDescriptor descriptor);
}
