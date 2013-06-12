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
package com.cloudera.data.filesystem;

import com.cloudera.data.DatasetDescriptor;
import com.cloudera.data.PartitionKey;
import com.cloudera.data.PartitionStrategy;
import com.google.common.io.Files;
import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.cloudera.data.filesystem.DatasetTestUtilities.USER_SCHEMA;
import static com.cloudera.data.filesystem.DatasetTestUtilities.readTestUsersInPartition;
import static com.cloudera.data.filesystem.DatasetTestUtilities.writeTestUsers;

public class TestFileSystemDatasetGetPartitionWithUri {

  private FileSystem fileSystem;
  private Path testDirectory;
  private PartitionStrategy partitionStrategy;
  private FileSystemDataset dataset;

  @Before
  public void setUp() throws IOException {
    fileSystem = FileSystem.get(new Configuration());
    testDirectory = new Path(Files.createTempDir().getAbsolutePath());
    partitionStrategy = new PartitionStrategy.Builder()
        .hash("username", "username_part", 2).hash("email", 3).get();

    dataset = new FileSystemDataset.Builder()
        .fileSystem(fileSystem)
        .directory(testDirectory)
        .name("partitioned-users")
        .descriptor(
            new DatasetDescriptor.Builder().schema(USER_SCHEMA)
                .partitionStrategy(partitionStrategy).get()).get();
  }

  @After
  public void tearDown() throws IOException {
    fileSystem.delete(testDirectory, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDifferentFileSystem() throws Exception {
    dataset.getPartition(new URI("hdfs://namenode/"), true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDifferentRootDirectory() throws Exception {
    dataset.getPartition(new Path(testDirectory.getParent(), "bogus").toUri(), true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNonExistentPartitionDirectory() throws Exception {
    dataset.getPartition(new Path(testDirectory, "not_a_partition").toUri(), true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTooManyPartitionDirectories() throws Exception {
    dataset.getPartition(
        new Path(testDirectory, "username_part=1/email=2/extra=3").toUri(), true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidPartitionName() throws Exception {
    dataset.getPartition(new Path(testDirectory, "not_a_partition=1").toUri(), true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingPartitionValue() throws Exception {
    dataset.getPartition(new Path(testDirectory, "username_part").toUri(), true);
  }

  @Test
  public void testWrite() throws Exception {
    FileSystemDataset userPartition = (FileSystemDataset) dataset.getPartition(
        new Path(testDirectory, "username_part=1").toUri(), true);
    PartitionKey key = partitionStrategy.partitionKey(1);
    Assert.assertEquals(key, userPartition.getPartitionKey());

    writeTestUsers(userPartition, 1);
    Assert.assertTrue("Partitioned directory exists",
        fileSystem.exists(new Path(testDirectory, "username_part=1/email=2")));
    Assert.assertEquals(1, readTestUsersInPartition(dataset, key, "email"));
  }

}
