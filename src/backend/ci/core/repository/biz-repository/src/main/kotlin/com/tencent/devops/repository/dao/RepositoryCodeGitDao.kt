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

package com.tencent.devops.repository.dao

import com.tencent.devops.common.api.enums.ScmType
import com.tencent.devops.model.repository.tables.TRepository
import com.tencent.devops.model.repository.tables.TRepositoryCodeGit
import com.tencent.devops.model.repository.tables.records.TRepositoryCodeGitRecord
import com.tencent.devops.repository.pojo.RepoOauthRefVo
import com.tencent.devops.repository.pojo.UpdateRepositoryInfoRequest
import com.tencent.devops.repository.pojo.enums.RepoAuthType
import org.jooq.DSLContext
import org.jooq.Result
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import jakarta.ws.rs.NotFoundException

@Suppress("ALL")
@Repository
class RepositoryCodeGitDao {
    fun create(
        dslContext: DSLContext,
        repositoryId: Long,
        projectName: String,
        userName: String,
        credentialId: String,
        authType: RepoAuthType?,
        gitProjectId: Long
    ) {
        val now = LocalDateTime.now()
        with(TRepositoryCodeGit.T_REPOSITORY_CODE_GIT) {
            dslContext.insertInto(
                this,
                REPOSITORY_ID,
                PROJECT_NAME,
                USER_NAME,
                CREDENTIAL_ID,
                CREATED_TIME,
                UPDATED_TIME,
                AUTH_TYPE,
                GIT_PROJECT_ID
            )
                .values(
                    repositoryId,
                    projectName,
                    userName,
                    credentialId,
                    now,
                    now,
                    authType?.name,
                    gitProjectId
                ).execute()
        }
    }

    fun getOrNull(dslContext: DSLContext, repositoryId: Long): TRepositoryCodeGitRecord? {
        with(TRepositoryCodeGit.T_REPOSITORY_CODE_GIT) {
            return dslContext.selectFrom(this)
                .where(REPOSITORY_ID.eq(repositoryId))
                .fetchOne()
        }
    }

    fun get(dslContext: DSLContext, repositoryId: Long): TRepositoryCodeGitRecord {
        return getOrNull(dslContext, repositoryId) ?: throw NotFoundException("Git repository does not exists")
    }

    fun list(dslContext: DSLContext, repositoryIds: Set<Long>): Result<TRepositoryCodeGitRecord>? {
        with(TRepositoryCodeGit.T_REPOSITORY_CODE_GIT) {
            return dslContext.selectFrom(this)
                .where(REPOSITORY_ID.`in`(repositoryIds))
                .fetch()
        }
    }

    fun edit(
        dslContext: DSLContext,
        repositoryId: Long,
        projectName: String,
        userName: String,
        credentialId: String,
        authType: RepoAuthType?,
        gitProjectId: Long?
    ) {
        val now = LocalDateTime.now()
        with(TRepositoryCodeGit.T_REPOSITORY_CODE_GIT) {
            val updateSetStep = dslContext.update(this)
                .set(PROJECT_NAME, projectName)
                .set(USER_NAME, userName)
                .set(CREDENTIAL_ID, credentialId)
                .set(UPDATED_TIME, now)
                .set(AUTH_TYPE, authType?.name ?: RepoAuthType.SSH.name)
            if (gitProjectId != null) {
                updateSetStep.set(GIT_PROJECT_ID, gitProjectId)
            }
            updateSetStep.where(REPOSITORY_ID.eq(repositoryId))
                .execute()
        }
    }

