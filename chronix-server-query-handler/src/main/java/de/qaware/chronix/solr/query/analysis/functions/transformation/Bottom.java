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
package de.qaware.chronix.solr.query.analysis.functions.transformation;

import de.qaware.chronix.solr.query.analysis.functions.ChronixTransformation;
import de.qaware.chronix.solr.query.analysis.functions.FunctionType;
import de.qaware.chronix.solr.query.analysis.functions.FunctionValueMap;
import de.qaware.chronix.solr.query.analysis.functions.math.NElements;
import de.qaware.chronix.timeseries.MetricTimeSeries;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Bottom transformation get the value bottom values
 *
 * @author f.lautenschlager
 */
public final class Bottom implements ChronixTransformation<MetricTimeSeries> {

    private final int value;

    /**
     * Constructs the bottom value values transformation
     *
     * @param value the threshold for the lowest values
     */
    public Bottom(int value) {
        this.value = value;
    }

    @Override
    public void execute(MetricTimeSeries timeSeries, FunctionValueMap functionValueMap) {
        NElements.NElementsResult result = NElements.calc(NElements.NElementsCalculation.BOTTOM, value, timeSeries.getTimestampsAsArray(), timeSeries.getValuesAsArray());

        //remove old time series
        timeSeries.clear();
        timeSeries.addAll(result.getNTimes(), result.getNValues());
        functionValueMap.add(this);
    }


    @Override
    public FunctionType getType() {
        return FunctionType.BOTTOM;
    }

    @Override
    public String[] getArguments() {
        return new String[]{"value=" + value};
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Bottom rhs = (Bottom) obj;
        return new EqualsBuilder()
                .append(this.value, rhs.value)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(value)
                .toHashCode();
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("value", value)
                .toString();
    }
}
