/*
 * Tencent is pleased to support the open source community by making BK-CI 蓝鲸持续集成平台 available.
 *
 * Copyright (C) 2019 Tencent.  All rights reserved.
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

package com.tencent.devops.process.api

import com.tencent.devops.common.api.pojo.Page
import com.tencent.devops.common.api.pojo.Result
import com.tencent.devops.common.web.RestResource
import com.tencent.devops.process.api.user.UserPublicVarGroupResource
import com.tencent.devops.process.pojo.`var`.`do`.PublicVarDO
import com.tencent.devops.process.pojo.`var`.`do`.PublicVarGroupDO
import com.tencent.devops.process.pojo.`var`.`do`.PublicVarReleaseHistoryDO
import com.tencent.devops.process.pojo.`var`.`do`.PublicVarVariableReferenceDO
import com.tencent.devops.process.pojo.`var`.`do`.PublicVerGroupReferenceDO
import com.tencent.devops.process.pojo.`var`.dto.PublicVarGroupDTO
import com.tencent.devops.process.pojo.`var`.vo.PublicVarGroupVO
import com.tencent.devops.process.pojo.`var`.vo.PublicVarGroupYamlStringVO
import com.tencent.devops.process.service.`var`.PublicVarGroupService
import jakarta.ws.rs.core.Response
import org.springframework.beans.factory.annotation.Autowired

@RestResource
class UserPublicVarGroupResourceImpl @Autowired constructor(
    val publicVarGroupService: PublicVarGroupService
) : UserPublicVarGroupResource {

    override fun addGroup(userId: String, projectId: String, publicVarGroup: PublicVarGroupVO): Result<Boolean> {
        return Result(
            publicVarGroupService.addGroup(
                PublicVarGroupDTO(
                    projectId = projectId,
                    userId = userId,
                    publicVarGroup = publicVarGroup
                )
            )
        )
    }

    override fun getGroups(userId: String, projectId: String): Result<Page<PublicVarGroupDO>> {
        return Result(Page(0, 0, 0, emptyList()))
    }

    override fun importGroup(userId: String, projectId: String, yaml: PublicVarGroupYamlStringVO): Result<String> {
        return Result("")
    }

    override fun exportGroup(userId: String, groupName: String): Response {
        TODO("Not yet implemented")
    }

    override fun deleteGroup(userId: String, groupName: String): Result<Boolean> {
        return Result(false)
    }

    override fun getReferences(
        userId: String,
        groupName: String,
        page: Int,
        pageSize: Int
    ): Result<Page<PublicVerGroupReferenceDO>> {
        return Result(Page(0, 0, 0, emptyList()))
    }

    override fun getVariables(userId: String, groupName: String): Result<List<PublicVarDO>> {
        return Result(emptyList())
    }

    override fun getReleaseHistory(
        userId: String,
        groupName: String,
        page: Int,
        pageSize: Int
    ): Result<Page<PublicVarReleaseHistoryDO>> {
        return Result(Page(0, 0, 0, emptyList()))
    }

    override fun getVariableReferences(
        userId: String,
        groupName: String,
        variableName: String,
        page: Int,
        pageSize: Int
    ): Result<Page<PublicVarVariableReferenceDO>> {
        return Result(Page(0, 0, 0, emptyList()))
    }
}