    fun updateRepositoryInfo(
        dslContext: DSLContext,
        repositoryId: Long,
        updateRepositoryInfoRequest: UpdateRepositoryInfoRequest
    ) {
        with(TRepositoryCodeGit.T_REPOSITORY_CODE_GIT) {
            val baseStep = dslContext.update(this)
            if (!updateRepositoryInfoRequest.projectName.isNullOrEmpty()) {
                baseStep.set(PROJECT_NAME, updateRepositoryInfoRequest.projectName)
            }
            if (!updateRepositoryInfoRequest.userId.isNullOrEmpty()) {
                baseStep.set(USER_NAME, updateRepositoryInfoRequest.userId)
            }
            if (!updateRepositoryInfoRequest.credentialId.isNullOrEmpty()) {
                baseStep.set(CREDENTIAL_ID, updateRepositoryInfoRequest.credentialId)
            }
            if (null != updateRepositoryInfoRequest.authType) {
                baseStep.set(AUTH_TYPE, updateRepositoryInfoRequest.authType!!.name)
            }
            baseStep.set(UPDATED_TIME, LocalDateTime.now())
                .where(REPOSITORY_ID.eq(repositoryId))
                .execute()
        }
    }

    /**
     * 分页查询
     */
    fun getAllRepo(
        dslContext: DSLContext,
        limit: Int,
        offset: Int
    ): Result<TRepositoryCodeGitRecord>? {
        with(TRepositoryCodeGit.T_REPOSITORY_CODE_GIT) {
            return dslContext.selectFrom(this)
                .orderBy(CREATED_TIME.desc())
                .limit(limit).offset(offset)
                .fetch()
        }
    }

    fun updateGitProjectId(
        dslContext: DSLContext,
        id: Long,
        gitProjectId: Long
    ) {
        with(TRepositoryCodeGit.T_REPOSITORY_CODE_GIT) {
            val conditions = mutableListOf(
                REPOSITORY_ID.eq(id),
                GIT_PROJECT_ID.le(0)
            )
            dslContext.update(this)
                .set(GIT_PROJECT_ID, gitProjectId)
                .where(conditions)
                .execute()
        }
    }

    fun listByGitProjectId(
        dslContext: DSLContext,
        gitProjectId: Long
    ): List<TRepositoryCodeGitRecord> {
        return with(TRepositoryCodeGit.T_REPOSITORY_CODE_GIT) {
            dslContext.selectFrom(this)
                .where(GIT_PROJECT_ID.eq(gitProjectId))
                .fetch()
        }
    }

    fun countOauthRepo(
        dslContext: DSLContext,
        userId: String
    ): Long {
        val t1 = TRepository.T_REPOSITORY
        val t2 = TRepositoryCodeGit.T_REPOSITORY_CODE_GIT
        return dslContext.selectCount()
            .from(t1)
            .leftJoin(t2)
            .on(t1.REPOSITORY_ID.eq(t2.REPOSITORY_ID))
            .where(
                listOf(
                    t1.IS_DELETED.eq(false),
                    t1.TYPE.eq(ScmType.CODE_GIT.name),
                    t2.AUTH_TYPE.eq(RepoAuthType.OAUTH.name),
                    t2.USER_NAME.eq(userId)
                )
            )
            .fetchOne(0, Long::class.java)!!
    }

    fun listOauthRepo(
        dslContext: DSLContext,
        userId: String,
        limit: Int,
        offset: Int
    ): List<RepoOauthRefVo> {
        val t1 = TRepository.T_REPOSITORY
        val t2 = TRepositoryCodeGit.T_REPOSITORY_CODE_GIT
        return dslContext.select(t1.ALIAS_NAME, t1.URL, t1.PROJECT_ID, t1.REPOSITORY_HASH_ID)
            .from(t1)
            .leftJoin(t2)
            .on(t1.REPOSITORY_ID.eq(t2.REPOSITORY_ID))
            .where(
                listOf(
                    t1.IS_DELETED.eq(false),
                    t1.TYPE.eq(ScmType.CODE_GIT.name),
                    t2.AUTH_TYPE.eq(RepoAuthType.OAUTH.name),
                    t2.USER_NAME.eq(userId)
                )
            )
            .limit(limit)
            .offset(offset)
            .fetch()
            .map {
                RepoOauthRefVo(
                    aliasName = it.get(0).toString(),
                    url = it.get(1).toString(),
                    projectId = it.get(2).toString(),
                    hashId = it.get(3).toString()
                )
            }
    }
}
