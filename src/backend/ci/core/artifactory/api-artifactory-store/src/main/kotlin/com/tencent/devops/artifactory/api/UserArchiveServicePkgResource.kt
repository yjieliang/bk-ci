/*
 * Tencent is pleased to support the open source community by making BK-CI 蓝鲸持续集成平台 available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * BK-CI 蓝鲸持续集成平台 is licensed under the MIT license.
 *
 * A copy of the MIT License is included in this file.
 *
 *
 * Terms of the MIT License:
 * ---------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.tencent.devops.artifactory.api

import com.tencent.devops.common.api.auth.AUTH_HEADER_USER_ID
import com.tencent.devops.common.api.pojo.Result
import com.tencent.devops.common.web.annotation.BkField
import com.tencent.devops.common.web.constant.BkStyleEnum
import com.tencent.devops.store.pojo.common.enums.ReleaseTypeEnum
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.glassfish.jersey.media.multipart.FormDataContentDisposition
import org.glassfish.jersey.media.multipart.FormDataParam
import java.io.InputStream
import javax.ws.rs.Consumes
import javax.ws.rs.HeaderParam
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Api(tags = ["USER_ARTIFACTORY"], description = "仓库-微扩展")
@Path("/user/artifactories/ext/service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface UserArchiveServicePkgResource {

    @ApiOperation("归档微扩展包")
    @POST
    @Path("/ids/{serviceId}/codes/{serviceCode}/versions/{version}/types/{releaseType}/archive")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun archiveServicePkg(
        @ApiParam("userId", required = true)
        @HeaderParam(AUTH_HEADER_USER_ID)
        userId: String,
        @ApiParam("微扩展ID", required = true)
        @PathParam("serviceId")
        @BkField(patternStyle = BkStyleEnum.ID_STYLE)
        serviceId: String,
        @ApiParam("微扩展标识", required = true)
        @PathParam("serviceCode")
        @BkField(patternStyle = BkStyleEnum.SERVICE_CODE_STYLE)
        serviceCode: String,
        @ApiParam("微扩展版本号", required = true)
        @PathParam("version")
        version: String,
        @ApiParam("发布类型", required = true)
        @PathParam("releaseType")
        releaseType: ReleaseTypeEnum,
        @ApiParam("文件", required = true)
        @FormDataParam("file")
        inputStream: InputStream,
        @FormDataParam("file")
        disposition: FormDataContentDisposition
    ): Result<Boolean>

    @ApiOperation("重新归档微扩展包")
    @POST
    @Path("/ids/{serviceId}/codes/{serviceCode}/versions/{version}/re/archive")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun reArchiveServicePkg(
        @ApiParam("userId", required = true)
        @HeaderParam(AUTH_HEADER_USER_ID)
        userId: String,
        @ApiParam("微扩展ID", required = true)
        @PathParam("serviceId")
        @BkField(patternStyle = BkStyleEnum.ID_STYLE)
        serviceId: String,
        @ApiParam("微扩展标识", required = true)
        @PathParam("serviceCode")
        @BkField(patternStyle = BkStyleEnum.SERVICE_CODE_STYLE)
        serviceCode: String,
        @ApiParam("微扩展版本号", required = true)
        @PathParam("version")
        version: String,
        @ApiParam("文件", required = true)
        @FormDataParam("file")
        inputStream: InputStream,
        @FormDataParam("file")
        disposition: FormDataContentDisposition
    ): Result<Boolean>
}