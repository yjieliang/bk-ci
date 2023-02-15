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

package com.tencent.devops.store.service.atom.impl

import com.tencent.devops.common.api.exception.ErrorCodeException
import com.tencent.devops.common.api.pojo.Result
import com.tencent.devops.store.constant.StoreMessageCode
import com.tencent.devops.store.dao.common.StoreErrorCodeInfoDao
import com.tencent.devops.store.pojo.common.StoreErrorCodeInfo
import com.tencent.devops.store.pojo.common.enums.ErrorCodeTypeEnum
import com.tencent.devops.store.pojo.common.enums.StoreTypeEnum
import com.tencent.devops.store.service.atom.MarketAtomErrorCodeService
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MarketAtomErrorCodeServiceImpl @Autowired constructor(
    private val dslContext: DSLContext,
    private val storeErrorCodeInfoDao: StoreErrorCodeInfoDao
) : MarketAtomErrorCodeService {

    @Value("\${store.defaultAtomErrorCodeLength:6}")
    private var defaultAtomErrorCodeLength: Int = 6

    @Value("\${store.defaultAtomErrorCodePrefix:8}")
    private lateinit var defaultAtomErrorCodePrefix: String

    override fun createErrorCode(userId: String, storeErrorCodeInfo: StoreErrorCodeInfo): Result<Boolean> {
        checkErrorCode(
            storeErrorCodeInfo.errorCodeType,
            storeErrorCodeInfo.errorCodeInfos.map { "${it.errorCode}" }
        )
        storeErrorCodeInfoDao.batchUpdateErrorCodeInfo(dslContext, userId, storeErrorCodeInfo)
        return Result(true)
    }

    override fun updateErrorCode(userId: String, storeErrorCodeInfo: StoreErrorCodeInfo): Result<Boolean> {
        checkErrorCode(
            storeErrorCodeInfo.errorCodeType,
            storeErrorCodeInfo.errorCodeInfos.map { "${it.errorCode}" }
        )
        val errorCodeInfos = storeErrorCodeInfoDao.getStoreErrorCodeInfo(
            dslContext = dslContext,
            storeCode = storeErrorCodeInfo.storeCode,
            storeType = storeErrorCodeInfo.storeType,
            errorCodeType = storeErrorCodeInfo.errorCodeType
        ).subtract(storeErrorCodeInfo.errorCodeInfos.toSet()).toList()
        if (errorCodeInfos.isNotEmpty()) {
            storeErrorCodeInfoDao.batchDeleteErrorCodeInfo(
                dslContext = dslContext,
                storeType = storeErrorCodeInfo.storeType,
                storeCode = storeErrorCodeInfo.storeCode,
                errorCodeType = storeErrorCodeInfo.errorCodeType,
                errorCodes = errorCodeInfos.map { it.errorCode }
            )
        }
        storeErrorCodeInfoDao.batchUpdateErrorCodeInfo(dslContext, userId, storeErrorCodeInfo)
        return Result(true)
    }

    private fun checkErrorCode(errorCodeType: ErrorCodeTypeEnum, errorCodeInfos: List<String>) {
        val invalidErrorCodes = mutableListOf<String>()
        errorCodeInfos.forEach {
            val errorCode = it
            if (errorCode.length != 6) invalidErrorCodes.add(errorCode)
            val errorCodePrefix = errorCode.substring(0, 3)
            when (errorCodeType) {
                ErrorCodeTypeEnum.ATOM -> {
                     if (!errorCodePrefix.startsWith("8")) {
                         invalidErrorCodes.add(errorCode)
                     }
                }
                ErrorCodeTypeEnum.GENERAL -> {
                    if (!errorCodePrefix.startsWith("100")) {
                        invalidErrorCodes.add(errorCode)
                    }
                }
                ErrorCodeTypeEnum.PLATFORM -> {
                    if (errorCodePrefix.toInt() !in 101..599) {
                        invalidErrorCodes.add(errorCode)
                    }
                }
            }
        }
        if (invalidErrorCodes.isNotEmpty()) {
            throw ErrorCodeException(
                errorCode = StoreMessageCode.USER_ERROR_CODE_INVALID,
                params = arrayOf("[${invalidErrorCodes.joinToString(",")}]")
            )
        }
    }

    override fun isComplianceErrorCode(
        storeCode: String,
        storeType: StoreTypeEnum,
        errorCode: String,
        errorCodeType: ErrorCodeTypeEnum
    ): Boolean {
        try {
            checkErrorCode(errorCodeType, listOf(errorCode))
        } catch (e: ErrorCodeException) {
            return false
        }
        return storeErrorCodeInfoDao.getAtomErrorCode(
            dslContext = dslContext,
            storeCode = storeCode,
            storeType = storeType,
            errorCode = errorCode.toInt(),
            errorCodeType = errorCodeType
        ).isNotEmpty
    }
}
