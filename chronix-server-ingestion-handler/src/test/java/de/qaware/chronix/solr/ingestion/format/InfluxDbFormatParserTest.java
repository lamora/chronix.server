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
package de.qaware.chronix.solr.ingestion.format;

import com.google.common.collect.Lists;
import de.qaware.chronix.timeseries.MetricTimeSeries;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class InfluxDbFormatParserTest {
    private static final Instant NOW = Instant.now();
    private InfluxDbFormatParser sut;

    @Before
    public void setUp() throws Exception {
        Clock clock = () -> NOW;
        sut = new InfluxDbFormatParser(clock);
    }

    @Test
    public void testParse() throws Exception {
        try (InputStream stream = InfluxDbFormatParserTest.class.getResourceAsStream("/influxdb.txt")) {
            assertNotNull(stream);
            List<MetricTimeSeries> series = Lists.newArrayList(sut.parse(stream));

            // We should have three metrics (server01, server02, server03)
            assertThat(series.size(), is(3));

            MetricTimeSeries server01 = findWithHost(series, "server01");
            assertThat(server01.getMetric(), is("cpu_load_short"));
            assertThat(server01.getTimestamps().size(), is(1));
            assertThat(server01.getTimestamps().get(0), is(NOW.toEpochMilli()));
            assertThat(server01.getValues().size(), is(1));
            assertThat(server01.getValues().get(0), is(0.67));
            assertThat(server01.getAttributesReference().get("host"), is("server01"));

            MetricTimeSeries server02 = findWithHost(series, "server02");
            assertThat(server02.getMetric(), is("cpu_load_short"));
            assertThat(server02.getTimestamps().size(), is(1));
            assertThat(server02.getTimestamps().get(0), is(1422568543702900L));
            assertThat(server02.getValues().size(), is(1));
            assertThat(server02.getValues().get(0), is(0.55));
            assertThat(server02.getAttributesReference().get("host"), is("server02"));

            MetricTimeSeries server03 = findWithHost(series, "server03");
            assertThat(server03.getMetric(), is("cpu_load_long"));
            assertThat(server03.getTimestamps().size(), is(2));
            assertThat(server03.getTimestamps().get(0), is(1422568543702900L));
            assertThat(server03.getTimestamps().get(1), is(1422568544702900L));
            assertThat(server03.getValues().size(), is(2));
            assertThat(server03.getValues().get(0), is(2.0));
            assertThat(server03.getValues().get(1), is(3.0));
            assertThat(server03.getAttributesReference().get("host"), is("server03"));
            assertThat(server03.getAttributesReference().get("direction"), is("in"));
            assertThat(server03.getAttributesReference().get("region"), is("us-west"));
        }
    }

    private MetricTimeSeries findWithHost(List<MetricTimeSeries> series, String host) {
        for (MetricTimeSeries serie : series) {
            if (serie.getAttributesReference().get("host").equals(host)) {
                return serie;
            }
        }

        throw new IllegalStateException("Series doesn't contain data for server " + host);
    }
}