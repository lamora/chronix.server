/*
 * Copyright (C) 2016 QAware GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package de.qaware.chronix.solr.query.analysis.functions.aggregations

import de.qaware.chronix.solr.query.analysis.functions.FunctionType
import de.qaware.chronix.solr.query.analysis.functions.FunctionValueMap
import de.qaware.chronix.timeseries.MetricTimeSeries
import spock.lang.Specification

/**
 * Unit test for the difference aggregation
 * @author f.lautenschlager
 */
class DifferenceTest extends Specification {
    def "test execute"() {
        given:
        MetricTimeSeries.Builder timeSeries = new MetricTimeSeries.Builder("Difference");
        10.times {
            timeSeries.point(it + 1, it * 10)
        }
        timeSeries.point(0, -1)
        MetricTimeSeries ts = timeSeries.build()

        def analysisResult = new FunctionValueMap(1, 1, 1);

        when:
        new Difference().execute(ts, analysisResult)
        then:
        analysisResult.getAggregationValue(0) == 91d
    }

    def "test execute with negative values"() {
        given:
        MetricTimeSeries.Builder timeSeries = new MetricTimeSeries.Builder("Difference");
        10.times {
            timeSeries.point(it + 1, (it + 1) * -10)
        }
        MetricTimeSeries ts = timeSeries.build()

        def analysisResult = new FunctionValueMap(1, 1, 1);

        when:
        new Difference().execute(ts, analysisResult)
        then:
        analysisResult.getAggregationValue(0) == 90d
    }


    def "test for empty time series"() {
        given:
        def analysisResult = new FunctionValueMap(1, 1, 1);

        when:
        new Difference().execute(new MetricTimeSeries.Builder("Empty").build(), analysisResult)
        then:
        analysisResult.getAggregationValue(0) == Double.NaN
    }

    def "test arguments"() {
        expect:
        new Difference().getArguments().length == 0
    }

    def "test type"() {
        expect:
        new Difference().getType() == FunctionType.DIFF
    }

    def "test equals and hash code"() {
        expect:
        def diff = new Difference();
        !diff.equals(null)
        !diff.equals(new Object())
        diff.equals(diff)
        diff.equals(new Difference())
        new Difference().hashCode() == new Difference().hashCode()
    }
}
