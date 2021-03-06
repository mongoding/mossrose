/**
 * Copyright 2015-2020 jiuxian.com.
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
package com.jiuxian.mossrose.job.handler;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.jiuxian.mossrose.compute.GridComputer;
import com.jiuxian.mossrose.compute.GridComputer.ComputeFuture;
import com.jiuxian.mossrose.config.MossroseConfig.JobMeta;
import com.jiuxian.mossrose.job.ExecutorJob;
import com.jiuxian.mossrose.job.StreamingJob;
import com.jiuxian.mossrose.job.StreamingJob.Streamer;
import com.jiuxian.mossrose.job.to.ObjectResource;

public class StreamingJobHandler implements JobHandler<StreamingJob<Serializable>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(StreamingJobHandler.class);

	@Override
	public void handle(JobMeta jobMeta, ObjectResource objectResource, GridComputer gridComputer) {
		@SuppressWarnings("unchecked")
		final StreamingJob<Serializable> mJob = (StreamingJob<Serializable>) objectResource.generate();
		final Streamer<Serializable> streamer = mJob.streamer();

		final int concurrency = gridComputer.concurrency() * jobMeta.getThreads();
		LOGGER.info("Cluster concurrency : {}", concurrency);

		final List<ComputeFuture> futures = Lists.newArrayList();
		int cycle = concurrency;
		while (streamer.hasNext()) {
			final Serializable next = streamer.next();
			futures.add(gridComputer.execute(jobMeta.getId(), () -> this.runInCluster(objectResource, next)));

			cycle--;
			if (cycle == 0) {
				futures.forEach(ComputeFuture::join);
				futures.clear();
				cycle = concurrency;
			}
		}
		futures.forEach(ComputeFuture::join);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object runInCluster(ObjectResource objectResource, Serializable data) {
		((ExecutorJob<Serializable>) objectResource.generate()).executor().execute(data);
		return null;
	}

}
