/*******************************************************************************
 * Copyright 2016-2017 ZTE, Inc. and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/**
 * Copyright (C) 2016 ZTE, Inc. and others. All rights reserved. (ZTE)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.onap.msb.apiroute.health;

import org.onap.msb.apiroute.api.DiscoverInfo;
import org.onap.msb.apiroute.wrapper.util.ConfigUtil;
import org.onap.msb.apiroute.wrapper.util.HttpClientUtil;
import org.onap.msb.apiroute.wrapper.util.RouteUtil;

import com.codahale.metrics.health.HealthCheck;

public class ApiRouteHealthCheck extends HealthCheck {


    public ApiRouteHealthCheck() {}

    @Override
    protected Result check() throws Exception {
        DiscoverInfo discoverInfo = ConfigUtil.getInstance().getDiscoverInfo();

        String checkUrl = (new StringBuilder().append("http://").append(discoverInfo.toString())
                        .append(RouteUtil.MSB_CHECK_URL)).toString();

        int resultStatus = HttpClientUtil.httpGetStatus(checkUrl);

        if (resultStatus == 200) {
            return Result.healthy();
        } else {
            return Result.unhealthy("check consul fail:[status]" + resultStatus);
        }


    }
}
