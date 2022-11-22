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

package com.tencent.devops.store.pojo

import com.tencent.devops.store.pojo.enums.DescInputTypeEnum
import io.swagger.annotations.ApiModelProperty

data class ExtServiceFeatureCreateInfo(
    @ApiModelProperty("微扩展code")
    val serviceCode: String,
    @ApiModelProperty("是否为公共微扩展， TRUE：是 FALSE：不是  ")
    val publicFlag: Boolean? = false,
    @ApiModelProperty("是否推荐， TRUE：是 FALSE：不是 ")
    val recommentFlag: Boolean? = false,
    @ApiModelProperty("是否官方认证， TRUE：是 FALSE：不是  ")
    val certificationFlag: Boolean? = false,
    @ApiModelProperty("权重（数值越大代表权重越高）")
    val weight: Int? = null,
    @ApiModelProperty("微扩展可见范围 0：私有 10：登录用户开源")
    val visibilityLevel: Int? = 10,
    @ApiModelProperty("描述录入类型")
    val descInputType: DescInputTypeEnum? = DescInputTypeEnum.MANUAL,
    @ApiModelProperty("代码库hashId")
    val repositoryHashId: String? = null,
    @ApiModelProperty("代码库地址")
    val codeSrc: String? = null,
    @ApiModelProperty("删除标签")
    val deleteFlag: Boolean? = false,
    @ApiModelProperty("添加用户")
    val creatorUser: String,
    @ApiModelProperty("修改用户")
    val modifierUser: String
)