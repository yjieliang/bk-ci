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

package com.tencent.devops.store.common.dao

import com.tencent.devops.model.store.tables.TClassify
import com.tencent.devops.model.store.tables.TStoreBase
import com.tencent.devops.model.store.tables.TStoreBaseFeature
import com.tencent.devops.model.store.tables.TStoreCategoryRel
import com.tencent.devops.model.store.tables.TStoreLabelRel
import com.tencent.devops.model.store.tables.TStoreProjectRel
import com.tencent.devops.model.store.tables.TStoreStatisticsTotal
import com.tencent.devops.model.store.tables.records.TClassifyRecord
import com.tencent.devops.store.pojo.common.enums.RdTypeEnum
import com.tencent.devops.store.pojo.common.enums.StoreSortTypeEnum
import com.tencent.devops.store.pojo.common.enums.StoreStatusEnum
import com.tencent.devops.store.pojo.common.enums.StoreTypeEnum
import java.math.BigDecimal
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Record1
import org.jooq.Result
import org.jooq.SelectConditionStep
import org.jooq.SelectJoinStep
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class MarketStoreQueryDao {

    /**
     * 研发商店搜索结果，总数
     */
    fun count(
        dslContext: DSLContext,
        storeType: StoreTypeEnum,
        projectCode: String?,
        keyword: String?,
        classifyId: String?,
        labelId: String?,
        categoryId: String?,
        score: Int?,
        recommendFlag: Boolean?,
        rdType: RdTypeEnum?,
        queryProjectComponentFlag: Boolean
    ): Int {
        val tStoreBase = TStoreBase.T_STORE_BASE
        val tStoreBaseFeature = TStoreBaseFeature.T_STORE_BASE_FEATURE
        val baseStep = dslContext.select(DSL.countDistinct(tStoreBase.ID)).from(tStoreBase)
            .leftJoin(tStoreBaseFeature)
            .on(tStoreBase.STORE_CODE.eq(tStoreBaseFeature.STORE_CODE)
                .and(tStoreBase.STORE_TYPE.eq(tStoreBaseFeature.STORE_TYPE)))
        val conditions = formatConditions(
            dslContext = dslContext,
            storeType = storeType,
            projectCode = projectCode,
            classifyId = classifyId,
            labelId = labelId,
            categoryId = categoryId,
            recommendFlag = recommendFlag,
            rdType = rdType,
            score = score,
            queryProjectComponentFlag = queryProjectComponentFlag,
            baseStep = baseStep
        )
        return baseStep.where(conditions).fetchOne(0, Int::class.java) ?: 0
    }

    /**
     * 研发商店搜索结果列表
     */
    fun list(
        dslContext: DSLContext,
        storeType: StoreTypeEnum,
        projectCode: String?,
        keyword: String?,
        classifyId: String?,
        labelId: String?,
        categoryId: String?,
        score: Int?,
        recommendFlag: Boolean?,
        rdType: RdTypeEnum?,
        queryProjectComponentFlag: Boolean,
        sortType: StoreSortTypeEnum?,
        page: Int?,
        pageSize: Int?
    ): Result<out Record>? {
        val tStoreBase = TStoreBase.T_STORE_BASE
        val baseStep = createBaseStep(dslContext)
        val conditions = formatConditions(
            dslContext = dslContext,
            storeType = storeType,
            projectCode = projectCode,
            classifyId = classifyId,
            labelId = labelId,
            categoryId = categoryId,
            recommendFlag = recommendFlag,
            rdType = rdType,
            score = score,
            queryProjectComponentFlag = queryProjectComponentFlag,
            baseStep = baseStep
        )
        if (!keyword.isNullOrEmpty()) {
            conditions.add(
                tStoreBase.NAME.contains(keyword)
                    .or(tStoreBase.SUMMARY.contains(keyword))
                    .or(tStoreBase.STORE_CODE.contains(keyword))
            )
        }
        if (null != sortType) {
            val flag = sortType == StoreSortTypeEnum.DOWNLOAD_COUNT
            if (flag && score == null) {
                val tas = TStoreStatisticsTotal.T_STORE_STATISTICS_TOTAL
                val t =
                    dslContext.select(
                        tas.STORE_CODE,
                        tas.DOWNLOADS.`as`(StoreSortTypeEnum.DOWNLOAD_COUNT.name)
                    )
                        .from(tas).where(tas.STORE_TYPE.eq(storeType.type.toByte())).asTable("t")
                baseStep.leftJoin(t).on(tStoreBase.STORE_CODE.eq(t.field("STORE_CODE", String::class.java)))
            }

            val realSortType = if (flag) { DSL.field(sortType.name) } else { tStoreBase.field(sortType.name) }
            baseStep.where(conditions).orderBy(realSortType)
        } else {
            baseStep.where(conditions)
        }
        return if (null != page && null != pageSize) {
            baseStep.limit((page - 1) * pageSize, pageSize).fetch()
        } else {
            baseStep.fetch()
        }
    }

    private fun createBaseStep(dslContext: DSLContext): SelectJoinStep<out Record> {
        val tStoreBase = TStoreBase.T_STORE_BASE
        val tStoreBaseFeature = TStoreBaseFeature.T_STORE_BASE_FEATURE

        return dslContext.select(
            tStoreBase.ID,
            tStoreBase.STORE_CODE,
            tStoreBase.STORE_TYPE,
            tStoreBase.NAME,
            tStoreBase.VERSION,
            tStoreBase.DOCS_LINK,
            tStoreBase.DESCRIPTION,
            tStoreBase.SUMMARY,
            tStoreBase.LOGO_URL,
            tStoreBase.PUBLISHER,
            tStoreBase.PUB_TIME,
            tStoreBase.MODIFIER,
            tStoreBase.UPDATE_TIME,
            tStoreBaseFeature.RECOMMEND_FLAG,
            tStoreBaseFeature.RD_TYPE,
            tStoreBaseFeature.PUBLIC_FLAG,
        ).from(tStoreBase)
            .leftJoin(tStoreBaseFeature)
            .on(tStoreBase.STORE_CODE.eq(tStoreBaseFeature.STORE_CODE)
                .and(tStoreBase.STORE_TYPE.eq(tStoreBaseFeature.STORE_TYPE)))
    }

    private fun formatConditions(
        dslContext: DSLContext,
        storeType: StoreTypeEnum,
        projectCode: String?,
        classifyId: String?,
        labelId: String?,
        categoryId: String?,
        recommendFlag: Boolean?,
        rdType: RdTypeEnum?,
        score: Int?,
        queryProjectComponentFlag: Boolean,
        baseStep: SelectJoinStep<out Record>
    ): MutableList<Condition> {
        val tStoreBase = TStoreBase.T_STORE_BASE
        val tStoreBaseFeature = TStoreBaseFeature.T_STORE_BASE_FEATURE
        val conditions = setStoreVisibleCondition(tStoreBase, storeType)
        conditions.add(tStoreBase.STORE_TYPE.eq(storeType.type.toByte()))
        // 缩减查询范围
        if (queryProjectComponentFlag || !classifyId.isNullOrBlank() || !labelId.isNullOrBlank()) {
            conditions.add(tStoreBase.STORE_CODE.`in`(getStoreCodesByCondition(
                dslContext = dslContext,
                storeType = storeType.type.toByte(),
                projectCode = projectCode,
                classifyId = classifyId,
                labelId = labelId,
                categoryId = categoryId,
                queryProjectComponentFlag = queryProjectComponentFlag
            )))
        }
        recommendFlag?.let {
            conditions.add(tStoreBaseFeature.RECOMMEND_FLAG.eq(it))
        }
        rdType?.let {
            conditions.add(tStoreBaseFeature.RD_TYPE.eq(it.name))
        }
        if (score != null) {
            val tas = TStoreStatisticsTotal.T_STORE_STATISTICS_TOTAL
            val t = dslContext.select(
                tas.STORE_CODE,
                tas.STORE_TYPE,
                tas.DOWNLOADS.`as`(StoreSortTypeEnum.DOWNLOAD_COUNT.name),
                tas.SCORE_AVERAGE
            ).from(tas).asTable("t")
            baseStep.leftJoin(t).on(tStoreBase.STORE_CODE.eq(t.field("STORE_CODE", String::class.java)))
            conditions.add(
                t.field("SCORE_AVERAGE", BigDecimal::class.java)!!.ge(BigDecimal.valueOf(score.toLong()))
            )
            conditions.add(t.field("STORE_TYPE", Byte::class.java)!!.eq(storeType.type.toByte()))
        }
        return conditions
    }

    private fun getStoreCodesByCondition(
        dslContext: DSLContext,
        storeType: Byte,
        projectCode: String?,
        classifyId: String?,
        labelId: String?,
        categoryId: String?,
        queryProjectComponentFlag: Boolean
    ): SelectConditionStep<Record1<String>> {
        val tStoreBase = TStoreBase.T_STORE_BASE
        val tStoreProjectRel = TStoreProjectRel.T_STORE_PROJECT_REL
        val tStoreLabelRel = TStoreLabelRel.T_STORE_LABEL_REL
        val tStoreCategoryRel = TStoreCategoryRel.T_STORE_CATEGORY_REL
        val tClassify = TClassify.T_CLASSIFY

        val conditions = mutableListOf<Condition>().apply {
            add(tStoreBase.STORE_TYPE.eq(storeType))

            projectCode?.let {
                if (queryProjectComponentFlag) {
                    add(tStoreProjectRel.PROJECT_CODE.eq(it))
                }
            }

            classifyId?.let {
                add(tClassify.ID.eq(it))
            }

            labelId?.let {
                add(tStoreLabelRel.LABEL_ID.eq(it))
            }

            categoryId?.let {
                add(tStoreCategoryRel.CATEGORY_ID.eq(it))
            }
        }

        return dslContext.select(tStoreBase.STORE_CODE)
            .from(tStoreBase)
            .leftJoin(tStoreProjectRel).on(tStoreBase.STORE_CODE.eq(tStoreProjectRel.STORE_CODE))
            .leftJoin(tClassify).on(tStoreBase.CLASSIFY_ID.eq(tClassify.ID))
            .leftJoin(tStoreLabelRel).on(tStoreBase.ID.eq(tStoreLabelRel.STORE_ID))
            .leftJoin(tStoreCategoryRel).on(tStoreBase.ID.eq(tStoreCategoryRel.STORE_ID))
            .where(conditions)
    }

    fun setStoreVisibleCondition(tStoreBase: TStoreBase, storeType: StoreTypeEnum): MutableList<Condition> {
        val conditions = mutableListOf<Condition>()
        conditions.add(tStoreBase.STORE_TYPE.eq(storeType.type.toByte()))
        conditions.add(tStoreBase.STATUS.eq(StoreStatusEnum.RELEASED.name)) // 已发布的
        conditions.add(tStoreBase.LATEST_FLAG.eq(true)) // 最新版本
        return conditions
    }

    fun getAllStoreClassify(dslContext: DSLContext, storeType: StoreTypeEnum): Result<TClassifyRecord> {
        with(TClassify.T_CLASSIFY) {
            val tStoreBase = TStoreBase.T_STORE_BASE
            val conditions = setStoreVisibleCondition(tStoreBase, storeType)
            conditions.add(0, tStoreBase.CLASSIFY_ID.eq(this.ID))
            return dslContext.selectFrom(this)
                .where(ID.`in`(
                    dslContext.select(tStoreBase.CLASSIFY_ID)
                        .from(tStoreBase)
                        .where(conditions).groupBy(tStoreBase.CLASSIFY_ID))
                )
                .and(TYPE.eq(storeType.type.toByte()))
                .orderBy(WEIGHT.desc()).fetch()
        }
    }
}