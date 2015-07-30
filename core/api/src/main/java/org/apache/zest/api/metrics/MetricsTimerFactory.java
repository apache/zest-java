/*
 * Copyright (c) 2012, Niclas Hedhman. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *     You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zest.api.metrics;

import java.util.concurrent.TimeUnit;

/**
 * Create MetricsTimer instances.
 */
public interface MetricsTimerFactory extends MetricsFactory
{
    /**
     * Create a MetricsTimer instance.
     * If the same arguments are given twice, the same instance must be returned.
     *
     * @param origin   The class that instantiate the metric
     * @param name     A human readable, short name of the metric.
     * @param duration the scale unit for this timer's duration metrics
     * @param rate     the scale unit for this timer's rate metrics
     *
     * @return A Metric instance to be used, OR org.apache.zest.spi.metrics.DefaultMetric.NULL if not supported.
     *
     */
    MetricsTimer createTimer( Class<?> origin, String name, TimeUnit duration, TimeUnit rate );
}
