package com.tencent.devops.repository.sdk.github.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.tencent.devops.repository.sdk.common.enums.HttpMethod
import com.tencent.devops.repository.sdk.common.json.JsonIgnorePath
import com.tencent.devops.repository.sdk.github.GithubRequest
import com.tencent.devops.repository.sdk.github.pojo.GithubAction
import com.tencent.devops.repository.sdk.github.pojo.GithubOutput
import com.tencent.devops.repository.sdk.github.response.CheckRunResponse
import org.apache.commons.lang3.StringUtils

data class CreateCheckRunRequest(
    // id或owner/repo
    @JsonIgnorePath
    val repoName: String,
    val name: String,
    @JsonProperty("head_sha")
    val headSha: String,
    @JsonProperty("details_url")
    val detailsUrl: String? = null,
    @JsonProperty("external_id")
    val externalId: String? = null,
    val status: String? = "queued",
    @JsonProperty("started_at")
    val startedAt: String? = null,
    val conclusion: String? = null,
    @JsonProperty("completed_at")
    val completedAt: String? = null,
    val output: GithubOutput? = null,
    val actions: List<GithubAction>? = null
) : GithubRequest<CheckRunResponse>() {
    override fun getHttpMethod() = HttpMethod.POST

    override fun getApiPath() = if (StringUtils.isNumeric(repoName)) {
        "repositories/$repoName/check-runs"
    } else {
        "repos/$repoName/check-runs"
    }
}
