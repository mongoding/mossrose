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
package com.jiuxian.mossrose.job;

import java.io.Serializable;
import java.util.List;

/**
 * 分布式任务，可以将密集任务分解后在集群中执行
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 * @param <T>
 *            The type of the sliced data
 */
public interface DistributedJob<T extends Serializable> extends ExecutorJob<T> {

	Slicer<T> slicer();

	interface Slicer<T> {
		List<T> slice();
	}

}
