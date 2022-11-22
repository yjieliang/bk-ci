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

package com.tencent.devops.store.service

import com.tencent.devops.common.api.constant.CommonMessageCode
import com.tencent.devops.common.api.exception.ErrorCodeException
import com.tencent.devops.store.constant.StoreMessageCode
import com.tencent.devops.store.dao.ExtServiceDao
import com.tencent.devops.store.pojo.common.enums.ReleaseTypeEnum
import com.tencent.devops.store.pojo.enums.ExtServiceStatusEnum
import com.tencent.devops.store.service.common.StoreCommonService
import com.tencent.devops.store.utils.VersionUtils
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExtServiceCommonService @Autowired constructor(
    private val dslContext: DSLContext,
    private val extServiceDao: ExtServiceDao,
    private val storeCommonService: StoreCommonService
) {

    fun checkEditCondition(serviceCode: String): Boolean {
        // 查询微扩展的最新记录
        val newestServiceRecord = extServiceDao.getNewestServiceByCode(dslContext, serviceCode)
            ?: throw ErrorCodeException(
                errorCode = CommonMessageCode.PARAMETER_IS_INVALID,
                params = arrayOf(serviceCode)
            )
        val serviceFinalStatusList = listOf(
            ExtServiceStatusEnum.AUDIT_REJECT.status.toByte(),
            ExtServiceStatusEnum.RELEASED.status.toByte(),
            ExtServiceStatusEnum.GROUNDING_SUSPENSION.status.toByte(),
            ExtServiceStatusEnum.UNDERCARRIAGED.status.toByte()
        )
        // 判断最近一个微扩展版本的状态，只有处于审核驳回、已发布、上架中止和已下架的状态才允许修改基本信息
        return serviceFinalStatusList.contains(newestServiceRecord.serviceStatus)
    }

    fun validateServiceVersion(
        releaseType: ReleaseTypeEnum,
        serviceCode: String,
        serviceName: String,
        serviceStatus: Byte,
        dbVersion: String,
        requestVersion: String
    ) {
        // 最近的版本处于上架中止状态，重新升级版本号不变
        val cancelFlag = serviceStatus == ExtServiceStatusEnum.GROUNDING_SUSPENSION.status.toByte()
        val requireVersionList =
            if (cancelFlag && releaseType == ReleaseTypeEnum.CANCEL_RE_RELEASE) {
                listOf(dbVersion)
            } else {
                // 历史大版本下的小版本更新模式需获取要更新大版本下的最新版本
                val reqVersion = if (releaseType == ReleaseTypeEnum.HIS_VERSION_UPGRADE) {
                    extServiceDao.getExtService(
                        dslContext = dslContext,
                        serviceCode = serviceCode,
                        version = VersionUtils.convertLatestVersion(requestVersion)
                    )?.version
                } else {
                    null
                }
                storeCommonService.getRequireVersion(
                    reqVersion = reqVersion,
                    dbVersion = dbVersion,
                    releaseType = releaseType
                )
            }
        if (!requireVersionList.contains(requestVersion)) {
            throw ErrorCodeException(
                errorCode = StoreMessageCode.USER_SERVICE_VERSION_IS_INVALID,
                params = arrayOf(requestVersion, requireVersionList.toString())
            )
        }
        if (dbVersion.isNotBlank() && releaseType != ReleaseTypeEnum.NEW) {
            // 判断最近一个微扩展版本的状态，只有处于审核驳回、已发布、上架中止和已下架的状态才允许添加新的版本
            val serviceFinalStatusList = mutableListOf(
                ExtServiceStatusEnum.AUDIT_REJECT.status.toByte(),
                ExtServiceStatusEnum.RELEASED.status.toByte(),
                ExtServiceStatusEnum.GROUNDING_SUSPENSION.status.toByte(),
                ExtServiceStatusEnum.UNDERCARRIAGED.status.toByte()
            )
            if (!serviceFinalStatusList.contains(serviceStatus)) {
                throw ErrorCodeException(
                    errorCode = StoreMessageCode.USER_SERVICE_VERSION_IS_NOT_FINISH,
                    params = arrayOf(serviceName, dbVersion)
                )
            }
        }
    }
}